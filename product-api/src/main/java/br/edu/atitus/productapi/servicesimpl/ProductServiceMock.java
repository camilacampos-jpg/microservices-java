package br.edu.atitus.productapi.servicesimpl;

import br.edu.atitus.productapi.dtos.ProductRequest;
import br.edu.atitus.productapi.dtos.ProductResponse;
import br.edu.atitus.productapi.entities.ProductEntity;
import br.edu.atitus.productapi.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceMock implements ProductService {

    private final List<ProductEntity> database = new ArrayList<>();

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.promotion.message:Nenhuma Promoção Ativa}")
    private String promotionMessage;

    public ProductServiceMock() {
        database.add(new ProductEntity(1L, "iPhone 15 128GB", "Apple", "iPhone 15", "USD", 799.00, "http://image.com/image.jpg"));
        database.add(new ProductEntity(2L, "iPhone 15 Pro 256GB", "Apple", "iPhone 15 Pro", "USD", 1099.00, "http://image.com/image.jpg"));
        database.add(new ProductEntity(3L, "Galaxy S24 256GB", "Samsung", "Galaxy S24", "USD", 859.00, "http://image.com/image.jpg"));
        database.add(new ProductEntity(4L, "Galaxy S24 Ultra 512GB", "Samsung", "Galaxy S24 Ultra", "USD", 1299.00, "http://image.com/image.jpg"));
    }

    @Override
    public ProductEntity save(ProductRequest dto) throws Exception {
        return null;
    }

    @Override
    public ProductResponse findById(Long id, String targetCurrency) throws Exception {
        ProductEntity product = database.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        return ProductResponse.fromEntity(product, buildEnvironment(), promotionMessage, targetCurrency, 0);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, String targetCurrency) throws Exception {
        String environment = buildEnvironment();

        // 1. Converte a lista em memória para DTOs
        List<ProductResponse> dtoList = database.stream()
                .map(product -> ProductResponse.fromEntity(
                        product,
                        environment,
                        promotionMessage,
                        targetCurrency,
                        0
                ))
                .toList();

        // 2. Recorta a página solicitada a partir do Pageable
        int totalElements = dtoList.size();
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), totalElements);

        List<ProductResponse> pagedContent = (fromIndex > totalElements)
                ? List.of()
                : dtoList.subList(fromIndex, toIndex);

        // 3. Devolve no formato Page do Spring Data
        return new PageImpl<>(pagedContent, pageable, totalElements);
    }

    private String buildEnvironment() {
        return "Product API running in port " + serverPort;
    }
}
