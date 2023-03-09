package com.lgu.ccss;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
@EnableAspectJAutoProxy
public class App {
	public static ApplicationContext ctx;
	
    public static void main( String[] args ){
    	ctx = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
    	
    }
}
