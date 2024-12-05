package com.example.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfiguration {


    @Value("${spring.minio.endpoint}")
    private String endpoint;
    @Value("${spring.minio.username}")
    private String username;
    @Value("${spring.minio.password}")
    private String password;


    @Bean
    public MinioClient minioClient(){
        log.info("init minio client...");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }
}
