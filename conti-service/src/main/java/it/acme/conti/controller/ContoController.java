package it.acme.conti.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.acme.conti.dto.ContoCreateRequest;
import it.acme.conti.dto.ContoDetailsResponse;
import it.acme.conti.dto.ContoResponse;
import it.acme.conti.dto.ContoUpdateRequest;
import it.acme.conti.dto.ImportoRequest;
import it.acme.conti.service.ContoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conti")
@RequiredArgsConstructor
public class ContoController {

    private final ContoService contoService;

    @GetMapping
    @Operation(summary = "List all accounts")
    public ResponseEntity<List<ContoResponse>> getAllConti() {
        return ResponseEntity.ok(contoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details enriched with customer data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<ContoDetailsResponse> getContoById(@PathVariable String id) {
        return ResponseEntity.ok(contoService.findDetailsById(id));
    }

    @PostMapping
    @Operation(summary = "Open new account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicated IBAN")
    })
    public ResponseEntity<ContoResponse> createConto(@Valid @RequestBody ContoCreateRequest request) {
        ContoResponse created = contoService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account data")
    public ResponseEntity<ContoResponse> updateConto(@PathVariable String id,
                                                     @Valid @RequestBody ContoUpdateRequest request) {
        return ResponseEntity.ok(contoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Close account")
    public ResponseEntity<Void> deleteConto(@PathVariable String id) {
        contoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposito")
    @Operation(summary = "Deposit amount into account")
    public ResponseEntity<ContoResponse> deposito(@PathVariable String id,
                                                  @Valid @RequestBody ImportoRequest request) {
        return ResponseEntity.ok(contoService.deposito(id, request.getImporto()));
    }

    @PostMapping("/{id}/prelievo")
    @Operation(summary = "Withdraw amount from account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdraw successful"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or insufficient balance")
    })
    public ResponseEntity<ContoResponse> prelievo(@PathVariable String id,
                                                  @Valid @RequestBody ImportoRequest request) {
        return ResponseEntity.ok(contoService.prelievo(id, request.getImporto()));
    }
}
