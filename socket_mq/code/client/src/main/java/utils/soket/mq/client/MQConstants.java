/**
 * Project Name:mqutils
 * File Name:MQConstants.java
 * Package Name:utils.soket.mq.mqutils
 * Date:2015年5月19日下午4:41:59
 * Copyright (c) 2015, sid Jenkins All Rights Reserved.
 * 
 *
*/

package utils.soket.mq.client;

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
	private static final String CLIENT_CONFIG="client.mq.properties";
	private static final String charset="utf-8";
	
	public static final String CLIENT_URL =PropertiesUtil.getProperties(CLIENT_CONFIG, charset).getProperty("mq.url");
	public static final String CLIENT_USER =PropertiesUtil.getProperties(CLIENT_CONFIG, charset).getProperty("mq.user");
	public static final String CLIENT_PASSWORD =PropertiesUtil.getProperties(CLIENT_CONFIG, charset).getProperty("mq.password");
	public static final String CLIENT_CODE =PropertiesUtil.getProperties(CLIENT_CONFIG, charset).getProperty("mq.code");
	public static final String HEARTBEATDELAY = PropertiesUtil.getProperties().getProperty("tcp.temheartbeatdelay");
	
}

