package com.example.awsboard.web.dto.toy;

import lombok.Builder;
import lombok.Getter;

@Getter // Getter 생성
public class LombokCar {
    private String id;
    private String name;

    @Builder // 생성자를 만든 후 그 위에 @Build 어노테이션 적용
    public LombokCar(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
