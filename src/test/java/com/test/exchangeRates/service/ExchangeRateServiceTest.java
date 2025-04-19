package com.test.exchangeRates.service;

import com.test.exchangeRates.config.ExchangeRateConfig;
import com.test.exchangeRates.dto.request.BillRequest;
import com.test.exchangeRates.dto.response.BillResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateConfig exchangeRateConfig;

    @InjectMocks
    private ExchangeRatesService exchangeRatesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private BillRequest createRequest(String itemType, boolean isEmployee, boolean isAffiliate, boolean isCustomerOverTwoYears, double amount) {
        BillRequest request = new BillRequest();
        request.setAmount(amount);
        request.setItemType(itemType);
        request.setEmployee(isEmployee);
        request.setAffiliate(isAffiliate);
        request.setCustomerOverTwoYears(isCustomerOverTwoYears);
        request.setSourceCurrency("USD");
        request.setTargetCurrency("PKR");
        return request;
    }

    @Test
    void testConvertCurrency_WithEmployeeDiscount() {
        BillRequest request = createRequest("electronics", true, false, false, 200.0);
        when(exchangeRateConfig.getExchangeRate("USD", "PKR")).thenReturn(278.5);

        BillResponse response = exchangeRatesService.calculateBill(request);

        assertEquals(200.0, response.getOriginalAmount());
        assertEquals("PKR", response.getCurrency());
        assertTrue(response.getDiscountApplied() > 0);
        assertTrue(response.getFinalAmount() < 200.0);
        assertTrue(response.getConvertedAmount() > 0);
    }

    @Test
    void testConvertCurrency_WithAffiliateDiscount() {
        BillRequest request = createRequest("clothing", false, true, false, 200.0);
        when(exchangeRateConfig.getExchangeRate("USD", "PKR")).thenReturn(278.5);

        BillResponse response = exchangeRatesService.calculateBill(request);

        assertEquals(200.0, response.getOriginalAmount());
        assertEquals("PKR", response.getCurrency());
        assertTrue(response.getDiscountApplied() > 0);
    }

    @Test
    void testConvertCurrency_WithCustomerOverTwoYearsDiscount() {
        BillRequest request = createRequest("furniture", false, false, true, 200.0);
        when(exchangeRateConfig.getExchangeRate("USD", "PKR")).thenReturn(278.5);

        BillResponse response = exchangeRatesService.calculateBill(request);

        assertEquals(200.0, response.getOriginalAmount());
        assertTrue(response.getDiscountApplied() > 0);
    }

    @Test
    void testConvertCurrency_Grocery_NoPercentageDiscount() {
        BillRequest request = createRequest("groceries", false, false, true, 200.0);
        when(exchangeRateConfig.getExchangeRate("USD", "PKR")).thenReturn(278.5);

        BillResponse response = exchangeRatesService.calculateBill(request);

        // Only flat discount applies: 5 for every $100 => $10
        assertEquals(200.0, response.getOriginalAmount());
        assertEquals(10.0, response.getDiscountApplied());
    }

    @Test
    void testConvertCurrency_NoDiscounts() {
        BillRequest request = createRequest("books", false, false, false, 90.0);
        when(exchangeRateConfig.getExchangeRate("USD", "PKR")).thenReturn(278.5);

        BillResponse response = exchangeRatesService.calculateBill(request);

        assertEquals(90.0, response.getOriginalAmount());
        assertEquals(0.0, response.getDiscountApplied());
    }
}

