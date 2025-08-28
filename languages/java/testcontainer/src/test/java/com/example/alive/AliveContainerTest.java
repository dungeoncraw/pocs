package com.example.alive;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class AliveContainerTest {
    @Test
    void testContainerIsRunning() {
        try (GenericContainer<?> container = new GenericContainer<>("alpine:3.18").withCommand("sleep", "10")) {
            container.start();
            assertTrue(container.isRunning(), "O container deve estar rodando");
        }
    }
}

