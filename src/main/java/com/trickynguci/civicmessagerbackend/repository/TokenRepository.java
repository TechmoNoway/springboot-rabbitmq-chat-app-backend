package com.trickynguci.civicmessagerbackend.repository;

import com.trickynguci.civicmessagerbackend.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Override
    Token save(Token token);
}
