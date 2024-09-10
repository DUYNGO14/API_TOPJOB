package com.duyngo.topjob.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;

import com.duyngo.topjob.repository.TokenRepository;
import com.duyngo.topjob.service.TokenService;
import com.duyngo.topjob.service.UserService;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final UserService userService;

    public TokenServiceImpl(TokenRepository tokenRepository, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
    }

    @Override
    public Token getTokenByUser(User user) {
        Optional<Token> token = this.tokenRepository.findByUser(user);
        if (token.isPresent()) {
            return token.get();
        }
        return null;
    }

    @Override
    public Token createToken(Token token) {
        return this.tokenRepository.save(token);
    }

    @Override
    public Token updateToken(Token token) {
        Token currentToken = this.getTokenByUser(token.getUser());
        currentToken.setRefreshToken(token.getRefreshToken());
        return this.tokenRepository.save(currentToken);
    }

    @Override
    public Token getUserByRefreshTokenAndName(String refreshToken, User user) {
        Optional<Token> token = this.tokenRepository.findByRefreshTokenAndUser(refreshToken, user);
        if (token.isPresent()) {
            return token.get();
        }
        return null;
    }

}
