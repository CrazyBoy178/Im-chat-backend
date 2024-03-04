package com.chat.imbackend;

import com.chat.imbackend.controller.WebSocketController;
import com.chat.imbackend.controller.WebsocketChatroomController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration //将这个类变成启动类
@SpringBootApplication
public class ImBackendApplication {

    public static void main(String[] args) {
        //SpringApplication.run(ImBackendApplication.class, args);
        SpringApplication springApplication = new SpringApplication(ImBackendApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        WebSocketController.setApplicationContext(configurableApplicationContext);

        System.out.println("Success");
    }

}
