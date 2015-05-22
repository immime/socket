package socket.netty.server;

import java.util.concurrent.LinkedBlockingQueue;

import socket.netty.msg.ReciPackBean;

/**
 * 
 * 消息队列
 * 
 * @author sid
 *
 */
public class ServerMsgQueue {
	/**
	 * 接收数据队列
	 */
	private static LinkedBlockingQueue<ReciPackBean> rec_queue = new LinkedBlockingQueue<ReciPackBean>();

	public static LinkedBlockingQueue<ReciPackBean> getRecqueue() {
		return rec_queue;
	}
}
