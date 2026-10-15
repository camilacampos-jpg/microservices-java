package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.clients.CurrencyClient;
import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CurrencyClientService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyClientService.class);

    private final CurrencyClient currencyClient;

    public CurrencyClientService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    // Ordem dos interceptadores: Cache (99) -> Retry (100) -> Circuit Breaker (101) -> chamada remota
    @Cacheable(
            value = "currency-api",
            key = "#source + '-' + #target"
    )
    @Retry(name = "currency-api")
    @CircuitBreaker(name = "currency-api")
    public CurrencyResponse getCurrency(String source, String target) {
        // Log para observar nos testes quando a chamada realmente chega ao currency-api
        log.info("Chamando currency-api: {} -> {}", source, target);
        return currencyClient.getCurrency(source, target);
    }
}
