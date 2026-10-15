package br.edu.atitus.currencyapi.clients;

import br.edu.atitus.currencyapi.dtos.BcbResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

// Cliente HTTP da API PTAX do Banco Central do Brasil (base URL definida no BcbClientConfig)
@HttpExchange
public interface BcbClient {

    // moeda: código da moeda (ex.: USD); dataCotacao: data no formato MM-dd-yyyy
    @GetExchange("/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)"
            + "?@moeda='{moeda}'&@dataCotacao='{dataCotacao}'&$format=json")
    BcbResponse getCotacaoMoedaDia(
            @PathVariable("moeda") String moeda,
            @PathVariable("dataCotacao") String dataCotacao
    );
}
