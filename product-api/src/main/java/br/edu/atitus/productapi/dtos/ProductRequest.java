package br.edu.atitus.currencyapi.dtos;

public record ProductRequest(
        String brand,
        String model,
        String description,
        String currency,
        double price,
        String image
) {
}
