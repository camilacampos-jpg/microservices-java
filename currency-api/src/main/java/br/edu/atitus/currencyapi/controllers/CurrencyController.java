package br.edu.atitus.currencyapi.controllers;

import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.services.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("currencies")
public class CurrencyController {
    private final CurrencyService service;

    public CurrencyController(@Qualifier("currencyServiceJpa") CurrencyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CurrencyResponse> getBySourceAndTarget(
            @RequestParam String source,
            @RequestParam String target
    ) throws Exception {
        var response = service.findBySourceCurrencyAndTargetCurrency(source, target);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex){
        return ResponseEntity.status(500).body("Ops!!! Algo deu errado.");
    }
   }