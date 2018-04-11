package com.mytijian.admin.web.util;

import org.springframework.util.StringUtils;

import java.beans.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * 
 * @author feng
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionTool {

	/**
	 * 用来转换map到对象的工具方法
	 * 
	 * @param type
	 * @param map
	 * @return 目标对象
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class<?> type, Map map) throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance();
		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		if (propertyDescriptors != null && propertyDescriptors.length > 0) {
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (map.containsKey(propertyName)) {

					try {
						Object value = map.get(propertyName);

						Object[] args = new Object[1];
						args[0] = value;

						descriptor.getWriteMethod().invoke(obj, args);
					} catch (Exception e) {
						// nothing,allow miss mapping's value exsited!
					}
				}
			}
		}

		return obj;
	}

	@SuppressWarnings("rawtypes")
	public static Map convertBean(Object bean) throws Exception {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

	/**
	 * 设定对象成员变量值的工具方法
	 * 
	 * @param object
	 * @param propertyName
	 * @param propertyValue
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public static void setObjectPropertyValue(Object object, String propertyName, Object propertyValue)
			throws NoSuchFieldException, IllegalAccessException {
		if (object != null && !StringUtils.isEmpty(propertyName)) {
			Class clazz = object.getClass();
			Field field = clazz.getDeclaredField(propertyName);
			if (field != null) {
				field.setAccessible(true);// 这个是必须的,因为不想外部使用setter改变它的值
				field.set(object, propertyValue);
			}
		}
	}

	public static Object executeObjMethod(Object obj, String methodName, Object... params) {
		if (obj != null && StringUtils.isEmpty(methodName))
			return null;
		Class<?> clazz = obj.getClass();
		try {
			Method method = clazz.getDeclaredMethod(methodName);
			return method.invoke(obj, params);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object getField(Object obj, String fieldName) {
		if (obj == null || StringUtils.isEmpty(fieldName)) {
			return null;
		}
		Class clazz = obj.getClass();
		try {
			Field field = clazz.getDeclaredField(fieldName);
			return field.get(obj);
		} catch (Exception e) {
			return null;
		}
	}
}

