package br.edu.atitus.currencyapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Um boletim de cotação da API PTAX (valores em reais para 1 unidade da moeda)
@JsonIgnoreProperties(ignoreUnknown = true)
public record BcbCotacao(
        Double cotacaoCompra,
        Double cotacaoVenda,
        String dataHoraCotacao,
        String tipoBoletim
) {
}
