package it.acme.clienti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clienti")
public class Cliente {

    @Id
    private String id;

    private String nome;

    private String cognome;

    @Indexed(unique = true)
    private String codiceFiscale;

    @Indexed(unique = true)
    private String email;

    private String indirizzo;
}
