package br.edu.atitus.currencyapi.services;

import br.edu.atitus.currencyapi.dtos.ProductRequest;
import br.edu.atitus.currencyapi.dtos.ProductResponse;
import br.edu.atitus.currencyapi.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse findById(Long id, String targetCurrency) throws Exception;
    Page<ProductResponse> findAll(Pageable pageable, String targetCurrency) throws Exception;
    ProductEntity save(ProductRequest request) throws Exception;
}
