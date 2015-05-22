/**
 * Project Name:mqutils
 * File Name:ToMQ.java
 * Package Name:utils.soket.mq.mqutils
 * Date:2015年5月20日上午9:21:46
 * Copyright (c) 2015, sid Jenkins All Rights Reserved.
 * 
 *
*/

package soket.mq.msg.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName:ToMQ
 * Reason:	 mq的Annotation
 * Date:     2015年5月20日 上午9:21:46 
 * @author   sid
 * @see 	 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ToMQField {

}

