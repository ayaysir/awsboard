package com.example.awsboard.web;

import com.example.awsboard.domain.log.Log;
import com.example.awsboard.domain.log.LogRepository;
import com.example.awsboard.web.dto.log.LogSaveRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        logRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void Log_등록된다() throws Exception {
        // given
        Long articleId = 1l;
        LogSaveRequestDTO logSaveRequestDTO = LogSaveRequestDTO.builder()
                .articleId(articleId)
                .boardName("Posts")
                .userId(1l)
                .build();

        String url = "http://localhost:" + port + "/api/v1/log";



        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(logSaveRequestDTO)))
                .andExpect(status().isOk());

        List<Log> all = logRepository.findAll();
        assertThat(all.get(0).getArticleId()).isEqualTo(articleId);
    }


}
