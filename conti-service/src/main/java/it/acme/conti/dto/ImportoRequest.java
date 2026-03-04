package it.acme.conti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Amount payload for deposit and withdrawal")
public class ImportoRequest {

    @NotNull(message = "L'importo e obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "L'importo deve essere maggiore di zero")
    @Schema(example = "150.00")
    private BigDecimal importo;
}
