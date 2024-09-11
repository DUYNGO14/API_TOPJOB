package com.duyngo.topjob.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.ResetPasswordDTO;
import com.duyngo.topjob.domain.response.ResForgotPassword;
import com.duyngo.topjob.domain.response.ResLoginDTO;
import com.duyngo.topjob.exception.CheckInvalidException;
import com.duyngo.topjob.exception.TokenException;
import com.duyngo.topjob.repository.TokenRepository;
import com.duyngo.topjob.repository.UserRepository;
import com.duyngo.topjob.service.TokenService;
import com.duyngo.topjob.service.UserService;
import com.duyngo.topjob.util.SecurityUtil;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    public TokenServiceImpl(TokenRepository tokenRepository, UserRepository userRepository, SecurityUtil securityUtil,
            PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
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
        currentToken.setResetToken(token.getResetToken());
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

    @Override
    public ResForgotPassword forgotPassword(String email) throws TokenException {
        ResLoginDTO res = new ResLoginDTO();
        Optional<User> currentUser = this.userRepository.findByEmail(email);
        User currentUserDB = currentUser.isPresent() ? currentUser.get() : null;
        if (currentUserDB == null) {
            throw new TokenException("Email not valid!");
        }
        res = this.convertResLoginDTO(currentUserDB);
        // create reset token
        String resetToken = this.securityUtil.createResetToken(email, res);

        // inssert reset token
        Token token = Token.builder().user(currentUserDB).resetToken(resetToken).build();
        this.updateToken(token);
        String link = "http://localhost:8080/api/v1/auth/reset-password";
        ResForgotPassword resFP = ResForgotPassword.builder().link(link).secretKey(resetToken).build();
        return resFP;
    }

    @Override
    public String resetPassword(String resetToken) throws TokenException {
        User user = this.validateToken(resetToken);
        return "Reset password";
    }

    @Override
    public User changePassword(ResetPasswordDTO request) throws CheckInvalidException, TokenException {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CheckInvalidException("Passwords do not match");
        }
        User currentUserDB = this.validateToken(request.getSecretKey());
        // update password
        currentUserDB.setPassword(passwordEncoder.encode(request.getPassword()));
        return this.userRepository.save(currentUserDB);
    }

    @Override
    public ResLoginDTO convertResLoginDTO(User user) {
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFullname())
                .role(user.getRole())
                .build();
        res.setUser(userLogin);
        return res;
    }

    @Override
    public User validateToken(String resetToken) throws TokenException {
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(resetToken);
        String email = decodedToken.getSubject();
        Optional<User> currentUser = this.userRepository.findByEmail(email);
        User currentUserDB = currentUser.isPresent() ? currentUser.get() : null;
        if (currentUserDB == null) {
            throw new TokenException("User not active");
        }

        return currentUserDB;
    }

}
