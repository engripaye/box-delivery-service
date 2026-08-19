package com.ipaye.box_delivery_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BoxControllerTestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateBox() throws Exception {


    }
}
