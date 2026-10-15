package br.edu.atitus.currencyapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Resposta da API PTAX do BCB: a lista "value" traz os boletins do dia (pode vir vazia)
@JsonIgnoreProperties(ignoreUnknown = true)
public record BcbResponse(
        List<BcbCotacao> value
) {
}
