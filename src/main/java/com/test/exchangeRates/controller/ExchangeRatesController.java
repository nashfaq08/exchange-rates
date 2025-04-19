package com.test.exchangeRates.controller;

import com.test.exchangeRates.dto.request.BillRequest;
import com.test.exchangeRates.dto.response.BillResponse;
import com.test.exchangeRates.service.ExchangeRatesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeRatesController {

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @PostMapping("/calculate")
    public ResponseEntity<BillResponse> calculate(@Valid @RequestBody BillRequest request) {
        BillResponse response = exchangeRatesService.calculateBill(request);
        return ResponseEntity.ok(response);
    }

}
