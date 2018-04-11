package com.mytijian.admin.web.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mytijian.admin.web.intercepter.LogIntercepter;
import com.mytijian.admin.web.intercepter.SecurityInterceptor;
import com.mytijian.web.intercepter.TokenInterceptor;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    @ConditionalOnBean(SecurityManager.class)
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean(name="securityInterceptor")
    public SecurityInterceptor securityInterceptor(){
        SecurityInterceptor securityInterceptor = new SecurityInterceptor();
        securityInterceptor.setDebarApis("login");
        return securityInterceptor;
    }

    @Bean(name="logInterceptor")
    public LogIntercepter logIntercepter(){
        LogIntercepter logIntercepter = new LogIntercepter();
        logIntercepter.setLogClose(false);
        return logIntercepter;
    }

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setProxyTargetClass(true);
        beanNameAutoProxyCreator.setOptimize(true);
        beanNameAutoProxyCreator.setInterceptorNames("securityInterceptor","logInterceptor");
        beanNameAutoProxyCreator.setBeanNames("*Controller");
        return beanNameAutoProxyCreator;
    }

    @Bean
    public TokenInterceptor tokenInterceptor(){
        TokenInterceptor tokenInterceptor = new TokenInterceptor();
        return tokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor());
        super.addInterceptors(registry);
    }

}
