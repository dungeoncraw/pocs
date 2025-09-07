package com.example.alive;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AliveConfigTest {

    static PostgreSQLContainer<?> postgres;
    static GenericContainer<?> javaAppContainer;
    static Network testNetwork;

    @Autowired
    TestRestTemplate restTemplate;

    static Stream<TestConfig> configProvider() {
        // DB image, profile, feature flag
        return Stream.of(
                new TestConfig("postgres:15-alpine", "dev", "featureA", "openjdk:17"),
                new TestConfig("postgres:16-alpine", "prod", "featureB", "openjdk:21")
        );
    }

    @ParameterizedTest
    @MethodSource("configProvider")
    void testWithMultipleConfigs(TestConfig config) {
        postgres = new PostgreSQLContainer<>(config.image)
                .withDatabaseName("alivedb")
                .withUsername("aliveuser")
                .withPassword("alivepass")
                .withEnv("SPRING_PROFILES_ACTIVE", config.profile)
                .withEnv("APP_FEATURE_FLAG", config.featureFlag);
        postgres.start();

        String response = restTemplate.getForObject("/alive", String.class);
        assertThat(response).isNotNull();

        postgres.stop();
    }
    @ParameterizedTest
    @MethodSource("configProvider")
    void testWithMultipleJavaImages(TestConfig config) {
        try {
            // Create a shared network for containers to communicate
            testNetwork = Network.newNetwork();

            // Start PostgreSQL container
            postgres = new PostgreSQLContainer<>(config.image)
                    .withDatabaseName("alivedb")
                    .withUsername("aliveuser")
                    .withPassword("alivepass")
                    .withNetwork(testNetwork)
                    .withNetworkAliases("postgres-db");
            postgres.start();

            // Start Java application container with specific Java version
            javaAppContainer = new GenericContainer<>(config.javaImage)
                    .withNetwork(testNetwork)
                    .withEnv("SPRING_PROFILES_ACTIVE", config.profile)
                    .withEnv("APP_FEATURE_FLAG", config.featureFlag)
                    .withCommand("sh", "-c", "echo 'Java version:' && java -version && echo 'Container ready' && sleep 30")
                    .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forLogMessage(".*Container ready.*", 1));

            javaAppContainer.start();

            // Test the setup - verify Java version is as expected
            String logs = javaAppContainer.getLogs();
            if (config.javaImage.contains("openjdk:17")) {
                assertThat(logs).contains("openjdk version \"17");
            } else if (config.javaImage.contains("openjdk:21")) {
                assertThat(logs).contains("openjdk version \"21");
            } else if (config.javaImage.contains("openjdk:11")) {
                assertThat(logs).contains("openjdk version \"11");
            }

            // Test that containers are running and can communicate
            assertThat(postgres.isRunning()).isTrue();
            assertThat(javaAppContainer.isRunning()).isTrue();

            // Test your Spring Boot application endpoint (this will use the original postgres container)
            String response = restTemplate.getForObject("/alive", String.class);
            assertThat(response).isNotNull();

        } finally {
            // Cleanup - ensure containers are stopped even if test fails
            if (javaAppContainer != null && javaAppContainer.isRunning()) {
                javaAppContainer.stop();
            }
            if (postgres != null && postgres.isRunning()) {
                postgres.stop();
            }
            if (testNetwork != null) {
                testNetwork.close();
            }
        }
    }
    // this is tricky because we need to override the datasource url and username/password
    // dinamically based on test configuration
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        if (postgres != null && postgres.isRunning()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        }
    }

    static class TestConfig {
        final String image;
        final String profile;
        final String featureFlag;
        final String javaImage;

        TestConfig(String image, String profile, String featureFlag, String javaImage) {
            this.image = image;
            this.profile = profile;
            this.featureFlag = featureFlag;
            this.javaImage = javaImage;
        }
    }
}