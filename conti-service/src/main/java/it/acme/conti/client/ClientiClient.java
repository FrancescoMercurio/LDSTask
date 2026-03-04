package it.acme.conti.client;

import it.acme.conti.dto.ClienteSummary;
import it.acme.conti.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientiClient {

    private final WebClient clientiWebClient;

    public Optional<ClienteSummary> getClienteById(String clienteId) {
        try {
            ClienteSummary cliente = clientiWebClient.get()
                    .uri("/api/clienti/{id}", clienteId)
                    .retrieve()
                    .bodyToMono(ClienteSummary.class)
                    .block();
            return Optional.ofNullable(cliente);
        } catch (WebClientResponseException.NotFound ex) {
            return Optional.empty();
        } catch (WebClientResponseException ex) {
            throw new ExternalServiceException("Errore da clienti-service: HTTP " + ex.getStatusCode().value());
        } catch (WebClientRequestException ex) {
            log.error("Impossibile raggiungere clienti-service", ex);
            throw new ExternalServiceException("Clienti-service non raggiungibile");
        }
    }
}
