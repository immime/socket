package utils.soket.mq.client;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.BytesMessage;

/**
 * 消息队列
 * 
 */
public class ClientMQMsgQueue {

	/**
	 * 接收数据队列
	 */
	private static LinkedBlockingQueue<BytesMessage> queue = new LinkedBlockingQueue<BytesMessage>();

	public static LinkedBlockingQueue<BytesMessage> getQueue() {
		return queue;
	}
}
