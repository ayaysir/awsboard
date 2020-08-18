package com.example.awsboard.web;

import com.example.awsboard.domain.midi.Midi;
import com.example.awsboard.domain.midi.MidiRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MidiApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MidiRepository midiRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        midiRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() throws Exception {
        midiRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void get_midi_list() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/v1/midi";

        midiRepository.save(Midi.builder()
                .originalFileName("e")
                .build());

        mvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Midi> all = midiRepository.findAll();
        Assertions.assertThat(all.get(0).getOriginalFileName()).isEqualTo("e");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void get_midi_list_by_userId() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/v1/midi";

        midiRepository.save(Midi.builder()
                .originalFileName("e")
                .userId(1L)
                .build());

        mvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Midi> all = midiRepository.findByUserId(1L);
        Assertions.assertThat(all.get(0).getOriginalFileName()).isEqualTo("e");
        Assertions.assertThat(all.get(0).getUserId()).isEqualTo(1L);
    }

}
