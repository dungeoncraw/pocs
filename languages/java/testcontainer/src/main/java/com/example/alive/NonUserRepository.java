package com.example.alive;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NonUserRepository extends JpaRepository<NonUserEntity, Long> {
}

