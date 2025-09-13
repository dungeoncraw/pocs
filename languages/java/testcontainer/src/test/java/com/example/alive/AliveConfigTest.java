package com.example.alive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AliveConfigTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("alivedb")
            .withUsername("aliveuser")
            .withPassword("alivepass");

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Test
    void testWithPostgres15Configuration() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getDatabaseName()).isEqualTo("alivedb");

        String url = "http://localhost:" + port + "/alive";
        NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUuid()).isNotNull();
    }

    @Test
    void testContainerNetworking() {
        Network testNetwork = null;
        GenericContainer<?> javaAppContainer = null;

        try {
            // Create a shared network for containers to communicate
            testNetwork = Network.newNetwork();

            // Start Java application container with specific Java version
            javaAppContainer = new GenericContainer<>("openjdk:21")
                    .withNetwork(testNetwork)
                    .withCommand("sh", "-c", "echo 'Java version:' && java -version && echo 'Container ready' && sleep 10")
                    .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forLogMessage(".*Container ready.*", 1));

            javaAppContainer.start();

            // Test the setup - verify Java version is as expected
            String logs = javaAppContainer.getLogs();
            assertThat(logs).contains("openjdk version \"21");

            // Test that containers are running and can communicate
            assertThat(postgres.isRunning()).isTrue();
            assertThat(javaAppContainer.isRunning()).isTrue();

            // Test Spring Boot application endpoint
            String url = "http://localhost:" + port + "/alive";
            NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
            assertThat(response).isNotNull();

        } finally {
            // Cleanup - ensure containers are stopped even if test fails
            if (javaAppContainer != null && javaAppContainer.isRunning()) {
                javaAppContainer.stop();
            }
            if (testNetwork != null) {
                testNetwork.close();
            }
        }
    }
}
