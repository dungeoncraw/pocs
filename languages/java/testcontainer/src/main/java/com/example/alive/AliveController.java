package com.example.alive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AliveController {
    private final NonUserRepository nonUserRepository;

    @Autowired
    public AliveController(NonUserRepository nonUserRepository) {
        this.nonUserRepository = nonUserRepository;
    }

    @GetMapping("/alive")
    public String alive() {
        UUID uuid = UUID.randomUUID();
        nonUserRepository.save(new NonUserEntity(uuid));
        return uuid.toString();
    }
}
