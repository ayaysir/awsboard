package com.example.awsboard.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class BasicControllerTest {
    @Autowired private MockMvc mvc;

    @Test
    public void hell이_리턴된다() throws Exception {
        String hell = "Hell";

        mvc.perform(get("/hell"))
                .andExpect(status().isOk())
                .andExpect(content().string(hell));

    }

    @Test
    public void basicResponseDTO가_리턴된다() throws Exception {
        String name = "Hell";
        int amount = 1000;

        mvc.perform(get("/hell/dto").param("name", name).param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.amount").value(amount));
    }

}
