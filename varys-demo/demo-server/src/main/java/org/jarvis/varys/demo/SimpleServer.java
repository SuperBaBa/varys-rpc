package org.jarvis.varys.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Marcus
 * @date 2021/2/17-12:51
 * @description this is function
 */
public class SimpleServer {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
    }
}
