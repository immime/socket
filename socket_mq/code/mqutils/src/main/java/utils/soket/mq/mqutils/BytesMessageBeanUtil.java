/**
 * Project Name:main
 * File Name:MongodbCollectionManagerUtil.java
 * Package Name:com.hdsx.taxi.driver.cq.Collection
 * Date:2014年4月9日下午12:49:55
 * Copyright (c) 2014, sid Jenkins All Rights Reserved.
 * 
 *
 */

package utils.soket.mq.mqutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;

import soket.mq.msg.utils.ToMQField;
import soket.mq.msg.utils.ToMQObject;
import utils.utils.LogUtil;

/**
 * 
 * ClassName: BytesMessageBeanUtil date: 2014年10月22日 上午10:22:36
 * 
 * @author sid
 */
public class BytesMessageBeanUtil {

	private volatile static Logger logger = LogUtil.getInstance().getLogger(
			BytesMessageBeanUtil.class);

	private volatile static BytesMessageBeanUtil singleton = null;

	public static BytesMessageBeanUtil getInstance() {
		if (singleton == null) {
			synchronized (BytesMessageBeanUtil.class) {
				if (singleton == null) {
					singleton = new BytesMessageBeanUtil();
				}
			}
			singleton = new BytesMessageBeanUtil();
		}
		return singleton;
	}

	private BytesMessageBeanUtil() {
	}

	/**
	 * 
	 * bean2BytesMessage:(把实体bean对象转换成BytesMessage).
	 * 
	 * @author sid
	 * @param obj
	 * @param bms
	 * @return
	 */
	public static BytesMessage bean2BytesMessage(Object obj, BytesMessage bms) {
		try {
			Class<?> clazz = obj.getClass();
			getSuperFields(obj, bms, clazz);
		} catch (Exception e) {
			logger.error("转换对象获取属性异常", e);
			e.printStackTrace();
		}
		return bms;
	}

	/**
	 * 
	 * getSuperFields:获取父类属性
	 * 
	 * @author sid
	 * @param obj
	 * @param bms
	 * @param clazz
	 * @return
	 * @throws Exception 
	 */
	private static void getSuperFields(Object obj, BytesMessage bms,
			Class<?> clazz) throws Exception {
		Class<?> superclass = clazz.getSuperclass();// 获取父类的属性
		String name = superclass.getName();
		if (!name.contains("Object")) {
			getSuperFields(obj, bms, superclass);
		}
		Field[] fds = clazz.getDeclaredFields();
		bean2Bms(obj, bms, clazz, fds);
	}

	/**
	 * 
	 * bean2Bms:(这里用一句话描述这个方法的作用).
	 * 
	 * @author sid
	 * @param obj
	 * @param bms
	 * @param metd
	 * @param clazz
	 * @param fds
	 * @return
	 * @throws Exception 
	 */
	private static void bean2Bms(Object obj, BytesMessage bms, Class<?> clazz,
			Field[] fds) throws Exception {
		Method metd = null;
		String fdname = null;
		for (Field field : fds) {
			boolean mqField = field.isAnnotationPresent(ToMQField.class);
			boolean mqObject = field.isAnnotationPresent(ToMQObject.class);
			if (!(mqField || mqObject)) {
				continue;
			}
			// 如果是普通属性，则直接获取对应的写入方式
			fdname = field.getName();
			metd = clazz.getMethod("get" + change(fdname));
			Object value = metd.invoke(obj);
			if (mqObject) {// 如果是自定义对象，则需要单独处理
				Class<?> type = field.getType();
				getSuperFields(value, bms, type);
			} else if (mqField) {
				boolean isArray = field.getType().isArray() ? true : false;
				String type = (isArray ? field.getType().getComponentType().getName() : field.getType().getName());
				if (isArray) {
					throw new Exception("暂时不支持数组数据的转换");
//					if (isArray) {
//						bms.writeBytes((byte[]) value);
//					} else if (isArray) {
//						bms.writeObject(value);
//					}
				} else {
					if (type.toLowerCase().contains("boolean")) {
						bms.writeBoolean((boolean) value);
					} else if (type.toLowerCase().contains("long")) {
						bms.writeLong((long) value);
					} else if (type.toLowerCase().contains("byte")) {
						bms.writeByte((byte) value);
					} else if (type.toLowerCase().contains("char")) {
						bms.writeChar((char) value);
					} else if (type.toLowerCase().contains("double")) {
						bms.writeDouble((double) value);
					} else if (type.toLowerCase().contains("float")) {
						bms.writeFloat((float) value);
					} else if (type.toLowerCase().contains("short")) {
						bms.writeShort((short) value);
					} else if (type.toLowerCase().contains("string")) {
						bms.writeUTF((String) value);
					} else if (type.toLowerCase().contains("int")) {
						bms.writeInt((int) value);
					} else if (type.toLowerCase().contains("object")) {
						throw new Exception("暂时不支持Object数据的设置");
//						bms.writeObject(value);
					}
				}
			}
		}
	}

