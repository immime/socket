package utils.soket.mq.mqutils;

import javax.jms.BytesMessage;

import socket.netty.msg.AbsMsg;
import socket.netty.msg.MSG_0x0001;
import socket.netty.msg.MSG_0x0002;
import socket.netty.msg.MSG_0x0003;
import socket.netty.msg.MSG_0x1001;
import socket.netty.msg.MSG_0x2001;
import socket.netty.msg.MSG_0x3003;

public class MessageFacotry {
	public static AbsMsg getMessage(short id,BytesMessage bm) throws Exception {
		if (id == 0x0001){
			MSG_0x0001 b = new MSG_0x0001();
			MSG_0x0001 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}else if (id == 0x0002){
			MSG_0x0002 b = new MSG_0x0002();
			MSG_0x0002 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}else if (id == 0x0003){
			MSG_0x0003 b = new MSG_0x0003();
			MSG_0x0003 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}else if (id == 0x1001){
			MSG_0x1001 b = new MSG_0x1001();
			MSG_0x1001 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}else if (id == 0x2001){
			MSG_0x2001 b = new MSG_0x2001();
			MSG_0x2001 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}else if (id == 0x3003){
			MSG_0x3003 b = new MSG_0x3003();
			MSG_0x3003 bms2Bean = BytesMessageBeanUtil.bms2Bean(bm, b);
			return bms2Bean;
		}
		return null;
	}

}
