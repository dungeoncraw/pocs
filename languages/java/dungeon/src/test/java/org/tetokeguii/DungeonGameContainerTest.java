import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests using Testcontainers for Spring Boot DungeonGame with PostgreSQL
 */
@Testcontainers
class DungeonGameContainerTest {

    // Create a custom network for container communication
    private static final Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("dungeon_test")
            .withUsername("dungeon_user")
            .withPassword("dungeon_pass")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withInitScript("init-db.sql");

    @Container
    static GenericContainer<?> dungeonApp = new GenericContainer<>(DockerImageName.parse("dungeon-game:latest"))
            .withNetwork(network)
            .withExposedPorts(8080)
            .withEnv("SPRING_PROFILES_ACTIVE", "docker")
            .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/dungeon_test")
            .withEnv("SPRING_DATASOURCE_USERNAME", "dungeon_user")
            .withEnv("SPRING_DATASOURCE_PASSWORD", "dungeon_pass")
            .waitingFor(Wait.forHttp("/api/games")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(3)))
            .withStartupTimeout(Duration.ofMinutes(3))
            .dependsOn(postgres);

    @BeforeAll
    static void setUp() {
        // Ensure containers are started
        assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
        assertTrue(dungeonApp.isRunning(), "Dungeon app container should be running");

        // Configure RestAssured
        RestAssured.baseURI = "http://" + dungeonApp.getHost();
        RestAssured.port = dungeonApp.getMappedPort(8080);
    }

    @Test
    @DisplayName("Test Spring Boot application with PostgreSQL container startup")
    void testContainerStartup() {
        // Test database connectivity
        assertDoesNotThrow(() -> {
            String jdbcUrl = postgres.getJdbcUrl();
            String username = postgres.getUsername();
            String password = postgres.getPassword();

            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                assertTrue(conn.isValid(5), "Database connection should be valid");
            }
        });

        // Test Spring Boot application is responding
        when()
            .get("/api/games")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Test REST API operations with PostgreSQL backend")
    void testRestApiWithDatabase() {
        // Create a new game via REST API
        String gameJson = """
            {
                "name": "PostgreSQL Integration Test",
                "dungeonData": "[[-3,5],[1,-4]]"
            }
            """;

        Integer gameId = given()
            .contentType(ContentType.JSON)
            .body(gameJson)
        .when()
            .post("/api/games")
        .then()
            .statusCode(201)
            .body("name", equalTo("PostgreSQL Integration Test"))
            .body("minimumHealth", equalTo(4))
            .body("algorithmUsed", equalTo("2D_DP"))
            .extract()
            .path("id");

        // Verify the game was persisted in PostgreSQL
        given()
            .pathParam("id", gameId)
        .when()
            .get("/api/games/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("PostgreSQL Integration Test"))
            .body("minimumHealth", equalTo(4));

        // Verify the game appears in the list
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("find { it.id == " + gameId + " }.name", 
                  equalTo("PostgreSQL Integration Test"));
    }

    @Test
    @DisplayName("Test database operations directly and via REST API")
    void testDatabaseOperations() throws Exception {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        // Test direct database access
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            // Verify games table exists
            try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM games")) {
                assertTrue(rs.next(), "Should be able to query games table");
                int count = rs.getInt(1);
                assertTrue(count >= 0, "Games table should exist and be queryable");
            }

            // Insert a game directly into database
            String insertSql = """
                INSERT INTO games (name, dungeon_data, minimum_health, algorithm_used, execution_time_ms) 
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, "Direct DB Insert");
                stmt.setString(2, "[[1,-2],[3,-4]]");
                stmt.setInt(3, 2);
                stmt.setString(4, "2D_DP");
                stmt.setLong(5, 10L);

                int rowsAffected = stmt.executeUpdate();
                assertEquals(1, rowsAffected, "Should insert one row");
            }
        }

        // Verify the directly inserted game appears via REST API
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("find { it.name == 'Direct DB Insert' }.minimumHealth", 
                  equalTo(2));
    }

    @Test
    @DisplayName("Test complex dungeon calculations with database persistence")
    void testComplexDungeonWithPersistence() {
        // Test with a complex dungeon scenario
        String complexGameJson = """
            {
                "name": "Complex PostgreSQL Test",
                "dungeonData": "[[-200,-3,4],[1,-3,-3],[4,-3,-200]]"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(complexGameJson)
        .when()
            .post("/api/games")
        .then()
            .statusCode(201)
            .body("name", equalTo("Complex PostgreSQL Test"))
            .body("minimumHealth", equalTo(399))
            .body("algorithmUsed", equalTo("2D_DP"))
            .body("executionTimeMs", greaterThan(0));

        // Verify the game is searchable
        given()
            .queryParam("name", "Complex PostgreSQL Test")
        .when()
            .get("/api/games/search")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].minimumHealth", equalTo(399));
    }

    @Test
    @DisplayName("Test container network communication")
    void testContainerNetworkCommunication() {
        // Test that the Spring Boot app can communicate with PostgreSQL
        String logs = dungeonApp.getLogs();

        assertAll(
            () -> assertTrue(logs.contains("Started DungeonGameApplication"), 
                "Spring Boot should start successfully"),
            () -> assertFalse(logs.contains("Connection refused"), 
                "Should not have connection errors"),
            () -> assertFalse(logs.contains("SQLException"), 
                "Should not have SQL exceptions during startup")
        );
    }

    @Test
    @DisplayName("Test data persistence and transaction handling")
    void testDataPersistenceAndTransactions() {
        // Create multiple games in sequence to test transaction handling
        String[] gameNames = {
            "Transaction Test 1",
            "Transaction Test 2", 
            "Transaction Test 3"
        };

        for (String gameName : gameNames) {
            String gameJson = String.format("""
                {
                    "name": "%s",
                    "dungeonData": "[[-1,-2],[-3,-4]]"
                }
                """, gameName);

            given()
                .contentType(ContentType.JSON)
                .body(gameJson)
            .when()
                .post("/api/games")
            .then()
                .statusCode(201);
        }

        // Verify all games are persisted
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("findAll { it.name.contains('Transaction Test') }.size()", 
                  equalTo(gameNames.length));
    }

    @Test
    @DisplayName("Test application performance with database backend")
    void testPerformanceWithDatabase() {
        long startTime = System.currentTimeMillis();

        // Create and retrieve multiple games to test performance
        for (int i = 0; i < 10; i++) {
            String gameJson = String.format("""
                {
                    "name": "Performance Test %d",
                    "dungeonData": "[[-1,-2],[-3,-4]]"
                }
                """, i);

            given()
                .contentType(ContentType.JSON)
                .body(gameJson)
            .when()
                .post("/api/games")
            .then()
                .statusCode(201);
        }

        // Retrieve all games
        when()
            .get("/api/games")
        .then()
            .statusCode(200);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        assertTrue(totalTime < 30000, "Database operations should complete within 30 seconds");
    }

    @Test
    @DisplayName("Test container health and resource usage")
    void testContainerHealthAndResources() {
        // Test that both containers are healthy
        assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
        assertTrue(dungeonApp.isRunning(), "Spring Boot container should be running");

        // Test application health via API
        when()
            .get("/api/games")
        .then()
            .statusCode(200);

        // Test database health
        assertDoesNotThrow(() -> {
            try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), 
                postgres.getUsername(), 
                postgres.getPassword())) {
                assertTrue(conn.isValid(5));
            }
        });
    }

    @AfterAll
    static void tearDown() {
        // Cleanup is handled automatically by Testcontainers
        network.close();
    }
}
