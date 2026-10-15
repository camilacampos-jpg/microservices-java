package br.edu.atitus.currencyapi.configs;

import br.edu.atitus.currencyapi.clients.BcbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class BcbClientConfig {

    @Value("${app.bcb.base-url:https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata}")
    private String baseUrl;

    @Bean
    BcbClient getBcbClient(RestClient.Builder builder) {
        // Timeout Pattern: limita o tempo de conexão e de leitura da resposta do BCB
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        RestClient restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(BcbClient.class);
    }
}
