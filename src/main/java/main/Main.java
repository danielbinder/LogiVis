package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import util.Logger;
import util.Result;

import java.util.Arrays;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, "--server.port=4000");
        Result.DEV = Arrays.asList(context.getEnvironment().getActiveProfiles()).contains("dev");

        Logger.info("Application started! Go to http://localhost:4000");
    }
}
