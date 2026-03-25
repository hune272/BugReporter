package com.bug_reporter.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.UUID;

@SpringBootApplication
public class BackendApplication {

    private static final String internalExecutionId = UUID.randomUUID().toString();

    public static void main(String[] args) {
        System.out.println("internalExecutionId: " + internalExecutionId);
        SpringApplication.run(BackendApplication.class, args);
    }
}