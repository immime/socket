package utils.soket.mq.client;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ClassName: MessageClientListener 
 * date: 2015年5月19日 下午4:40:18 
 *
 * @author sid
 */
public class ClientMessageListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(ClientMessageListener.class);

	public void onMessage(Message message) {
		logger.info("客户端接收：");
		try {
			if (message instanceof BytesMessage) {
				// 与判断接收到的Message属于何种类型
				BytesMessage msg = (BytesMessage) message;
				ClientMQMsgQueue.getQueue().put(msg);
			}
		} catch (Exception e) {
			logger.error("onMessage(Message)", e);
		}
	}

}
