package it.acme.clienti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer response")
public class ClienteResponse {

    @Schema(description = "Customer id", example = "65f1e6e779fca76a8f123456")
    private String id;

    @Schema(description = "Customer first name", example = "Mario")
    private String nome;

    @Schema(description = "Customer last name", example = "Rossi")
    private String cognome;

    @Schema(description = "Italian tax code", example = "RSSMRA80A01H501U")
    private String codiceFiscale;

    @Schema(description = "Customer email", example = "mario.rossi@example.com")
    private String email;

    @Schema(description = "Customer address", example = "Via Roma 1, Milano")
    private String indirizzo;
}
