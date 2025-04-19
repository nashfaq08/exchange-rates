package com.test.exchangeRates.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.exchangeRates.dto.response.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExchangeRateConfigTest {

    @InjectMocks
    private ExchangeRateConfig exchangeRateConfig;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(exchangeRateConfig, "apiKey", "mock-key");
        ReflectionTestUtils.setField(exchangeRateConfig, "baseUrl", "https://mock.api");

        ReflectionTestUtils.setField(exchangeRateConfig, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(exchangeRateConfig, "objectMapper", objectMapper);
    }

    @Test
    void testGetExchangeRate_Success() throws Exception {
        String from = "USD";
        String to = "PKR";
        double expectedRate = 0.0;

        // Mock JSON response
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setResult("success");
        Map<String, Double> rates = new HashMap<>();
        rates.put("PKR", expectedRate);
        mockResponse.setConversion_rates(rates);

        String jsonResponse = new ObjectMapper().writeValueAsString(mockResponse);

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(jsonResponse);

        double rate = exchangeRateConfig.getExchangeRate(from, to);

        assertEquals(expectedRate, rate, 0.01);
        verify(restTemplate, times(1)).getForObject(contains(from), eq(String.class));
    }

    @Test
    void testGetExchangeRate_WhenCurrencyNotFound_ReturnsZero() throws Exception {
        String from = "USD";
        String to = "INVALID";

        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setResult("success");
        mockResponse.setConversion_rates(new HashMap<>()); // empty map

        String json = new ObjectMapper().writeValueAsString(mockResponse);

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);

        double rate = exchangeRateConfig.getExchangeRate(from, to);
        assertEquals(0.0, rate);
    }

    @Test
    void testGetExchangeRate_WhenApiFails_ReturnsZero() {
        String from = "USD";
        String to = "PKR";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(
                new RuntimeException("API is not reachable"));

        double rate = exchangeRateConfig.getExchangeRate(from, to);
        assertEquals(0.0, rate);
    }

    @Test
    void testJsonProcessingExceptionHandling() throws JsonProcessingException {
        String url = "http://api.com/apiKey/latest/USD";
        when(restTemplate.getForObject(url, String.class)).thenReturn("Invalid JSON Response");

        when(objectMapper.readValue(anyString(), eq(ExchangeRateResponse.class)))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        double result = exchangeRateConfig.getExchangeRate("USD", "EUR");

        assertEquals(0.0, result, "Expected result to be 0.0 when JsonProcessingException is thrown");
    }

    @Test
    void shouldReturnExchangeRate_whenApiReturnsValidResponse() throws JsonProcessingException {
        String from = "USD";
        String to = "EUR";
        double expectedRate = 0.85;

        String mockJson = "{ \"result\": \"success\", \"conversion_rates\": { \"EUR\": " + expectedRate + " } }";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockJson);
        when(objectMapper.readValue(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(new ExchangeRateResponse("success", "", Map.of("EUR", expectedRate)));

        double rate = exchangeRateConfig.getExchangeRate(from, to);

        assertEquals(expectedRate, rate);
    }

    @Test
    void shouldReturnZero_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        String from = "USD";
        String to = "EUR";

        String badJson = "invalid json";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(badJson);
        when(objectMapper.readValue(anyString(), eq(ExchangeRateResponse.class)))
                .thenThrow(new JsonProcessingException("Malformed JSON") {});

        double rate = exchangeRateConfig.getExchangeRate(from, to);

        assertEquals(0.0, rate);
    }


}

