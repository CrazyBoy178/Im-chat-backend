package com.chat.imbackend.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class RedisConfig{

    public RedisMessageListenerContainer listenerContainer
            (RedisConnectionFactory connectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        List<Topic> list = new ArrayList<Topic>();
        list.add(new PatternTopic("yootk:*"));
        list.add(new ChannelTopic("channel:yootk"));
        container.addMessageListener(this.messageListener(),list);
        return container;
    }

    @Bean
    public MessageListener messageListener(){
        return new  MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern){
                String channel = new String(message.getChannel());
                String payload = new String(message.getBody());
                log.info("[消息订阅] 通道[{}]内容[{}]",channel,payload);
            }
        };
    }
}
