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

    public ProductServiceJpa(ProductRepository repository) {
        this.repository = repository;
    }

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.promotion.message:Nenhuma Promoção Ativa}")
    private String promotionMessage;


    @Override
    public ProductResponse findById(Long id, String targetCurrency) throws Exception {
        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        String environment = "Product API running in port " + serverPort;
        return ProductResponse.fromEntity(
                product,
                environment,
                promotionMessage,
                targetCurrency,
                0
        );
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, String targetCurrency) throws Exception {
        var products = repository.findAll(pageable);
        String environment = "Product API running in port " + serverPort;

        return products.map(
                entity -> ProductResponse.fromEntity(
                        entity,
                        environment,
                        promotionMessage,
                        targetCurrency,
                        0
                )
        );
    }

    @Override
    public ProductEntity save(ProductRequest request) throws Exception {
        return null;
    }
}