	/**
	 * 
	 * BytesMessage2Bean:把BytesMessage转换成bean对象
	 * 
	 * @author sid
	 * @param BytesMessage
	 * @param bean
	 * @return
	 */
	public static <T> T bms2Bean(BytesMessage bms, T bean) {
		if (bean == null) {
			return null;
		}
		try {
			Class<?> clazz = bean.getClass();
			setSuperFields(bean, bms, clazz);
		} catch (Exception e) {
			logger.error("BytesMessage对象转为" + bean.getClass().getName() + "异常",e);
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 
	 * setSuperFields:设置父类属性
	 * 
	 * @author sid
	 * @param obj
	 * @param bms
	 * @param clazz
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws JMSException
	 * @throws Exception
	 */
	private static void setSuperFields(Object obj, BytesMessage bms,
			Class<?> clazz) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, JMSException,
			Exception {
		Class<?> superclass = clazz.getSuperclass();// 获取父类的属性
		String name = superclass.getName();
		if (!name.contains("Object")) {
			setSuperFields(obj, bms, superclass);
		}

		Field[] fds = clazz.getDeclaredFields();
		bms2Bean(obj, bms, clazz, fds);
	}

	/**
	 * 
	 * bms2Bean:(这里用一句话描述这个方法的作用).
	 * 
	 * @author sid
	 * @param obj
	 * @param bms
	 * @param metd
	 * @param clazz
	 * @param fds
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws JMSException
	 * @throws Exception
	 */
	private static <T> void bms2Bean(T obj, BytesMessage bms, Class<?> clazz,
			Field[] fds) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, JMSException, Exception {
		String fdname = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			boolean mqField = field.isAnnotationPresent(ToMQField.class);
			boolean mqObject = field.isAnnotationPresent(ToMQObject.class);
			if (!(mqField || mqObject)) {
				continue;
			}
			boolean isArray = field.getType().isArray() ? true : false;
			String type = (isArray ? field.getType().getComponentType()
					.getName() : field.getType().getName());
			fdname = field.getName();
			// 如果是普通属性，则直接获取对应的写入方式
			if (mqObject) {// 如果是自定义对象，则需要单独处理
				Class<?> fieldClass = field.getType();
				Object fieldObject = fieldClass.newInstance();
				setSuperFields(fieldObject, bms, fieldClass);
				BeanUtils.setProperty(obj, fdname, fieldObject);
			} else if (mqField) {
				Object object = null;
				if (isArray) {
					throw new Exception("暂时不支持数组的读取");
				} else {
					if (type.toLowerCase().contains("boolean")) {
						object = bms.readBoolean();
					} else if (type.toLowerCase().contains("long")) {
						object = bms.readLong();
					} else if (type.toLowerCase().contains("byte")) {
						object = bms.readByte();
					} else if (type.toLowerCase().contains("char")) {
						object = bms.readChar();
					} else if (type.toLowerCase().contains("double")) {
						object = bms.readDouble();
					} else if (type.toLowerCase().contains("float")) {
						object = bms.readFloat();
					} else if (type.toLowerCase().contains("short")) {
						object = bms.readShort();
					} else if (type.toLowerCase().contains("string")) {
						object = bms.readUTF();
					} else if (type.toLowerCase().contains("int")) {
						object = bms.readInt();
					} else if (type.toLowerCase().contains("object")) {
						throw new Exception("暂时不支持Object数据的读取");
					}
				}
				logger.debug("当前字段："+fdname+"当前字段类型："+type.toLowerCase()+"对应值："+object);
				if (object != null) {
					BeanUtils.setProperty(obj, fdname, object);
				}
			}
		}
	}

	/**
	 * 将字符串第一个字符大写并返还
	 * 
	 * @author sid
	 * @param src
	 *            源字符串
	 * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
	 */
	public static String change(String src) {
		if (src != null) {
			StringBuffer sb = new StringBuffer(src);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		} else {
			return null;
		}
	}
}
