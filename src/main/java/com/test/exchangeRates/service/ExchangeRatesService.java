package com.test.exchangeRates.service;

import com.test.exchangeRates.config.ExchangeRateConfig;
import com.test.exchangeRates.dto.request.BillRequest;
import com.test.exchangeRates.dto.response.BillResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesService {

    private final ExchangeRateConfig exchangeRateConfig;

    public BillResponse calculateBill(BillRequest request) {
        log.info("Starting currency conversion for request: {}", request);

        double originalAmount = request.getAmount();
        log.debug("Original amount: {}", originalAmount);

        boolean isGrocery = isGroceryItem(request.getItemType());
        double percentageDiscountRate = isGrocery ? 0 : getPercentageDiscountRate(request);
        double percentageDiscountAmount = originalAmount * percentageDiscountRate;
        log.debug("Percentage discount rate: {}, Discount amount: {}", percentageDiscountRate,
                percentageDiscountAmount);

        double totalAfterPercentageDiscount = originalAmount - percentageDiscountAmount;
        log.debug("Amount after percentage discount: {}", totalAfterPercentageDiscount);

        double flatDiscount = calculateFlatDiscount(totalAfterPercentageDiscount);
        log.debug("Flat discount based on bill: {}", flatDiscount);

        double finalAmount = totalAfterPercentageDiscount - flatDiscount;
        double totalDiscount = percentageDiscountAmount + flatDiscount;
        log.debug("Final amount after all discounts: {}, Total discount: {}", finalAmount, totalDiscount);

        double exchangeRate = exchangeRateConfig.getExchangeRate(
                request.getSourceCurrency(),
                request.getTargetCurrency()
        );
        log.debug("Exchange rate from {} to {}: {}", request.getSourceCurrency(),
                request.getTargetCurrency(), exchangeRate);

        double convertedAmount = finalAmount * exchangeRate;
        log.info("Converted amount in {}: {}", request.getTargetCurrency(), convertedAmount);

        BillResponse billResponse = new BillResponse(
                originalAmount,
                totalDiscount,
                finalAmount,
                convertedAmount,
                request.getTargetCurrency()
        );
        log.info("Completed currency conversion. Response: {}", billResponse);
        return billResponse;
    }

    private boolean isGroceryItem(String itemType) {
        return itemType != null && itemType.equalsIgnoreCase("groceries");
    }

    private double getPercentageDiscountRate(BillRequest request) {
        if (request.isEmployee()) return 0.30;
        if (request.isAffiliate()) return 0.10;
        if (request.isCustomerOverTwoYears()) return 0.05;
        return 0.0;
    }

    private double calculateFlatDiscount(double amount) {
        final double FLAT_DISCOUNT_UNIT = 100.0;
        final double DISCOUNT_PER_UNIT = 5.0;
        return Math.floor(amount / FLAT_DISCOUNT_UNIT) * DISCOUNT_PER_UNIT;
    }

}
