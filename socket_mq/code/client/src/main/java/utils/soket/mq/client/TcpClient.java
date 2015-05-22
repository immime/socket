package utils.soket.mq.client;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socket.netty.msg.AbsMsg;
import socket.netty.msg.MSG_0x0001;
import socket.netty.msg.MSG_0x1001;
import soket.netty.msg.utils.Constants;
import utils.soket.mq.client.thread.ParseMsgThreadManager;
import utils.utils.DateUtil;

public class TcpClient extends Thread {

	private volatile static TcpClient obj;

	public static TcpClient getInstance() {
		if (obj == null) {
			synchronized (TcpClient.class) {
				if (obj == null) {
					obj = new TcpClient();
				}
			}
			obj = new TcpClient();
		}
		return obj;
	}

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

	private boolean isLogined = false; // 是否登陆成功
	private int connstate = 0;

	public void reconnect() {
		logger.info("数据端：断线重连线程，当前状态：" + (this.connstate==1?"连接中":"已断线"));
		if (this.connstate != 1){
			try {
				init();
			} catch (Exception e) {
				logger.error("client断线重连初始化失败：", e);
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		init();
		
		ParseMsgThreadManager.getInstance().run(0, 0);
		
		login();
	}

	private void init() {
		MQService.getInstance().initClient(new ClientMessageListener());
	}

	/**
	 * 设置登陆状态
	 * 
	 * @param b
	 */
	public void loginOK(boolean b) {
		this.isLogined = b;
		//如果是第一次登录成功
		if (this.isLogined) {
			this.connstate = 1;
			logger.info("client线程启动成功");
			MSG_0x1001 msg = new MSG_0x1001();
			msg.setId("110703198811123562");
			TcpClient.getInstance().send(msg);
		}
	}

	/**
	 * 断开连接，关闭服务
	 */
	public void stopClient() {
		ParseMsgThreadManager.getInstance().stop();
		this.isLogined = false;
		this.connstate = 0;
	}

	/**
	 * 发送消息
	 * 
	 * @param m
	 */
	public void send(AbsMsg m) {
		if (this.isLogined) {
			logger.debug("CLINET发送："+m.toString());
			MQService.getInstance().sendClientMsg(MQConstants.CLIENT_CODE,m);
		}
	}

	/**
	 * 
	 * login:(发送登陆消息).
	 * 
	 * @author sid
	 */
	public void login() {
		// 打开连接时发送登录消息
		try {
			MSG_0x0001 msg = new MSG_0x0001();
			msg.setMd5(Constants.CLIENT_MAC);
			msg.setConnecttime(DateUtil.dateToStr(new Date(), "yyyyMMddHHmmss"));
			MQService.getInstance().sendClientMsg(MQConstants.CLIENT_CODE,msg);
		} catch (Exception e) {
			logger.error("Client :login() - Exception",e); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	public int getConnstate() {
		return connstate;
	}

	public void setConnstate(int connstate) {
		this.connstate = connstate;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}
}
