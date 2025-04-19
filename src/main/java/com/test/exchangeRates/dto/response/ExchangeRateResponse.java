package com.test.exchangeRates.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ExchangeRateResponse {
    private String result;
    private String baseCode;
    private Map<String, Double> conversion_rates;

    public ExchangeRateResponse() {}
}
