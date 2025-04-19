package com.test.exchangeRates.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.exchangeRates.dto.response.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateConfig {

    @Value("${exchange.api.key}")
    private String apiKey;

    @Value("${exchange.api.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "exchangeRates", key = "#from + '_' + #to")
    public double getExchangeRate(String from, String to) {
        String url = String.format("%s/%s/latest/%s", baseUrl, apiKey, from);
        log.info("Fetching exchange rate for {} to {}", from, to);
        try {
            log.info("Calling the Exchange Rates API to get the rate from {} to {}", from, to);
            String response = restTemplate.getForObject(url, String.class);
            ExchangeRateResponse exchangeRateResponse = objectMapper.readValue(response, ExchangeRateResponse.class);

            if (exchangeRateResponse != null && "success".equals(exchangeRateResponse.getResult())) {
                Double rate = exchangeRateResponse.getConversion_rates().get(to);
                if (rate != null) {
                    log.info("Fetched rate from {} to {} = {}", from, to, rate);
                    return rate;
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing exchange rate response: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exchange API call failed: {}", e.getMessage());
        }

        return 0.0;
    }
}

