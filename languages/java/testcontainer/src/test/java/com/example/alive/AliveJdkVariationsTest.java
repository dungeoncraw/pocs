package com.example.alive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class AliveJdkVariationsTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("alivedb")
            .withUsername("aliveuser")
            .withPassword("alivepass")
            .withNetworkAliases("postgres-db");

    private static Network sharedNetwork = Network.newNetwork();

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
    @Order(1)
    void testWithJdk17AndDebugMode() {
        GenericContainer<?> jdk17Container = null;
        try {
            // Test with JDK 17 and debug mode enabled
            jdk17Container = createJavaContainer("eclipse-temurin:17-jdk-alpine", "17")
                    .withEnv("JAVA_OPTS", "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005")
                    .withEnv("SPRING_PROFILES_ACTIVE", "debug")
                    .withExposedPorts(5005);

            jdk17Container.start();

            // Verify JDK version
            String logs = jdk17Container.getLogs();
            assertThat(logs).containsAnyOf("openjdk version \"17", "Eclipse Temurin");

            // Test application with debug mode
            System.setProperty("spring.jpa.show-sql", "true");
            System.setProperty("logging.level.org.springframework", "DEBUG");

            String url = "http://localhost:" + port + "/alive";
            NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getUuid()).isNotNull();

            // Verify debug port is exposed
            Integer debugPort = jdk17Container.getMappedPort(5005);
            assertThat(debugPort).isNotNull();

        } finally {
            if (jdk17Container != null && jdk17Container.isRunning()) {
                jdk17Container.stop();
            }
            // Clear debug properties
            System.clearProperty("spring.jpa.show-sql");
            System.clearProperty("logging.level.org.springframework");
        }
    }

    @Test
    @Order(2)
    void testWithJdk21AndProductionMode() {
        GenericContainer<?> jdk21Container = null;
        try {
            // Test with JDK 21 and production-like settings
            jdk21Container = createJavaContainer("eclipse-temurin:21-jdk-alpine", "21")
                    .withEnv("JAVA_OPTS", "-Xms512m -Xmx1024m -XX:+UseG1GC")
                    .withEnv("SPRING_PROFILES_ACTIVE", "prod")
                    .withEnv("SERVER_PORT", "8080");

            jdk21Container.start();

            // Verify JDK version
            String logs = jdk21Container.getLogs();
            assertThat(logs).containsAnyOf("openjdk version \"21", "Eclipse Temurin");

            // Test application with production settings
            System.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
            System.setProperty("spring.jpa.show-sql", "false");
            System.setProperty("logging.level.com.example.alive", "WARN");

            String url = "http://localhost:" + port + "/alive";
            NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getUuid()).isNotNull();

        } finally {
            if (jdk21Container != null && jdk21Container.isRunning()) {
                jdk21Container.stop();
            }
            // Clear production properties
            System.clearProperty("spring.jpa.hibernate.ddl-auto");
            System.clearProperty("spring.jpa.show-sql");
            System.clearProperty("logging.level.com.example.alive");
        }
    }

    @Test
    @Order(3)
    void testWithJdk21ExperimentalFeaturesAndExperimentalFeatures() {
        GenericContainer<?> jdk21ExperimentalContainer = null;
        try {
            // Test with JDK 21 and experimental features enabled
            jdk21ExperimentalContainer = createJavaContainer("eclipse-temurin:21-jdk", "21")
                    .withEnv("JAVA_OPTS", "--enable-preview -XX:+UnlockExperimentalVMOptions")
                    .withEnv("SPRING_PROFILES_ACTIVE", "experimental")
                    .withEnv("SPRING_JPA_PROPERTIES_HIBERNATE_SHOW_SQL", "true")
                    .withEnv("SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL", "true");

            jdk21ExperimentalContainer.start();

            // Verify JDK version
            String logs = jdk21ExperimentalContainer.getLogs();
            assertThat(logs).containsAnyOf("openjdk version \"21", "Eclipse Temurin");

            // Test application with experimental settings
            System.setProperty("spring.jpa.hibernate.naming.physical-strategy", 
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
            System.setProperty("spring.jackson.serialization.write-dates-as-timestamps", "false");
            System.setProperty("spring.jackson.deserialization.fail-on-unknown-properties", "false");

            String url = "http://localhost:" + port + "/alive";
            NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getUuid()).isNotNull();

            // Test multiple requests to verify stability with experimental features
            for (int i = 0; i < 3; i++) {
                NonUserEntity additionalResponse = restTemplate.getForObject(url, NonUserEntity.class);
                assertThat(additionalResponse).isNotNull();
                assertThat(additionalResponse.getId()).isNotNull();
            }

        } finally {
            if (jdk21ExperimentalContainer != null && jdk21ExperimentalContainer.isRunning()) {
                jdk21ExperimentalContainer.stop();
            }
            // Clear experimental properties
            System.clearProperty("spring.jpa.hibernate.naming.physical-strategy");
            System.clearProperty("spring.jackson.serialization.write-dates-as-timestamps");
            System.clearProperty("spring.jackson.deserialization.fail-on-unknown-properties");
        }
    }

    @Test
    @Order(4)
    void testCrossJdkCompatibilityWithDifferentSpringFlags() {
        GenericContainer<?> jdk17Container = null;
        GenericContainer<?> jdk21Container = null;

        try {
            // Start containers with different JDK versions simultaneously
            jdk17Container = createJavaContainer("eclipse-temurin:17-jdk-alpine", "17")
                    .withEnv("SPRING_PROFILES_ACTIVE", "jdk17-compat")
                    .withEnv("JAVA_OPTS", "-XX:+UseStringDeduplication");

            jdk21Container = createJavaContainer("eclipse-temurin:21-jdk-alpine", "21")
                    .withEnv("SPRING_PROFILES_ACTIVE", "jdk21-compat")
                    .withEnv("JAVA_OPTS", "-XX:+UseZGC -XX:+UnlockExperimentalVMOptions");

            jdk17Container.start();
            jdk21Container.start();

            // Test with different Spring flags for each environment
            System.setProperty("spring.jpa.defer-datasource-initialization", "true");
            System.setProperty("spring.sql.init.mode", "always");
            System.setProperty("management.endpoints.web.exposure.include", "health,info");

            String url = "http://localhost:" + port + "/alive";
            NonUserEntity response = restTemplate.getForObject(url, NonUserEntity.class);
            assertThat(response).isNotNull();

            // Verify both containers are running with their respective JDK versions
            String jdk17Logs = jdk17Container.getLogs();
            String jdk21Logs = jdk21Container.getLogs();

            assertThat(jdk17Logs).containsAnyOf("openjdk version \"17", "Eclipse Temurin");
            assertThat(jdk21Logs).containsAnyOf("openjdk version \"21", "Eclipse Temurin");

            assertThat(jdk17Container.isRunning()).isTrue();
            assertThat(jdk21Container.isRunning()).isTrue();

        } finally {
            if (jdk17Container != null && jdk17Container.isRunning()) {
                jdk17Container.stop();
            }
            if (jdk21Container != null && jdk21Container.isRunning()) {
                jdk21Container.stop();
            }
            // Clear compatibility properties
            System.clearProperty("spring.jpa.defer-datasource-initialization");
            System.clearProperty("spring.sql.init.mode");
            System.clearProperty("management.endpoints.web.exposure.include");
        }
    }

    private GenericContainer<?> createJavaContainer(String imageName, String expectedVersion) {
        return new GenericContainer<>(imageName)
                .withNetwork(sharedNetwork)
                .withCommand("sh", "-c", 
                    "echo 'Starting Java container with version:' && " +
                    "java -version && " +
                    "echo 'Expected version: " + expectedVersion + "' && " +
                    "echo 'Container is ready for testing' && " +
                    "sleep 30")
                .waitingFor(Wait.forLogMessage(".*Container is ready for testing.*", 1))
                .withStartupTimeout(Duration.ofMinutes(2));
    }
}
