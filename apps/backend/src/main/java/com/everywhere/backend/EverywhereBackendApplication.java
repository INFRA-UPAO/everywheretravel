package com.everywhere.backend;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EverywhereBackendApplication {

    public static void main(String[] args) {
        String appTimezone = System.getenv().getOrDefault("APP_TIMEZONE", "America/Lima");
        TimeZone.setDefault(TimeZone.getTimeZone(appTimezone));
        SpringApplication.run(EverywhereBackendApplication.class, args);
    }

}