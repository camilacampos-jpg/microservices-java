package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.dtos.ProductRequest;
import br.edu.atitus.currencyapi.dtos.ProductResponse;
import br.edu.atitus.currencyapi.entities.ProductEntity;
import br.edu.atitus.currencyapi.repositories.ProductRepository;
import br.edu.atitus.currencyapi.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceJpa implements ProductService {

    private final ProductRepository repository;
    private final CurrencyClientService currencyClientService;

    public ProductServiceJpa(
            ProductRepository repository,
            CurrencyClientService currencyClientService) {
        this.repository = repository;
        this.currencyClientService = currencyClientService;
    }

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.promotion.message:Nenhuma Promoção Ativa}")
    private String promotionMessage;


    @Override
    public ProductResponse findById(Long id, String targetCurrency) throws Exception {
        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return toResponseWithConversion(product, targetCurrency);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, String targetCurrency) throws Exception {
        var products = repository.findAll(pageable);
        return products.map(entity -> toResponseWithConversion(entity, targetCurrency));
    }

    @Override
    public ProductEntity save(ProductRequest request) throws Exception {
        return null;
    }

    private ProductResponse toResponseWithConversion(ProductEntity entity, String targetCurrency) {
        String environment = "Product API running in port " + serverPort;
        double convertedValue;

        // Fallback Pattern: se a conversão falhar (mesmo após Retry/Circuit Breaker),
        // retorna o produto com -1 em convertedPrice e o erro no campo environment
        try {
            var currency = currencyClientService.getCurrency(
                    entity.getCurrency(),
                    targetCurrency
            );
            convertedValue = entity.getPrice() * currency.conversionRate();
            environment += " - " + currency.environment();
        } catch (Exception e) {
            convertedValue = -1;
            environment += " - ERROR: " + e.getMessage();
        }

        return ProductResponse.fromEntity(
                entity,
                environment,
                promotionMessage,
                targetCurrency,
                convertedValue
        );
    }
}
