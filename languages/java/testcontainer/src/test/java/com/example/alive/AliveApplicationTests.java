package com.example.alive;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AliveConfigTest {

    static PostgreSQLContainer<?> postgres;

    @Autowired
    TestRestTemplate restTemplate;

    static Stream<TestConfig> configProvider() {
        // DB image, profile, feature flag
        return Stream.of(
                new TestConfig("postgres:15-alpine", "dev", "featureA"),
                new TestConfig("postgres:16-alpine", "prod", "featureB")
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

        TestConfig(String image, String profile, String featureFlag) {
            this.image = image;
            this.profile = profile;
            this.featureFlag = featureFlag;
        }
    }
}