package com.lynqo.backend.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService  {

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

}
