package utils.soket.mq.server.handler;

import socket.netty.msg.AbsMsg;

public interface IHandler {

	void doHandle(AbsMsg m);

}
