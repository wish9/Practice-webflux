package com.codestates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories    // R2DBC의 Reposiroty를 사용하기 위해 추가
@EnableR2dbcAuditing        // 데이터베이스에 엔티티가 저장 및 수정 될 때, 생성 날짜와 수정 날짜를 자동으로 저장할 수 있도록 Auditing 기능을 사용하기 위해 추가
@SpringBootApplication
public class WebFluxCoffeeOrderSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxCoffeeOrderSampleApplication.class, args);
    }

}
