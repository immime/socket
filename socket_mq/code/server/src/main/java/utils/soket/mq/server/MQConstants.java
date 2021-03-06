/**
 * Project Name:mqutils
 * File Name:MQConstants.java
 * Package Name:utils.soket.mq.mqutils
 * Date:2015年5月19日下午4:41:59
 * Copyright (c) 2015, sid Jenkins All Rights Reserved.
 * 
 *
*/

package utils.soket.mq.server;

import java.util.Properties;

import utils.utils.PropertiesUtil;

/**
 * ClassName:MQConstants
 * Date:     2015年5月19日 下午4:41:59 
 * @author   sid
 * @see 	 
 */
public class MQConstants {

	/**
	 * 配置文件地址
	 */
	private static final String SERVER_CONFIG="server.mq.properties";
	private static final String charset="utf-8";

	public static final Properties SERVER = PropertiesUtil.getProperties(SERVER_CONFIG, charset);

	public static final String HEARTBEATDELAY = PropertiesUtil.getProperties().getProperty("tcp.temheartbeatdelay");
	
}

