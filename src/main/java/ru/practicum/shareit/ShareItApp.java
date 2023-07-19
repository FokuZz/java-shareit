package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "ru.practicum")
//@ComponentScan(basePackages = "ru.practicum") это тоже не помогает
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}
