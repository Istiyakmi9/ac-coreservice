package com.bot.coreservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            return switch (response.status()) {
                case 400 -> new ApplicationException("Bad Request");
                case 404 -> new ApplicationException("Resource Not Found");
                case 500 -> new ApplicationException("Internal Server Error");
                default -> new Exception("Generic error");
            };
        }
    }
}
