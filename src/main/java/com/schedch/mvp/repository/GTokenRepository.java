package com.schedch.mvp.repository;

import com.schedch.mvp.model.GToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GTokenRepository extends JpaRepository<GToken, Long> {
    Optional<GToken> findByState(String state);
}
