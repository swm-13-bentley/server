package com.schedch.mvp.repository;

import com.schedch.mvp.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByEmail(String email);

    Optional<Token> findByAccessToken(String accessToken);
}
