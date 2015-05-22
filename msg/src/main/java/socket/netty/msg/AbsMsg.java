package socket.netty.msg;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soket.mq.msg.utils.ToMQObject;
import soket.netty.msg.utils.Constants;


@SuppressWarnings("serial")
public abstract class AbsMsg implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(AbsMsg.class);

	@ToMQObject
	protected MsgHeader head;
	// 消息长度
	private ByteBuffer buffer = ByteBuffer.allocate(10*1024*1024);

	public AbsMsg() {
		this.head = new MsgHeader();
		this.head.setMsgid((short) getMsgID());
//		byte[] fs = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//		String persist = new String(fs);
		this.head.setMac(Constants.SERVER_MAC);
		if(this.head.getSeq()==Integer.MAX_VALUE)
			this.head.setSeq(0);;
		this.head.setSeq(this.head.getSeq()+1);
		this.head.setMsgid((short)this.getMsgID());
		this.head.setLength(Constants.HEAD_LENGTH+this.getBodylen());

	}

	public byte[] toBytes() {

		byte[] b = new byte[0];
		try {
			// 消息内容
			this.head.setLength(Constants.SIGN_STAR_LENGTH + Constants.HEAD_LENGTH + getBodylen() 
					+ Constants.SIGN_LENGTH + Constants.SIGN_END_LENGTH);
			byte[] head = this.head.tobytes();
			byte[] body = bodytoBytes();

			// 计算 校验位
			byte xor = 0;
			for (byte bt : head) {
				xor ^= bt;
			}
			for (byte bt : body) {
				xor ^= bt;
			}
			
			head = encode(head);
			body = encode(body);

			buffer.position(0);
			buffer.put((byte) 0x5b);
			buffer.put(head);
			buffer.put(body);
			buffer.put(xor);
			buffer.put((byte) 0x5d);

			b = new byte[buffer.position()];
			buffer.position(0);
			buffer.get(b);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("toBytes异常：",e);
		}

		return b;

	}

	/**
	 * 编码转义
	 * 
	 * @param bytes
	 * @return
	 */
	private byte[] encode(byte[] bytes) {

		buffer.position(0);
		for (byte b : bytes) {
			if (b == 0x5b) {
				buffer.put((byte) 0x5a);
				buffer.put((byte) 0x01);
			} else if (b == 0x5a) {
				buffer.put((byte) 0x5a);
				buffer.put((byte) 0x02);
			} else if (b == 0x5d) {
				buffer.put((byte) 0x5e);
				buffer.put((byte) 0x01);
			} else if (b == 0x5e) {
				buffer.put((byte) 0x5e);
				buffer.put((byte) 0x02);
			} else {
				buffer.put(b);
			}
		}

		byte[] result = new byte[buffer.position()];
		buffer.position(0);
		buffer.get(result);
		return result;
	}

	protected abstract int getMsgID();

	protected abstract int getBodylen();

	protected abstract byte[] bodytoBytes();

	protected abstract boolean bodyfrombytes(byte[] b);

	public MsgHeader getHead() {
		return head;
	}

	public void setHead(MsgHeader head) {
		this.head = head;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

}
