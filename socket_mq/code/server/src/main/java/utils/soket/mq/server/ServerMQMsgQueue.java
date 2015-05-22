package utils.soket.mq.server;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.BytesMessage;

/**
 * 
 * 消息队列
 * 
 * @author sid
 *
 */
public class ServerMQMsgQueue {
	/**
	 * 接收数据队列
	 */
	private static LinkedBlockingQueue<BytesMessage> queue = new LinkedBlockingQueue<BytesMessage>();

	public static LinkedBlockingQueue<BytesMessage> getQueue() {
		return queue;
	}
}
