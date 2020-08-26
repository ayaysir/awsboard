package com.example.awsboard.domain.midi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MidiRepositoryTest {

    @Autowired
    MidiRepository midiRepository;

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        midiRepository.deleteAll();
    }

    @Test
    public void save_and_load() {
        // given

        midiRepository.save(Midi.builder()
                .category("e")
                .customTitle("e")
                .hash("e")
                .originalFileName("e")
                .originalMidiPath("e")
                .originalMp3Path("e")
                .userId(1l)
                .build());


        // when
        List<Midi> list = midiRepository.findAll();

        // then
        Midi midi = (Midi) list.get(0);
        assertThat(midi.getCategory()).isEqualTo("e");
        assertThat(midi.getId()).isPositive();
    }


}
