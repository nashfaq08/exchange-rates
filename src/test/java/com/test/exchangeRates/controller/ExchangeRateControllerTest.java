package com.test.exchangeRates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.exchangeRates.dto.request.BillRequest;
import com.test.exchangeRates.dto.response.BillResponse;
import com.test.exchangeRates.service.ExchangeRatesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeRatesController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculate_ReturnsUnAuthorizedResponse() throws Exception {
        // Arrange
        BillRequest request = new BillRequest();
        request.setAmount(200);
        request.setItemType("electronics");
        request.setEmployee(true);
        request.setAffiliate(false);
        request.setCustomerOverTwoYears(false);
        request.setSourceCurrency("USD");
        request.setTargetCurrency("PKR");

        BillResponse mockResponse = new BillResponse(
                200.0,
                70.0,
                130.0,
                34000.0,
                "PKR"
        );

        Mockito.when(exchangeRatesService.calculateBill(any(BillRequest.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}

