package com.example.alive;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class AliveAppContainerIT {
    @Container
    private static final GenericContainer<?> appContainer = new GenericContainer<>("alive-app:latest")
            .withExposedPorts(8080);

    @Test
    void testAliveEndpoint() throws IOException, InterruptedException {
        appContainer.start();
        Integer port = appContainer.getMappedPort(8080);
        String address = "http://localhost:" + port + "/alive";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(address)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("It's alive", response.body());
    }
}

