package com.lynqo.backend.shared.security;

import com.lynqo.backend.user.domain.User;
import com.lynqo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        User user = userRepository.findById(Integer.parseInt(jwt.getSubject()));
        if (user == null || user.isBlocked() || !user.isActive()) {
            throw new BadCredentialsException("User is not allowed to authenticate");
        }
        return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
    }
}
