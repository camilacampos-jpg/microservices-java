package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.clients.BcbClient;
import br.edu.atitus.currencyapi.dtos.BcbCotacao;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BcbClientService {

    private static final Logger log = LoggerFactory.getLogger(BcbClientService.class);

    private final BcbClient bcbClient;

    public BcbClientService(BcbClient bcbClient) {
        this.bcbClient = bcbClient;
    }

    /**
     * Retorna a cotação de venda (em reais) da moeda na data informada,
     * ou null quando o BCB responde sem cotação para essa moeda/data.
     *
     * Ordem dos interceptadores: Cache (99) -> Retry (100) -> Circuit Breaker (101) -> chamada ao BCB.
     * O "unless" impede que respostas sem cotação (null) fiquem no cache;
     * falhas de comunicação lançam exceção e também não são armazenadas.
     */
    @Cacheable(
            value = "bcb-api",
            key = "#moeda + '-' + #dataCotacao",
            unless = "#result == null"
    )
    @Retry(name = "bcb-api")
    @CircuitBreaker(name = "bcb-api")
    public Double getCotacaoVenda(String moeda, String dataCotacao) {
        // Log para observar nos testes quando a chamada realmente chega ao BCB
        log.info("Chamando API do BCB: moeda={} data={}", moeda, dataCotacao);

        var response = bcbClient.getCotacaoMoedaDia(moeda, dataCotacao);
        List<BcbCotacao> cotacoes = (response == null || response.value() == null)
                ? List.of()
                : response.value();

        if (cotacoes.isEmpty()) {
            log.info("BCB nao possui cotacao para moeda={} data={}", moeda, dataCotacao);
            return null;
        }

        // Prioriza o boletim de fechamento (PTAX); se não houver, usa o último boletim do dia
        BcbCotacao cotacao = cotacoes.stream()
                .filter(c -> c.tipoBoletim() != null && c.tipoBoletim().startsWith("Fechamento"))
                .findFirst()
                .orElse(cotacoes.getLast());

        return cotacao.cotacaoVenda();
    }
}
