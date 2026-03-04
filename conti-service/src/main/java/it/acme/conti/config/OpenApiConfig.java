package it.acme.conti.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Conti Service API")
                        .description("REST API for account management and transactions")
                        .version("v1")
                        .contact(new Contact().name("LDS Task Team").email("dev@example.com"))
                        .license(new License().name("Apache 2.0")));
    }
}
