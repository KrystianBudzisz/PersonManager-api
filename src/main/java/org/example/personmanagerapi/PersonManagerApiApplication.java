package org.example.personmanagerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PersonManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonManagerApiApplication.class, args);
    }

}
