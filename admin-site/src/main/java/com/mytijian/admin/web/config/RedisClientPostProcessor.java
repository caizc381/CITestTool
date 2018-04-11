package com.mytijian.admin.web.config;

import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.RedisCacheClientImpl;
import com.mytijian.cache.annotation.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 *
 * @author linzhihao
 */
@Component
public class RedisClientPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

	private final static Logger logger = LoggerFactory.getLogger(com.mytijian.cache.RedisClientPostProcessor.class);

	private RedisTemplate<String, Serializable> redisTemplate;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		injectRedisCacheBean(bean, beanName);

		return super.postProcessBeforeInitialization(bean, beanName);
	}

	private void injectRedisCacheBean(final Object bean, String beanName) {
		try {
			injectField(bean, beanName);
			injectMethod(bean, beanName);
		} catch (InvocationTargetException | IllegalAccessException e) {
			logger.error("在 {} 注入redis client 失败 {}", beanName, e);
		}
	}

	private void injectMethod(final Object bean, String beanName) throws IllegalAccessException,
			InvocationTargetException {
		Class<?> clazz = bean.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			RedisClient info = method.getDeclaredAnnotation(RedisClient.class);
			if (info != null) {
				if (redisTemplate == null){
					this.redisTemplate = (RedisTemplate<String, Serializable>) applicationContext.getBean("redisTemplate");
				}
				if (method.getParameterTypes()[0] == RedisCacheClient.class) {
					RedisCacheClient<?> client = new RedisCacheClientImpl<>(redisTemplate, info.nameSpace(), info.timeout());
					method.setAccessible(true);
					method.invoke(bean, client);
					logger.info("在 {} 注入redis client 成功。", beanName);
				}
			}
		}
	}

	private void injectField(final Object bean, String beanName) throws IllegalAccessException {
		Class<?> clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			RedisClient info = field.getDeclaredAnnotation(RedisClient.class);
			if (info != null) {
				if (redisTemplate == null){
					this.redisTemplate = (RedisTemplate<String, Serializable>) applicationContext.getBean("redisTemplate");
				}
				if (field.getType() == RedisCacheClient.class) {
					RedisCacheClient<?> client = new RedisCacheClientImpl<>(redisTemplate, info.nameSpace(), info.timeout());
					field.setAccessible(true);
					field.set(bean, client);
					logger.info("在 {} 注入redis client 成功。", beanName);
				}
			}
		}
	}

}
