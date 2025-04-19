package com.test.exchangeRates.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ExchangeRateConfigIntegrationTest {

    @Autowired
    private ExchangeRateConfig exchangeRateConfig;

    @Test
    void shouldFetchActualExchangeRateFromAPI() {
        String from = "USD";
        String to = "EUR";

        double rate = exchangeRateConfig.getExchangeRate(from, to);

        System.out.println("Exchange rate from " + from + " to " + to + " is: " + rate);
        assertTrue(rate > 0, "Exchange rate should be greater than 0");
    }

}
