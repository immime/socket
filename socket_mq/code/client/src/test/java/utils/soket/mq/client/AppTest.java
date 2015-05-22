package utils.soket.mq.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import socket.netty.msg.MSG_0x0001;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	public void ClientTest() {
		MSG_0x0001 msg = new MSG_0x0001();
		MQService.getInstance().initClient(new ClientMessageListener());
		MQService.getInstance().sendClientMsg(MQConstants.CLIENT_CODE, msg);
	}

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}
}
