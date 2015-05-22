package utils.soket.mq.client.handler;

import socket.netty.msg.AbsMsg;

public interface IHandler {

	void doHandle(AbsMsg m);

}
