package utils.soket.mq.client;

import java.io.IOException;

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
	private MessageConsumer consumer;

	private PooledConnection pooledconn;
	private PooledConnectionFactory pooledConnectionFactory;

	/**
	 * 
	 * initClient:
	 * 客户端使用的初始化。客户端和中心服务端的差别在余中心端的服务连接多个Queue
	 *
	 * @author sid
	 * @param listener
	 * @throws JMSException
	 * @throws IOException
	 */
	public PooledConnection initClient(MessageListener listener) {
		 try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					MQConstants.CLIENT_USER, MQConstants.CLIENT_PASSWORD, MQConstants.CLIENT_URL);
			pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
			pooledconn = (PooledConnection) pooledConnectionFactory.createConnection();

			session = pooledconn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue inQueue = session.createQueue(MQConstants.CLIENT_CODE + ".client");//客户端的接收队列
			this.consumer = session.createConsumer(inQueue);
			this.consumer.setMessageListener(listener);
			pooledconn.start();
			logger.info("客户端连接ActiveMQ成功");
			return pooledconn;
		} catch (Exception e) {
			logger.error("客户端连接ActiveMQ失败："+e);
			e.printStackTrace();
		}
		 return null;
	}

	/**
	 * 
	 * sendClientMsg:客户端发送消息
	 *
	 * @author sid
	 * @param code
	 * @param msg
	 */
	public void sendClientMsg(final String code,final AbsMsg msg) {
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
					logger.error("客户端发送消息："+e);
					e.printStackTrace();
				}finally{
					try {
						if(sess!=null)
						sess.close();
						if(p!=null)
						p.close();
					} catch (JMSException e) {
						logger.error("客户端关闭session和生产者失败："+e);
						e.printStackTrace();
					}
				}
			}
		};
		t.start();//启动，等待系统调用
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
			return initClient(new ClientMessageListener());
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
			consumer.close();
			session.close();
		} catch (JMSException e) {
			logger.error("close()", e);
		}
	}
}
