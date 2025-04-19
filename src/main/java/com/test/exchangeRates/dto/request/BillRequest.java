package com.test.exchangeRates.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {
    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Source currency is required")
    private String sourceCurrency;

    @NotBlank(message = "Target currency is required")
    private String targetCurrency;

    @NotBlank(message = "Item type is required (e.g., groceries, electronics)")
    private String itemType;

    private boolean isEmployee;
    private boolean isAffiliate;
    private boolean isCustomerOverTwoYears;
}
