package com.duyngo.topjob.service;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.ResetPasswordDTO;
import com.duyngo.topjob.domain.response.ResForgotPassword;
import com.duyngo.topjob.domain.response.ResLoginDTO;
import com.duyngo.topjob.exception.CheckInvalidException;
import com.duyngo.topjob.exception.TokenException;

public interface TokenService {
    public Token createToken(Token token);

    public Token updateToken(Token token);

    public Token getTokenByUser(User user);

    public Token getUserByRefreshTokenAndName(String refreshToken, User user);

    public ResForgotPassword forgotPassword(String email) throws TokenException;

    public String resetPassword(String token) throws TokenException;

    public User changePassword(ResetPasswordDTO request) throws CheckInvalidException, TokenException;

    public ResLoginDTO convertResLoginDTO(User user);

    public User validateToken(String token) throws TokenException;
}
