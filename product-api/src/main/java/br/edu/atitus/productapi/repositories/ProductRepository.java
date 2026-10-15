package br.edu.atitus.currencyapi.repositories;

import br.edu.atitus.currencyapi.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
