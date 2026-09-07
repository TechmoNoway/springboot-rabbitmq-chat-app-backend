package com.lynqo.backend.auth.repository;

import com.lynqo.backend.auth.domain.Token;
import com.lynqo.backend.auth.domain.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    boolean existsByTokenAndTokenTypeAndRevokedFalseAndExpiredFalse(String token, TokenType tokenType);

    List<Token> findAllByUserIdAndRevokedFalse(int userId);
}
