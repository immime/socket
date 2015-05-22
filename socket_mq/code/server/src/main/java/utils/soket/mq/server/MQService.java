package utils.soket.mq.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socket.netty.msg.AbsMsg;
import utils.soket.mq.mqutils.BytesMessageBeanUtil;

public class MQService {
	
	private static final Logger logger = LoggerFactory.getLogger(MQService.class);

	private volatile static MQService obj = null;

	public static MQService getInstance() {
		if (obj == null) {
			synchronized (MQService.class) {
				if (obj == null) {
					obj = new MQService();
				}
			}
			obj = new MQService();
		}
		return obj;
	}

	private Session session;

	private HashMap<String, MessageConsumer> consumerMap;
	private HashMap<String, MessageProducer> producerMap;;
	private PooledConnection pooledconn;
	private PooledConnectionFactory pooledConnectionFactory;

	/**
	 * 
	 * initServer:
	 * 服务端的初始化 客户端端和中心服务端的差别在余中心端的服务连接多个Queue
	 *
	 * @author sid
	 * @param listener
	 * @throws JMSException
	 */
	public PooledConnection initServer(MessageListener listener) {
		try {
			Properties server = MQConstants.SERVER;
			String[] es = server.getProperty("mq.codes").split(",");
			List<String> list_code = new ArrayList<>();
			for (String c : es) {
				String code = server.getProperty("mq.code." + c);
				if (code != null) {
					list_code.add(code);
				}
			}
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					server.getProperty("mq.user"), server.getProperty("mq.password"), server.getProperty("mq.url"));
			pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
			pooledConnectionFactory.setIdleTimeout(30);
			//池中借出的对象的最大数目
			pooledConnectionFactory.setMaxConnections(100);  
			pooledConnectionFactory.setMaximumActiveSessionPerConnection(50);        
		    //后台对象清理时，休眠时间超过了3000毫秒的对象为过期  
			pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(3000);

			pooledconn = (PooledConnection) pooledConnectionFactory.createConnection();
			session = pooledconn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumerMap = new HashMap<>();
			producerMap = new HashMap<>();
			for (String code : list_code) {
				Queue inQueue = session.createQueue(code + ".client");//服务端的发送队列
				Queue outQueue = session.createQueue(code + ".server");//服务端的接收队列
				MessageConsumer c = session.createConsumer(outQueue);
				consumerMap.put(code, c);
				producerMap.put(code, session.createProducer(inQueue));
				c.setMessageListener(listener);
			}
			pooledconn.start();
			logger.info("服务器连接ActiveMQ成功");

			logger.info("Active ActiveSessions:"+ pooledconn.getNumActiveSessions());
			logger.info("Active Sessions" + pooledconn.getNumSessions());
			logger.info("Active  Idle Sessions" + pooledconn.getNumtIdleSessions());
			return pooledconn;
		} catch (Exception e) {
			logger.error("服务器连接ActiveMQ失败："+e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * sendServerMsg:服务器发送消息
	 *
	 * @author sid
	 * @param code
	 * @param msg
	 */
	public void sendServerMsg(final String code,final AbsMsg msg) {
		try {
			Thread t = new Thread() {
				@Override
				public void run() {
					Session sess = null;
					MessageProducer p =null;
					try {
						sess = getPooledConnection().createSession(false,Session.AUTO_ACKNOWLEDGE);
						Queue outQueue = sess.createQueue(code + ".server");// 发送队列
						p = sess.createProducer(outQueue);
						BytesMessage bmsg = sess.createBytesMessage();
						bmsg = BytesMessageBeanUtil.bean2BytesMessage(msg, bmsg);
						p.send(bmsg);
					} catch (Exception e) {
						logger.error("服务器发送消息失败："+e);
						e.printStackTrace();
					}finally{
						try {
							if(sess!=null)
							sess.close();
							if(p!=null)
							p.close();
						} catch (JMSException e) {
							logger.error("服务器关闭session和生产者失败："+e);
							e.printStackTrace();
						}
					}
				}
			};
			t.run();
		} catch (Exception e) {
			logger.error("发送消息失败："+e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * getPooledConnection:获取连接池的引用
	 *
	 * @author sid
	 * @return
	 */
	public PooledConnection getPooledConnection(){
		if(this.pooledconn==null){
			return initServer(new ServerMessageListener());
		}else{
			return this.pooledconn;
		}
	}

	/**
	 * 
	 * close:关闭连接
	 *
	 * @author sid
	 */
	public void close() {
		try {
			Set<Entry<String, MessageConsumer>> cs = consumerMap.entrySet();
			for (Iterator<Entry<String, MessageConsumer>> iterator = cs.iterator(); iterator.hasNext();) {
				Entry<String, MessageConsumer> entry = (Entry<String, MessageConsumer>) iterator.next();
				entry.getValue().close();
			}
			Set<Entry<String, MessageProducer>> ps = producerMap.entrySet();

			for (Iterator<Entry<String, MessageProducer>> iterator = ps.iterator(); iterator.hasNext();) {
				Entry<String, MessageProducer> entry = (Entry<String, MessageProducer>) iterator.next();
				entry.getValue().close();
			}
			session.close();
		} catch (JMSException e) {
			logger.error("close()", e);
		}
	}
}
