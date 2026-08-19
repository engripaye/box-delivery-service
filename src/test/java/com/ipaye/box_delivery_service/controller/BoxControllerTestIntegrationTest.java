package com.ipaye.box_delivery_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BoxControllerTestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateBox() throws Exception {

        mockMvc.perform(
                        post("/api/boxes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "txref": "TEST001",
                                    "weightLimit": 500,
                                    "batteryCapacity": 80
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.txref")
                        .value("TEST001"))
                .andExpect(jsonPath("$.weightLimit")
                        .value(500))
                .andExpect(jsonPath("$.batteryCapacity")
                        .value(80))
                .andExpect(jsonPath("$.state")
                        .value("IDLE"));
    }

    @Test
    void shouldRejectDuplicateBox() throws Exception {
        String request = """
            {
                "txref": "DUPLICATE001",
                "weightLimit": 500,
                "batteryCapacity": 80
            }
            """;

        mockMvc.perform(
                        post("/api/boxes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/boxes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)

                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }


}
