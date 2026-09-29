package br.edu.atitus.currencyapi.dtos;

import br.edu.atitus.currencyapi.entities.ProductEntity;

public record ProductResponse(
        Long id,
        String brand,
        String model,
        String description,
        String currency,
        double price,
        String image,

        String environment,
        String promotionMessage,
        String targetCurrency,
        double convertedPrice
) {
    public static ProductResponse fromEntity(ProductEntity entity, String environment, String promotionMessage, String targetCurrency, double convertedPrice) {
        return new ProductResponse(
                entity.getId(),
                entity.getDescription(),
                entity.getBrand(),
                entity.getModel(),
                entity.getCurrency(),
                entity.getPrice(),
                entity.getImage(),
                environment,
                promotionMessage,
                targetCurrency,
                convertedPrice
                );
    }
}
