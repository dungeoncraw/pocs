package org.tetokeguii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DungeonGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(DungeonGameApplication.class, args);
    }

    @Bean
    CommandLineRunner demo() {
        return args -> {
            System.out.println("=== Dungeon Game REST Server Started ===");
            System.out.println("Available endpoints:");
            System.out.println("POST /api/games - Create a new game");
            System.out.println("GET /api/games - Get all games");
            System.out.println("GET /api/games/{id} - Get game by ID");
            System.out.println("PUT /api/games/{id} - Update game");
            System.out.println("DELETE /api/games/{id} - Delete game");
            System.out.println("GET /api/games/search - Search games");
            System.out.println("Server running on port 8080");
        };
    }
}
