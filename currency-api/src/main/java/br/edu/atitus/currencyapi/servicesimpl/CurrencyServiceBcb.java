package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.repositories.CurrencyRepository;
import br.edu.atitus.currencyapi.services.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceBcb implements CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyServiceBcb.class);

    // A API PTAX cota as moedas em reais; o real não aparece na API e vale 1
    private static final String MOEDA_BASE = "BRL";

    private final BcbClientService bcbClientService;
    private final CurrencyRepository repository;

    public CurrencyServiceBcb(BcbClientService bcbClientService, CurrencyRepository repository) {
        this.bcbClientService = bcbClientService;
        this.repository = repository;
    }

    @Value("${server.port:8100}")
    private String serverPort;

    // Data fixa de um dia útil com cotação disponível no BCB (formato MM-dd-yyyy)
    @Value("${app.bcb.data-cotacao:10-14-2026}")
    private String dataCotacao;

    @Override
    public CurrencyResponse findBySourceCurrencyAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws Exception {
        String source = sourceCurrency.toUpperCase();
        String target = targetCurrency.toUpperCase();
        String environment = "Currency API running in Port: " + serverPort;

        // 1. Tenta obter a cotação na API do Banco Central
        try {
            Double conversionRate = getConversionRateFromBcb(source, target);
            if (conversionRate != null) {
                return new CurrencyResponse(source, target, conversionRate,
                        environment + " - Fonte: BCB (" + dataCotacao + ")");
            }
            log.warn("Sem cotacao no BCB para {} -> {} em {}. Usando banco local.", source, target, dataCotacao);
        } catch (Exception e) {
            // Falha de comunicação, timeout, retries esgotados ou Circuit Breaker aberto
            log.warn("Falha ao consultar o BCB para {} -> {}: {}. Usando banco local.", source, target, e.getMessage());
        }

        // 2. Fallback Pattern: usa a cotação cadastrada no banco de dados local
        var currency = repository.findBySourceCurrencyAndTargetCurrency(source, target)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada"));
        return CurrencyResponse.fromEntity(currency, environment + " - Fonte: banco local");
    }

    /**
     * Calcula a taxa de conversão source -> target a partir das cotações em reais do BCB.
     * Ex.: USD -> EUR = (BRL por 1 USD) / (BRL por 1 EUR).
     * Retorna null quando o BCB não possui cotação para alguma das moedas.
     */
    private Double getConversionRateFromBcb(String source, String target) {
        if (source.equals(target)) {
            return 1.0;
        }
        Double sourceInBrl = getCotacaoEmReais(source);
        if (sourceInBrl == null) {
            return null;
        }
        Double targetInBrl = getCotacaoEmReais(target);
        if (targetInBrl == null || targetInBrl == 0) {
            return null;
        }
        return sourceInBrl / targetInBrl;
    }

    private Double getCotacaoEmReais(String moeda) {
        if (MOEDA_BASE.equals(moeda)) {
            return 1.0;
        }
        return bcbClientService.getCotacaoVenda(moeda, dataCotacao);
    }
}
