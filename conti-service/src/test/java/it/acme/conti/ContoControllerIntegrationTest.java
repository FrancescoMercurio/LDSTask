package it.acme.conti;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import it.acme.conti.dto.ContoCreateRequest;
import it.acme.conti.model.Conto;
import it.acme.conti.repository.ContoRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContoControllerIntegrationTest {

    private static final MongodExecutable MONGO_EXECUTABLE;
    private static final MongodProcess MONGO_PROCESS;
    private static final int MONGO_PORT;
    private static final MockWebServer MOCK_WEB_SERVER;

    static {
        try {
            MONGO_PORT = Network.getFreeServerPort();
            MongodConfig config = MongodConfig.builder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
                    .build();
            MONGO_EXECUTABLE = MongodStarter.getDefaultInstance().prepare(config);
            MONGO_PROCESS = MONGO_EXECUTABLE.start();

            MOCK_WEB_SERVER = new MockWebServer();
            MOCK_WEB_SERVER.start();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to start test infrastructure", ex);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:" + MONGO_PORT + "/conti_test_db");
        registry.add("clients.clienti.base-url", () -> "http://localhost:" + MOCK_WEB_SERVER.getPort());
    }

    @AfterAll
    static void stopMockWebServer() throws IOException {
        MOCK_WEB_SERVER.shutdown();
        MONGO_PROCESS.stop();
        MONGO_EXECUTABLE.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContoRepository contoRepository;

    @BeforeEach
    void setUp() {
        contoRepository.deleteAll();
    }

    @Test
    void shouldOpenAccount() throws Exception {
        ContoCreateRequest request = ContoCreateRequest.builder()
                .iban("IT60X0542811101000000123456")
                .saldo(new BigDecimal("1000.00"))
                .dataApertura(LocalDate.of(2026, 3, 4))
                .clienteId("cliente-1")
                .build();

        mockMvc.perform(post("/api/conti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.iban").value("IT60X0542811101000000123456"))
                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    @Test
    void shouldListAccounts() throws Exception {
        contoRepository.save(Conto.builder()
                .iban("IT01X0542811101000000000001")
                .saldo(new BigDecimal("10.00"))
                .dataApertura(LocalDate.of(2026, 1, 1))
                .clienteId("cliente-1")
                .build());

        contoRepository.save(Conto.builder()
                .iban("IT01X0542811101000000000002")
                .saldo(new BigDecimal("20.00"))
                .dataApertura(LocalDate.of(2026, 1, 2))
                .clienteId("cliente-2")
                .build());

        mockMvc.perform(get("/api/conti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetDetailsWithCustomerFromClientiService() throws Exception {
        Conto saved = contoRepository.save(Conto.builder()
                .iban("IT55X0542811101000000099999")
                .saldo(new BigDecimal("700.00"))
                .dataApertura(LocalDate.of(2026, 2, 10))
                .clienteId("cliente-42")
                .build());

        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "id": "cliente-42",
                          "nome": "Mario",
                          "cognome": "Rossi",
                          "codiceFiscale": "RSSMRA80A01H501U",
                          "email": "mario.rossi@example.com",
                          "indirizzo": "Via Roma 1, Milano"
                        }
                        """));

        mockMvc.perform(get("/api/conti/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.cliente.id").value("cliente-42"))
                .andExpect(jsonPath("$.cliente.nome").value("Mario"));

        RecordedRequest recordedRequest = MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getPath()).isEqualTo("/api/clienti/cliente-42");
    }

    @Test
    void shouldApplyDepositAndWithdrawRules() throws Exception {
        Conto saved = contoRepository.save(Conto.builder()
                .iban("IT77X0542811101000000088888")
                .saldo(new BigDecimal("100.00"))
                .dataApertura(LocalDate.of(2026, 2, 1))
                .clienteId("cliente-5")
                .build());

        mockMvc.perform(post("/api/conti/{id}/deposito", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("importo", new BigDecimal("50.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(150.00));

        mockMvc.perform(post("/api/conti/{id}/prelievo", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("importo", new BigDecimal("30.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(120.00));

        mockMvc.perform(post("/api/conti/{id}/prelievo", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("importo", new BigDecimal("999.00")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo insufficiente per completare il prelievo"));
    }
}
