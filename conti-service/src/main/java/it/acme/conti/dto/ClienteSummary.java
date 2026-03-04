package it.acme.conti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer summary returned by clienti-service")
public class ClienteSummary {

    @Schema(example = "65f1e6e779fca76a8f123456")
    private String id;

    @Schema(example = "Mario")
    private String nome;

    @Schema(example = "Rossi")
    private String cognome;

    @Schema(example = "RSSMRA80A01H501U")
    private String codiceFiscale;

    @Schema(example = "mario.rossi@example.com")
    private String email;

    @Schema(example = "Via Roma 1, Milano")
    private String indirizzo;
}
