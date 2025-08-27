import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.time.Duration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Docker-specific integration tests for Spring Boot DungeonGame application
 */
@Testcontainers
class DungeonGameDockerIntegrationTest {

    @Container
    static GenericContainer<?> dungeonContainer = new GenericContainer<>(DockerImageName.parse("dungeon-game:latest"))
            .withExposedPorts(8080)
            .withEnv("SPRING_PROFILES_ACTIVE", "dev")
            .waitingFor(Wait.forHttp("/api/games")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(3)))
            .withStartupTimeout(Duration.ofMinutes(3));

    @BeforeEach
    void setUp() {
        assertTrue(dungeonContainer.isRunning(), "Container should be running");

        // Configure RestAssured to use the container
        RestAssured.baseURI = "http://" + dungeonContainer.getHost();
        RestAssured.port = dungeonContainer.getMappedPort(8080);
    }

    @Test
    @DisplayName("Test Spring Boot application starts successfully in container")
    void testSpringBootApplicationStartup() {
        // Test that the Spring Boot application is running
        String logs = dungeonContainer.getLogs();

        assertAll(
            () -> assertTrue(logs.contains("Started DungeonGameApplication"), 
                "Spring Boot application should start"),
            () -> assertTrue(logs.contains("Dungeon Game REST Server Started"), 
                "Application banner should be displayed"),
            () -> assertTrue(logs.contains("Tomcat started on port"), 
                "Tomcat server should start")
        );
    }

    @Test
    @DisplayName("Test REST API endpoints are accessible in container")
    void testRestApiEndpoints() {
        // Test GET /api/games endpoint
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));

        // Test POST /api/games endpoint
        String gameJson = """
            {
                "name": "Container Test Game",
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
            .body("name", equalTo("Container Test Game"))
            .body("minimumHealth", equalTo(4))
            .body("algorithmUsed", equalTo("2D_DP"));
    }

    @Test
    @DisplayName("Test container uses correct Java version")
    void testJavaVersion() throws Exception {
        var result = dungeonContainer.execInContainer("java", "-version");

        assertEquals(0, result.getExitCode(), "Java version command should succeed");
        String output = result.getStderr();
        assertAll(
            () -> assertTrue(output.contains("openjdk version") || output.contains("OpenJDK"), 
                "Should use OpenJDK"),
            () -> assertTrue(output.contains("21"), "Should use Java 21")
        );
    }

    @Test
    @DisplayName("Test container security - runs as non-root user")
    void testNonRootUser() throws Exception {
        var result = dungeonContainer.execInContainer("whoami");
        assertEquals(0, result.getExitCode(), "whoami command should succeed");

        String user = result.getStdout().trim();
        assertEquals("appuser", user, "Should run as appuser, not root");
    }

    @Test
    @DisplayName("Test container file system structure")
    void testContainerStructure() throws Exception {
        // Check working directory
        var pwdResult = dungeonContainer.execInContainer("pwd");
        assertEquals("/app", pwdResult.getStdout().trim(), "Should be in /app directory");

        // Check Spring Boot JAR exists
        var jarResult = dungeonContainer.execInContainer("ls", "-la", "app.jar");
        assertEquals(0, jarResult.getExitCode(), "Spring Boot JAR should exist");

        // Check classes directory for backward compatibility
        var classesResult = dungeonContainer.execInContainer("ls", "-la", "classes");
        assertEquals(0, classesResult.getExitCode(), "Classes directory should exist");
    }

    @Test
    @DisplayName("Test application health endpoint")
    void testHealthEndpoint() {
        // Test application is healthy by calling the games endpoint
        when()
            .get("/api/games")
        .then()
            .statusCode(200);

        // Test that we can create and retrieve games
        String gameJson = """
            {
                "name": "Health Check Game",
                "dungeonData": "[[1,2],[3,4]]"
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

        // Verify the game was created and can be retrieved
        given()
            .pathParam("id", gameId)
        .when()
            .get("/api/games/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("Health Check Game"));
    }

    @Test
    @DisplayName("Test Spring Boot application performance in container")
    void testApplicationPerformance() {
        long startTime = System.currentTimeMillis();

        // Make multiple API calls to test performance
        for (int i = 0; i < 5; i++) {
            String gameJson = String.format("""
                {
                    "name": "Performance Test Game %d",
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

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        assertTrue(totalTime < 10000, "5 API calls should complete within 10 seconds");
    }

    @Test
    @DisplayName("Test container resource usage")
    void testResourceUsage() throws Exception {
        // Test JVM memory settings
        var result = dungeonContainer.execInContainer("java", "-XX:+PrintFlagsFinal", "-version");
        assertEquals(0, result.getExitCode(), "JVM flags check should succeed");

        // Test that the application is responsive
        long startTime = System.currentTimeMillis();

        when()
            .get("/api/games")
        .then()
            .statusCode(200);

        long responseTime = System.currentTimeMillis() - startTime;
        assertTrue(responseTime < 5000, "API should respond within 5 seconds");
    }

    @Test
    @DisplayName("Test database operations in containerized Spring Boot app")
    void testDatabaseOperations() {
        // Create multiple games and verify they're persisted
        String[] gameNames = {"Game 1", "Game 2", "Game 3"};
        String[] dungeonData = {
            "[[-3,5],[1,-4]]",
            "[[-1,-2,-3],[4,5,6]]",
            "[[1,2,3],[-4,-5,-6]]"
        };

        // Create games
        for (int i = 0; i < gameNames.length; i++) {
            String gameJson = String.format("""
                {
                    "name": "%s",
                    "dungeonData": "%s"
                }
                """, gameNames[i], dungeonData[i]);

            given()
                .contentType(ContentType.JSON)
                .body(gameJson)
            .when()
                .post("/api/games")
            .then()
                .statusCode(201);
        }

        // Verify all games are retrievable
        when()
            .get("/api/games")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(gameNames.length));
    }
}
