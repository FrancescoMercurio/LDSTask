package it.acme.clienti.service;

import it.acme.clienti.dto.ClienteRequest;
import it.acme.clienti.dto.ClienteResponse;
import it.acme.clienti.exception.ResourceNotFoundException;
import it.acme.clienti.model.Cliente;
import it.acme.clienti.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse findById(String id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con id " + id + " non trovato"));
        return toResponse(cliente);
    }

    public ClienteResponse create(ClienteRequest request) {
        Cliente cliente = Cliente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .codiceFiscale(request.getCodiceFiscale())
                .email(request.getEmail())
                .indirizzo(request.getIndirizzo())
                .build();
        return toResponse(clienteRepository.save(cliente));
    }

    public void deleteById(String id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente con id " + id + " non trovato");
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cognome(cliente.getCognome())
                .codiceFiscale(cliente.getCodiceFiscale())
                .email(cliente.getEmail())
                .indirizzo(cliente.getIndirizzo())
                .build();
    }
}
