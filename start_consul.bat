@echo off
echo Iniciando Consul...
start "Consul" consul agent -dev
echo Aguardando Consul iniciar...
timeout /t 10 /nobreak > nul
echo Carregando configuracoes...

REM ===== product-api =====
consul kv put config/product-api/server.port "8000"
consul kv put config/product-api/spring.datasource.url "jdbc:postgresql://localhost/db_product"
consul kv put config/product-api/spring.datasource.username "postgres"
consul kv put config/product-api/spring.datasource.password "postgres"

consul kv put config/product-api/app.promotion.message "Promoção Semana do Cliente"

consul kv put config/product-api,en/app.promotion.message "Christmas Promotion!!!!"

REM ===== currency-api =====
consul kv put config/currency-api/server.port "8100"
consul kv put config/currency-api/spring.datasource.url "jdbc:postgresql://localhost/db_currency"
consul kv put config/currency-api/spring.datasource.username "postgres"
consul kv put config/currency-api/spring.datasource.password "postgres"

REM Configuracao para todos os microservicos
consul kv put config/application/management.endpoints.web.exposure.include "*"
consul kv put config/application/management.endpoint.health.show-components "always"
consul kv put config/application/management.endpoint.health.show-details "always"
consul kv put config/application/management.health.livenessstate.enabled "true"
consul kv put config/application/management.health.readinessstate.enabled "true"
consul kv put config/application/management.health.db.enabled "true"

REM Configuracao do Retry
consul kv put config/product-api/resilience4j.retry.instances.currency-api.max-attempts "3"
consul kv put config/product-api/resilience4j.retry.instances.currency-api.wait-duration "1s"

REM Configuracao do Circuit Breaker
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.sliding-window-type "COUNT_BASED"
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.sliding-window-size "5"
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.minimum-number-of-calls "5"
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.failure-rate-threshold "50"
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.wait-duration-in-open-state "100s"

REM Nao repetir chamadas quando o Circuit Breaker estiver aberto
consul kv put config/product-api/resilience4j.retry.instances.currency-api.ignore-exceptions[0] "io.github.resilience4j.circuitbreaker.CallNotPermittedException"

REM Erros 4xx do currency-api (ex.: 404 cotacao inexistente) nao sao falhas de disponibilidade:
REM nao repetir a chamada e nao contar para abrir o Circuit Breaker
consul kv put config/product-api/resilience4j.retry.instances.currency-api.ignore-exceptions[1] "org.springframework.web.client.HttpClientErrorException"
consul kv put config/product-api/resilience4j.circuitbreaker.instances.currency-api.ignore-exceptions[0] "org.springframework.web.client.HttpClientErrorException"

REM Configuracao do cache utilizando Caffeine
consul kv put config/product-api/spring.cache.type "caffeine"
consul kv put config/product-api/spring.cache.cache-names "currency-api"
consul kv put config/product-api/spring.cache.caffeine.spec "maximumSize=1000,expireAfterWrite=10m"

REM Ordem de execucao dos aspectos de resiliencia e cache
consul kv put config/product-api/resilience4j.retry.retry-aspect-order "100"
consul kv put config/product-api/resilience4j.circuitbreaker.circuit-breaker-aspect-order "101"

REM ===== currency-api: integracao com a API do Banco Central (PTAX) =====
REM Data fixa de um dia util com cotacao disponivel (formato MM-dd-yyyy)
consul kv put config/currency-api/app.bcb.base-url "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata"
consul kv put config/currency-api/app.bcb.data-cotacao "10-14-2026"

REM Retry na chamada ao BCB
consul kv put config/currency-api/resilience4j.retry.instances.bcb-api.max-attempts "3"
consul kv put config/currency-api/resilience4j.retry.instances.bcb-api.wait-duration "1s"
consul kv put config/currency-api/resilience4j.retry.instances.bcb-api.ignore-exceptions[0] "io.github.resilience4j.circuitbreaker.CallNotPermittedException"

REM Circuit Breaker na chamada ao BCB
consul kv put config/currency-api/resilience4j.circuitbreaker.instances.bcb-api.sliding-window-type "COUNT_BASED"
consul kv put config/currency-api/resilience4j.circuitbreaker.instances.bcb-api.sliding-window-size "5"
consul kv put config/currency-api/resilience4j.circuitbreaker.instances.bcb-api.minimum-number-of-calls "5"
consul kv put config/currency-api/resilience4j.circuitbreaker.instances.bcb-api.failure-rate-threshold "50"
consul kv put config/currency-api/resilience4j.circuitbreaker.instances.bcb-api.wait-duration-in-open-state "100s"

REM Cache das cotacoes do BCB utilizando Caffeine
consul kv put config/currency-api/spring.cache.type "caffeine"
consul kv put config/currency-api/spring.cache.cache-names "bcb-api"
consul kv put config/currency-api/spring.cache.caffeine.spec "maximumSize=1000,expireAfterWrite=10m"

REM Ordem de execucao: Cache (99, na aplicacao) -> Retry (100) -> Circuit Breaker (101)
consul kv put config/currency-api/resilience4j.retry.retry-aspect-order "100"
consul kv put config/currency-api/resilience4j.circuitbreaker.circuit-breaker-aspect-order "101"

echo.
echo Consul iniciado e configuracoes carregadas!
pause
