@echo off
echo Iniciando Consul...
start "Consul" consul agent -dev
echo Aguardando Consul iniciar...
timeout /t 10 /nobreak > nul
echo Carregando configuracoes...

consul kv put config/product-api/server.port "8000"
consul kv put config/product-api/spring.datasource.url "jdbc:postgresql://localhost/db_product"
consul kv put config/product-api/spring.datasource.username "postgres"
consul kv put config/product-api/spring.datasource.password "postgres"

consul kv put config/product-api/app.promotion.message "Customer Week Promotion"
consul kv put config/product-api,it/app.promotion.message "Promozione Settimana del Cliente"

echo.
echo Consul iniciado e configuracoes carregadas!
pause