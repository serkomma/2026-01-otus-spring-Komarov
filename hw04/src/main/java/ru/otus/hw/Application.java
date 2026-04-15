package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value = "ru.otus.hw")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
//        ApplicationContext context = null;
//        var testRunnerService = context.getBean(TestRunnerService.class);
//        testRunnerService.run();
        SpringApplication.run(Application.class, args);

    }
}