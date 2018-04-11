package com.mytijian.admin.web;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;

/**
 * Created by king on 2017/9/28.
 */
@SpringBootApplication(exclude = { ValidationAutoConfiguration.class,
		PersistenceExceptionTranslationAutoConfiguration.class,
		EmbeddedMongoAutoConfiguration.class })
@ImportResource(locations = {"classpath:applicationContext.xml"})
@ComponentScan("com.mytijian.admin")
@MapperScan("com.mytijian.admin.dao")
@EnableTransactionManagement
@EnableApolloConfig
public class AdminSiteApplication  {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("1MB");
        factory.setMaxRequestSize("1MB");
        factory.setLocation("/tmp");
        return factory.createMultipartConfig();
    }

    public static void main(String[] args) {
        SpringApplication.run(AdminSiteApplication.class,args);
    }
}
