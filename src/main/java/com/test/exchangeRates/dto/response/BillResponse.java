package com.test.exchangeRates.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private double originalAmount;
    private double discountApplied;
    private double finalAmount;
    private double convertedAmount;
    private String currency;
}
