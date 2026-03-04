package it.acme.conti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account details enriched with optional customer data")
public class ContoDetailsResponse {

    @Schema(example = "65f1e6e779fca76a8f777777")
    private String id;

    @Schema(example = "IT60X0542811101000000123456")
    private String iban;

    @Schema(example = "1000.00")
    private BigDecimal saldo;

    @Schema(example = "2026-03-04")
    private LocalDate dataApertura;

    @Schema(example = "65f1e6e779fca76a8f123456")
    private String clienteId;

    private ClienteSummary cliente;
}
