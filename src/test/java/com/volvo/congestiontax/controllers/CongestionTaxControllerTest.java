package com.volvo.congestiontax.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.congestiontax.dtos.CongestionTaxRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Somesh Kumar
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CongestionTaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testCalculateCongestionTax() throws Exception {
        // Prepare the request body (TaxRequest object)
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Car")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 9, 19, 6, 0),
                        LocalDateTime.of(2023, 9, 19, 7, 10),
                        LocalDateTime.of(2023, 9, 19, 6, 29)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.totalTax").value(26))
                .andExpect(jsonPath("$.taxesByDate[0].date").value("2023-09-19"))
                .andExpect(jsonPath("$.taxesByDate[0].amount").value("18.0"))
                .andExpect(jsonPath("$.taxesByDate[1].date").value("2013-09-19"))
                .andExpect(jsonPath("$.taxesByDate[1].amount").value("8.0"))
                .andExpect(jsonPath("$.vehicleType").value("Car"))  // Check vehicleType
                .andExpect(jsonPath("$.city").value("Gothenburg"));
    }

    @Test
    void testCalculateCongestionTaxWithExemptedVehicles() throws Exception {
        // Prepare the request body (TaxRequest object)
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Emergency")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 9, 19, 6, 0),
                        LocalDateTime.of(2023, 9, 19, 7, 10),
                        LocalDateTime.of(2023, 9, 19, 6, 29)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.totalTax").value(0))  // Check taxAmount
                .andExpect(jsonPath("$.vehicleType").value("Emergency"))  // Check vehicleType
                .andExpect(jsonPath("$.city").value("Gothenburg"));
    }

    @Test
    void testCalculateCongestionTaxWithWeekendExempted() throws Exception {
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Car")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2024, 9, 15, 6, 0)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.totalTax").value(0))  // Check taxAmount
                .andExpect(jsonPath("$.vehicleType").value("Car"))  // Check vehicleType
                .andExpect(jsonPath("$.city").value("Gothenburg"));
    }

    @Test
    void testCalculateCongestionTaxWithPublicHolidayExempted() throws Exception {
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Car")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 12, 25, 6, 0)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.totalTax").value(0))  // Check taxAmount
                .andExpect(jsonPath("$.vehicleType").value("Car"))  // Check vehicleType
                .andExpect(jsonPath("$.city").value("Gothenburg"));
    }

    @Test
    void testCalculateCongestionTaxWithDayBeforePublicHolidayExempted() throws Exception {
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Car")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 12, 24, 6, 0)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.totalTax").value(0))  // Check taxAmount
                .andExpect(jsonPath("$.vehicleType").value("Car"))  // Check vehicleType
                .andExpect(jsonPath("$.city").value("Gothenburg"));
    }

    @Test
    void testCalculateCongestionTaxCityAndCountryNotSupported() throws Exception {
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("Car")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 12, 24, 6, 0)
                ))
                .city("Gothenburg")
                .country("Norway")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.detail").value("City Gothenburg and country Norway not configured"));
    }

    @Test
    void testCalculateCongestionTaxVehicleTypeNotSupported() throws Exception {
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .vehicleType("X")
                .entryTimes(Arrays.asList(
                        LocalDateTime.of(2013, 12, 24, 6, 0)
                ))
                .city("Gothenburg")
                .country("Sweden")
                .build();

        // Convert the request object to JSON
        String jsonRequest = objectMapper.writeValueAsString(congestionTaxRequest);

        // Mock POST request
        mockMvc.perform(post("/api/v1/congestion-taxes")
                        .contentType(MediaType.APPLICATION_JSON) // Specify the content type as JSON
                        .content(jsonRequest)) // Pass the JSON request body
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))  // Verify response content type
                // Verify JSON response content using jsonPath
                .andExpect(jsonPath("$.detail").value("Vehicle type X not supported"));
    }

}
