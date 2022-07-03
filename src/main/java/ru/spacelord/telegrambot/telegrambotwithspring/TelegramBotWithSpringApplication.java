package ru.spacelord.telegrambot.telegrambotwithspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TelegramBotWithSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotWithSpringApplication.class, args);
    }
}
