package br.edu.atitus.productapi.clients;

import br.edu.atitus.productapi.dtos.CurrencyResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/currencies")
public interface CurrencyClient {

    @GetExchange
    CurrencyResponse getCurrency(
            @RequestParam("source") String source,
            @RequestParam("target") String target
    );
}
