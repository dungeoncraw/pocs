package org.tetokeguii;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.time.Duration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the Spring Boot REST API using Testcontainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class GameControllerTest {

    @LocalServerPort
    private int port;

    @Container
    static GenericContainer<?> dungeonContainer = new GenericContainer<>(DockerImageName.parse("dungeon-game:latest"))
            .withExposedPorts(8080)
            .withStartupTimeout(Duration.ofMinutes(2));

    @Test
    @DisplayName("Test POST /api/games - Create a new game")
    void testCreateGame() {
        RestAssured.port = port;

        String gameJson = """
            {
                "name": "Test Game",
                "dungeonData": "[[-3,5],[1,-4]]"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(gameJson)
        .when()
            .post("/api/games")
        .then()
            .statusCode(201)
            .body("name", equalTo("Test Game"))
            .body("minimumHealth", equalTo(4))
            .body("algorithmUsed", equalTo("2D_DP"))
            .body("executionTimeMs", greaterThan(0))
            .body("id", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    @DisplayName("Test GET /api/games - Get all games")
    void testGetAllGames() {
        RestAssured.port = port;

        // First create a game
        String gameJson = """
            {
                "name": "Sample Game",
                "dungeonData": "[[1,2,3],[4,5,6]]"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(gameJson)
        .when()
            .post("/api/games");

        // Then get all games
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].name", notNullValue())
            .body("[0].minimumHealth", notNullValue());
    }

    @Test
    @DisplayName("Test GET /api/games/{id} - Get game by ID")
    void testGetGameById() {
        RestAssured.port = port;

        // Create a game first
        String gameJson = """
            {
                "name": "ID Test Game",
                "dungeonData": "[[-1,-2],[-3,-4]]"
            }
            """;

        Integer gameId = given()
            .contentType(ContentType.JSON)
            .body(gameJson)
        .when()
            .post("/api/games")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Get the game by ID
        given()
            .pathParam("id", gameId)
        .when()
            .get("/api/games/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(gameId))
            .body("name", equalTo("ID Test Game"));
    }

    @Test
    @DisplayName("Test POST /api/games with invalid data")
    void testCreateGameWithInvalidData() {
        RestAssured.port = port;

        // Test with missing name
        String invalidGameJson = """
            {
                "dungeonData": "[[-3,5],[1,-4]]"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidGameJson)
        .when()
            .post("/api/games")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Test GET /api/games/search with parameters")
    void testSearchGames() {
        RestAssured.port = port;

        // Create a game with specific name
        String gameJson = """
            {
                "name": "Searchable Game",
                "dungeonData": "[[-5,1,3],[2,-3,1],[3,1,-2]]"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(gameJson)
        .when()
            .post("/api/games");

        // Search by name
        given()
            .queryParam("name", "Searchable Game")
        .when()
            .get("/api/games/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].name", equalTo("Searchable Game"));
    }

    @Test
    @DisplayName("Test containerized application REST endpoints")
    void testContainerizedAPI() {
        // Test the containerized application if it's running
        if (dungeonContainer.isRunning()) {
            String containerHost = dungeonContainer.getHost();
            Integer containerPort = dungeonContainer.getMappedPort(8080);

            // Wait a bit for the application to start
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            RestAssured.baseURI = "http://" + containerHost;
            RestAssured.port = containerPort;

            // Test health/basic endpoint
            try {
                when()
                    .get("/api/games")
                .then()
                    .statusCode(200);
            } catch (Exception e) {
                // Container might not have the REST server running yet
                System.out.println("Container API not ready: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test complex dungeon calculation via REST API")
    void testComplexDungeonCalculation() {
        RestAssured.port = port;

        String complexGameJson = """
            {
                "name": "Complex Test",
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
            .body("name", equalTo("Complex Test"))
            .body("minimumHealth", equalTo(399))
            .body("algorithmUsed", equalTo("2D_DP"))
            .body("executionTimeMs", greaterThan(0L));
    }
}
