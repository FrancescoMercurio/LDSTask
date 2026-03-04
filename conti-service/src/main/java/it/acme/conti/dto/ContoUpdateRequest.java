package it.acme.conti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for updating account data")
public class ContoUpdateRequest {

    @NotBlank(message = "L'IBAN e obbligatorio")
    @Schema(description = "Unique IBAN", example = "IT60X0542811101000000123456")
    private String iban;

    @NotNull(message = "La data apertura e obbligatoria")
    @Schema(description = "Account opening date", example = "2026-03-04")
    private LocalDate dataApertura;

    @NotBlank(message = "Il clienteId e obbligatorio")
    @Schema(description = "Customer id from clienti-service", example = "65f1e6e779fca76a8f123456")
    private String clienteId;
}
