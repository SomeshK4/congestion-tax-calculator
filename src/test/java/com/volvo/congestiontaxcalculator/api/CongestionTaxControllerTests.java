package com.volvo.congestiontaxcalculator.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CongestionTaxControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String API_URI = "/api/v1/road-tolls/congestiontax";


    @Test
    @DisplayName("Test congestion tax free time")
    void testTaxFreeTime() throws Exception {
        File file = ResourceUtils.getFile("classpath:fixtures/taxFreeRequest.json");
        String json = new String(Files.readAllBytes(file.toPath()));
        mockMvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.tax").value(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Test with congestion tax greater than zero for a vehicel")
    void testWithTaxGreaterThanZero() throws Exception {
        File file = ResourceUtils.getFile("classpath:fixtures/taxGreaterThanZeroRequest.json");
        String json = new String(Files.readAllBytes(file.toPath()));
        mockMvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tax").value(new BigDecimal(58)));
    }


}
