package me.shinsunyoung.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing  // created_at, updated_at 자동 업데이트
@SpringBootApplication
@EnableScheduling   // 수동 수집 -> 자동 수집
public class SpringBootDeveloperApplication {
    public static void main(String[] args) {

        SpringApplication.run(SpringBootDeveloperApplication.class, args);
    }
}
