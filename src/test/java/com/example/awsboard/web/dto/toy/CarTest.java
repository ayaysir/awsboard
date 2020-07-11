package com.example.awsboard.web.dto.toy;

import com.example.awsboard.web.dto.toy.Car;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CarTest {

    @Test
    public void 빌더_테스트_1() {
        String id = "1";
        String name = "Charles";

        // Car car1 = new Car(id, name)
        // Car car2 = new Car(name, car) // ??
        Car car1 = Car.builder()
                .id(id)
                .name(name)
                .build();

        assertThat(car1.getId()).isEqualTo(id);
        assertThat(car1.getName()).isEqualTo(name);

    }
}
