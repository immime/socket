package utils.soket.mq.client.thread;

import javax.jms.BytesMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socket.netty.msg.AbsMsg;
import utils.soket.mq.client.handler.HandlerFactory;
import utils.soket.mq.client.handler.IHandler;
import utils.soket.mq.mqutils.MessageFacotry;

/**
 * 处理消息线程
 * 
 * @author sid
 *
 */
public class ParseMsgThread extends Thread {

	private static final Logger logger = LoggerFactory
			.getLogger(ParseMsgThread.class);

	private BytesMessage bm;

	public ParseMsgThread(BytesMessage bm) {
		this.bm = bm;
	}

	@Override
	public void run() {
		try {
			// 生成消息后产生handler
			short id = this.bm.readShort();
			this.bm.reset();
			AbsMsg msg = MessageFacotry.getMessage(id,this.bm);
			if (msg == null) {
				logger.error(Integer.toHexString(id) + "消息不存在");
				return;
			}
			logger.info("消息体："+msg.toString());
			// 交给对应handler处理
			IHandler handler = HandlerFactory.getHandler(msg);
			if (handler != null) {
				handler.doHandle(msg);
			}
		} catch (Exception e) {
			logger.error("接受消息队列处理数据错误", e);
			e.printStackTrace();
		}
	}

}
