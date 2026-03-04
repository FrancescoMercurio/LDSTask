package it.acme.conti.repository;

import it.acme.conti.model.Conto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContoRepository extends MongoRepository<Conto, String> {

    boolean existsByIban(String iban);
}
