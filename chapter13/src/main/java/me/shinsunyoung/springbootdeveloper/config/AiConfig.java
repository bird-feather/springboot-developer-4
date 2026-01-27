package me.shinsunyoung.springbootdeveloper.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    // ImageModel은 Spring AI가 자동으로 빈을 생성해줍니다
}