package com.example.awsboard.web.dto.toy;

import com.example.awsboard.web.dto.toy.LombokCar;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokCarTest {

    @Test
    public void 빌더_테스트_2() {
        String id = "2";
        String name = "James";

        LombokCar car2 = LombokCar.builder()
                .id(id)
                .name(name)
                .build();

        assertThat(car2.getId()).isEqualTo(id);
        assertThat(car2.getName()).isEqualTo(name);

    }
}
