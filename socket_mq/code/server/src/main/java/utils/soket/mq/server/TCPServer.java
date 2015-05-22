package utils.soket.mq.server;

import org.slf4j.Logger;

import socket.netty.msg.AbsMsg;
import utils.soket.mq.server.thread.ParseMsgThreadManager;
import utils.utils.LogUtil;


/**
 * 
 * ClassName: TCPServer 
 * date: 2015年1月29日 下午4:11:19 
 *
 * @author sid
 */
public class TCPServer extends Thread {

	private volatile static TCPServer instance = null;

	public static TCPServer getSingletonInstance() {
		if (instance == null) {
			synchronized (TCPServer.class) {
				if (instance == null) {
					instance = new TCPServer();
				}
			}
			instance = new TCPServer();
		}
		return instance;
	}

	private TCPServer() {}
	
	private Logger logger = LogUtil.getInstance().getLogger(TCPServer.class);

	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug("run() - start"); //$NON-NLS-1$
		}

		init();

		// 启动消息处理
		ParseMsgThreadManager.getInstance().run(0,0);

		if (logger.isDebugEnabled()) {
			logger.debug("run() - end"); //$NON-NLS-1$
		}
	}

	private void init() {
		MQService.getInstance().initServer(new ServerMessageListener());
	}

	/**
	 * 
	 * send:(发送消息).
	 *
	 * @author sid
	 * @param m
	 */
	public void send(AbsMsg m) {
		MQService.getInstance().sendServerMsg("2301",m);
	}
}
