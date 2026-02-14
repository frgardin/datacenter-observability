package com.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatacenterObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatacenterObservabilityApplication.class, args);
    }
}
