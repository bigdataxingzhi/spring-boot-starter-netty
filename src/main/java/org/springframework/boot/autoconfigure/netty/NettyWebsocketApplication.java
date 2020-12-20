package org.springframework.boot.autoconfigure.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.netty.annotation.EnableNettyWebSocket;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableNettyWebSocket
@EnableTransactionManagement
public class NettyWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyWebsocketApplication.class, args);

    }

}
