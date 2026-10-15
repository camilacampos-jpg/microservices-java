package br.edu.atitus.currencyapi.dtos;

public record CurrencyResponse(
        String sourceCurrency,
        String targetCurrency,
        Double conversionRate,
        String environment
) {
}
