package it.acme.conti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conti")
public class Conto {

    @Id
    private String id;

    @Indexed(unique = true)
    private String iban;

    private BigDecimal saldo;

    private LocalDate dataApertura;

    private String clienteId;
}
