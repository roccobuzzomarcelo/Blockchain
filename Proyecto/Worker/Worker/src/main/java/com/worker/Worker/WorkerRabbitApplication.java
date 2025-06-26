package com.worker.Worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkerRabbitApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerRabbitApplication.class, args);
    }
}