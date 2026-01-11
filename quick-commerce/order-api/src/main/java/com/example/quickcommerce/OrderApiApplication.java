package com.example.quickcommerce;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling public class OrderApiApplication{
    public static void main(String[] a){
        SpringApplication.run(OrderApiApplication.class,a);
    }
}