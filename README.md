# 💱 Exchange Rates Service

A Spring Boot application that calculates the payable amount for a bill in a target currency after applying relevant discounts and converting the currency using real-time exchange rates from a third-party service.

---

## 🧩 Features

- 🎯 Currency conversion using real-time exchange rates (ExchangeRate-API).
- 🧾 Apply discounts based on user type and bill amount.
- 🚀 REST API endpoint to process conversion requests.
- 🔁 Caching with Redis to reduce third-party API calls.
- 📊 Actuator support for cache inspection and health checks.

---

## 🔧 Configuration

Update the following in `application.yml`:

```yaml
exchange:
  api:
    key: YOUR_EXCHANGE_RATE_API_KEY
    url: https://v6.exchangerate-api.com/v6

spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```
---

## 🌍 Exchange Rate Integration

This project uses the ExchangeRate-API to retrieve real-time currency conversion rates. Results are cached to improve performance and reduce API calls.

---

## 🔐 Authentication

The `/calculate` endpoint is protected using **Basic Authentication**.

Credentials are defined in your `application.yml` file:

```yaml
spring:
  security:
    user:
      name: admin
      password: ******
```
---

## 📍 `/calculate` Endpoint - Currency Conversion & Discount

This endpoint calculates the final amount after applying discounts and converts the total to a target currency using exchange rates.

``` 
POST /api/exchangeRates/calculate

{
  "amount": 200,
  "itemType": "electronics",
  "employee": true,
  "affiliate": false,
  "customerOverTwoYears": false,
  "sourceCurrency": "USD",
  "targetCurrency": "PKR"
}

```
---

## 📦 Run Tests

To execute all unit and integration tests:
```
mvn test
```
---

## 📊 Generate Code Coverage Report (JaCoCo)

To generate the jacoco code coverage report:
```
mvn clean test jacoco:report
```
After successful execution, open the report from:
```
target/site/jacoco/index.html
```
