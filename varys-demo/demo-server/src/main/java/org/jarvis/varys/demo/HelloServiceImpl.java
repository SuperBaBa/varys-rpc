package org.jarvis.varys.demo;

import org.jarvis.varys.annotation.VarysService;
import org.jarvis.varys.demo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marcus
 * @date 2021/2/17-12:47
 * @description this is function
 */
@VarysService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    private Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public void sayHelloWorld() {
        log.info("HelloWorld in {}", this.getClass().getCanonicalName());
    }

    @Override
    public String sayHi() {
        return "";
    }

    @Override
    public String sayHello() {
        return "Hello World";
    }
}
