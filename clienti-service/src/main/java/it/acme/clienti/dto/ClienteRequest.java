package it.acme.clienti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating a customer")
public class ClienteRequest {

    @NotBlank(message = "Il nome e obbligatorio")
    @Size(max = 100, message = "Il nome non puo superare 100 caratteri")
    @Schema(description = "Customer first name", example = "Mario")
    private String nome;

    @NotBlank(message = "Il cognome e obbligatorio")
    @Size(max = 100, message = "Il cognome non puo superare 100 caratteri")
    @Schema(description = "Customer last name", example = "Rossi")
    private String cognome;

    @NotBlank(message = "Il codice fiscale e obbligatorio")
    @Size(max = 16, min = 16, message = "Il codice fiscale deve avere 16 caratteri")
    @Schema(description = "Italian tax code", example = "RSSMRA80A01H501U")
    private String codiceFiscale;

    @NotBlank(message = "L'email e obbligatoria")
    @Email(message = "Email non valida")
    @Schema(description = "Customer email", example = "mario.rossi@example.com")
    private String email;

    @NotBlank(message = "L'indirizzo e obbligatorio")
    @Size(max = 255, message = "L'indirizzo non puo superare 255 caratteri")
    @Schema(description = "Customer address", example = "Via Roma 1, Milano")
    private String indirizzo;
}
