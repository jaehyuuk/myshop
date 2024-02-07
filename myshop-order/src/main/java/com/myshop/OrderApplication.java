package com.myshop;

import com.myshop.global.config.JpaAuditingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import(JpaAuditingConfig.class)
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @PostConstruct
    public void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
