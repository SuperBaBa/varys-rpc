package org.jarvis.varys.demo;

import org.jarvis.varys.client.VarysRpcProxy;
import org.jarvis.varys.demo.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author marcus
 * @date 2021/8/23-0:15
 */
public class HelloClient {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        VarysRpcProxy proxy = context.getBean(VarysRpcProxy.class);
        HelloService helloService = proxy.createProxy(HelloService.class);
        String helloStr = helloService.sayHello();
        System.out.println(helloStr);
    }
}
