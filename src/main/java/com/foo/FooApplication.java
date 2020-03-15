package com.foo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableRetry
public class FooApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("EST5EDT"));
        SpringApplication.run(FooApplication.class, args);
    }
}

