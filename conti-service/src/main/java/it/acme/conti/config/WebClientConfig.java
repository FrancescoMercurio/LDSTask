package it.acme.conti.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient clientiWebClient(WebClient.Builder builder,
                                      @Value("${clients.clienti.base-url}") String clientiBaseUrl) {
        return builder.baseUrl(clientiBaseUrl).build();
    }
}
