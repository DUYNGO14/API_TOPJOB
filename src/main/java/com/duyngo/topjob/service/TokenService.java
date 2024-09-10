package com.duyngo.topjob.service;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;

public interface TokenService {
    public Token createToken(Token token);

    public Token updateToken(Token token);

    public Token getTokenByUser(User user);

    public Token getUserByRefreshTokenAndName(String refreshToken, User user);
}
