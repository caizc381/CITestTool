package com.mytijian.mediator.company.migrate.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.mytijian.sharding.datasource.DataSource;
import com.mytijian.sharding.datasource.DynamicDataSource;
import com.mytijian.sharding.datasource.DynamicDataSourceHolder;

@Component
@Aspect
public class DynamicDataSourceAop implements Ordered{

	public DynamicDataSourceAop(){
	}
	
    @Pointcut("execution(* com.mytijian.mediator.company.migrate.dao..*(..))")
    public void daoMethodPointCut() {
    }
    
    @Before("daoMethodPointCut()")
    public void beforeDaoMethod(JoinPoint jp) {
    	DynamicDataSourceHolder.removeCurrentDataSource();
        Class<? extends Object> mapperProxyClass = jp.getTarget().getClass();
        Class<?>[] mapperInterfaces = mapperProxyClass.getInterfaces();
        if (mapperInterfaces == null || mapperInterfaces.length == 0) {
            return;
        }
        for (Class<?> mapperInterface : mapperInterfaces) {
            if (mapperInterface.isAnnotationPresent(DynamicDataSource.class)) {
                DynamicDataSource dynamicDataSource = mapperInterface
                        .getAnnotation(DynamicDataSource.class);
                DataSource dataSource = dynamicDataSource.value();
                if (dataSource != null) {
                    DynamicDataSourceHolder.setCurrentDataSource(dataSource.getValue());
                }
            }
        }
    }

    @After("daoMethodPointCut()")
    public void afterDaoMethod() {
    	DynamicDataSourceHolder.removeCurrentDataSource();
    }

    /**
     * 切面执行顺序，保证在Transaction切面执行之前，Transaction切面order为2
     */
    @Override
    public int getOrder() {
    	return 1;
    }
}
