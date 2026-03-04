package it.acme.clienti.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clientiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clienti Service API")
                        .description("REST API for customer management")
                        .version("v1")
                        .contact(new Contact().name("LDS Task Team").email("dev@example.com"))
                        .license(new License().name("Apache 2.0")));
    }
}
