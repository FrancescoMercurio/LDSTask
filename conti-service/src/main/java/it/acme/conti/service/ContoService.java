package it.acme.conti.service;

import it.acme.conti.client.ClientiClient;
import it.acme.conti.dto.ClienteSummary;
import it.acme.conti.dto.ContoCreateRequest;
import it.acme.conti.dto.ContoDetailsResponse;
import it.acme.conti.dto.ContoResponse;
import it.acme.conti.dto.ContoUpdateRequest;
import it.acme.conti.exception.BadRequestException;
import it.acme.conti.exception.ConflictException;
import it.acme.conti.exception.ResourceNotFoundException;
import it.acme.conti.model.Conto;
import it.acme.conti.repository.ContoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContoService {

    private final ContoRepository contoRepository;
    private final ClientiClient clientiClient;

    public List<ContoResponse> findAll() {
        return contoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ContoDetailsResponse findDetailsById(String id) {
        Conto conto = getContoOrThrow(id);
        ClienteSummary cliente = clientiClient.getClienteById(conto.getClienteId()).orElse(null);
        return toDetailsResponse(conto, cliente);
    }

    public ContoResponse create(ContoCreateRequest request) {
        if (contoRepository.existsByIban(request.getIban())) {
            throw new ConflictException("Esiste gia un conto con IBAN " + request.getIban());
        }

        Conto conto = Conto.builder()
                .iban(request.getIban())
                .saldo(request.getSaldo())
                .dataApertura(request.getDataApertura() != null ? request.getDataApertura() : LocalDate.now())
                .clienteId(request.getClienteId())
                .build();

        return toResponse(contoRepository.save(conto));
    }

    public ContoResponse update(String id, ContoUpdateRequest request) {
        Conto conto = getContoOrThrow(id);

        if (!conto.getIban().equals(request.getIban()) && contoRepository.existsByIban(request.getIban())) {
            throw new ConflictException("Esiste gia un conto con IBAN " + request.getIban());
        }

        conto.setIban(request.getIban());
        conto.setDataApertura(request.getDataApertura());
        conto.setClienteId(request.getClienteId());

        return toResponse(contoRepository.save(conto));
    }

    public void delete(String id) {
        Conto conto = getContoOrThrow(id);
        contoRepository.delete(conto);
    }

    public ContoResponse deposito(String id, BigDecimal importo) {
        if (importo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("L'importo deve essere maggiore di zero");
        }

        Conto conto = getContoOrThrow(id);
        conto.setSaldo(conto.getSaldo().add(importo));
        return toResponse(contoRepository.save(conto));
    }

    public ContoResponse prelievo(String id, BigDecimal importo) {
        if (importo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("L'importo deve essere maggiore di zero");
        }

        Conto conto = getContoOrThrow(id);
        BigDecimal nuovoSaldo = conto.getSaldo().subtract(importo);
        if (nuovoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Saldo insufficiente per completare il prelievo");
        }

        conto.setSaldo(nuovoSaldo);
        return toResponse(contoRepository.save(conto));
    }

    private Conto getContoOrThrow(String id) {
        return contoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conto con id " + id + " non trovato"));
    }

    private ContoResponse toResponse(Conto conto) {
        return ContoResponse.builder()
                .id(conto.getId())
                .iban(conto.getIban())
                .saldo(conto.getSaldo())
                .dataApertura(conto.getDataApertura())
                .clienteId(conto.getClienteId())
                .build();
    }

    private ContoDetailsResponse toDetailsResponse(Conto conto, ClienteSummary cliente) {
        return ContoDetailsResponse.builder()
                .id(conto.getId())
                .iban(conto.getIban())
                .saldo(conto.getSaldo())
                .dataApertura(conto.getDataApertura())
                .clienteId(conto.getClienteId())
                .cliente(cliente)
                .build();
    }
}
