package com.citest.tool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages="com.citest.tool")
@SpringBootApplication
@MapperScan(value ="com.citest.tool.dao")
//@MapperScan("com.citest.tool.mapper.*")
//@ImportResource(value = { "dubbo-consumer.xml", "dubbo-provider.xml"})
//@EnableAutoConfiguration
@EnableScheduling
//@EnableApolloConfig
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
