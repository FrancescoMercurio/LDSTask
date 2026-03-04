package it.acme.clienti;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import it.acme.clienti.dto.ClienteRequest;
import it.acme.clienti.model.Cliente;
import it.acme.clienti.repository.ClienteRepository;
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

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIntegrationTest {

    private static final MongodExecutable MONGO_EXECUTABLE;
    private static final MongodProcess MONGO_PROCESS;
    private static final int MONGO_PORT;

    static {
        try {
            MONGO_PORT = Network.getFreeServerPort();
            MongodConfig config = MongodConfig.builder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
                    .build();
            MONGO_EXECUTABLE = MongodStarter.getDefaultInstance().prepare(config);
            MONGO_PROCESS = MONGO_EXECUTABLE.start();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to start embedded MongoDB", ex);
        }
    }

    @DynamicPropertySource
    static void configureMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:" + MONGO_PORT + "/clienti_test_db");
    }

    @AfterAll
    static void stopMongo() {
        MONGO_PROCESS.stop();
        MONGO_EXECUTABLE.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    @Test
    void shouldCreateAndGetCliente() throws Exception {
        ClienteRequest request = ClienteRequest.builder()
                .nome("Mario")
                .cognome("Rossi")
                .codiceFiscale("RSSMRA80A01H501U")
                .email("mario.rossi@example.com")
                .indirizzo("Via Roma 1, Milano")
                .build();

        String response = mockMvc.perform(post("/api/clienti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Mario"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String createdId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/clienti/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.codiceFiscale").value("RSSMRA80A01H501U"));
    }

    @Test
    void shouldListClienti() throws Exception {
        clienteRepository.save(Cliente.builder()
                .nome("Anna")
                .cognome("Verdi")
                .codiceFiscale("VRDNNA90B41F205L")
                .email("anna.verdi@example.com")
                .indirizzo("Via Torino 12, Roma")
                .build());

        clienteRepository.save(Cliente.builder()
                .nome("Luca")
                .cognome("Bianchi")
                .codiceFiscale("BNCLCU85C10H501Z")
                .email("luca.bianchi@example.com")
                .indirizzo("Via Firenze 7, Napoli")
                .build());

        mockMvc.perform(get("/api/clienti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldDeleteCliente() throws Exception {
        Cliente saved = clienteRepository.save(Cliente.builder()
                .nome("Giulia")
                .cognome("Neri")
                .codiceFiscale("NREGLL92D45F839K")
                .email("giulia.neri@example.com")
                .indirizzo("Corso Italia 45, Torino")
                .build());

        mockMvc.perform(delete("/api/clienti/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clienti/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
