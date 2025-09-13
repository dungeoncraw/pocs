package com.example.alive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureWebMvc
public class AlivePostgres14ContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("alivedb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NonUserRepository nonUserRepository;

    @Test
    void shouldStartPostgres14Container() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getDatabaseName()).isEqualTo("alivedb");
    }

    @Test
    void shouldApplyMigrationAndStoreUuid() {
        // Test if migration was applied by checking if we can save entities
        UUID testUuid = UUID.randomUUID();
        NonUserEntity entity = new NonUserEntity(testUuid);

        NonUserEntity savedEntity = nonUserRepository.save(entity);

        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getUuid()).isEqualTo(testUuid);

        // Verify the entity exists in database
        NonUserEntity foundEntity = nonUserRepository.findById(savedEntity.getId()).orElse(null);
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getUuid()).isEqualTo(testUuid);
    }

    @Test
    void shouldCallAliveEndpointAndStoreUuidInDatabase() {
        String url = "http://localhost:" + port + "/alive";

        ResponseEntity<NonUserEntity> response = restTemplate.getForEntity(url, NonUserEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getUuid()).isNotNull();

        // Verify the UUID is stored in database
        Long entityId = response.getBody().getId();
        UUID returnedUuid = response.getBody().getUuid();

        NonUserEntity storedEntity = nonUserRepository.findById(entityId).orElse(null);
        assertThat(storedEntity).isNotNull();
        assertThat(storedEntity.getUuid()).isEqualTo(returnedUuid);

        // Verify we can find it in database
        long totalCount = nonUserRepository.count();
        assertThat(totalCount).isGreaterThan(0);
    }

    @Test
    void shouldVerifyPostgres14Version() throws Exception {
        String version = postgres.execInContainer("psql", "-U", postgres.getUsername(),
                "-d", postgres.getDatabaseName(), "-t", "-c", "SELECT version();")
                .getStdout();

        assertThat(version).contains("PostgreSQL 14");
    }
}
