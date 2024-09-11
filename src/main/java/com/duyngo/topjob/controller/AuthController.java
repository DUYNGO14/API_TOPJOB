package com.duyngo.topjob.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.ResetPasswordDTO;
import com.duyngo.topjob.domain.request.user_request.ReqLoginDTO;
import com.duyngo.topjob.domain.response.ResForgotPassword;
import com.duyngo.topjob.domain.response.ResLoginDTO;
import com.duyngo.topjob.domain.response.user.ResCreateUserDTO;
import com.duyngo.topjob.exception.CheckInvalidException;
import com.duyngo.topjob.exception.TokenException;
import com.duyngo.topjob.service.TokenService;
import com.duyngo.topjob.service.UserService;
import com.duyngo.topjob.util.SecurityUtil;
import com.duyngo.topjob.util.annotation.ApiMessage;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
            PasswordEncoder passwordEncoder, SecurityUtil securityUtil, TokenService tokenService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    @ApiMessage("Register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User requestUser)
            throws CheckInvalidException {
        User newUser = this.userService.createUSer(requestUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(newUser));
    }

    @PostMapping("/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLogin) throws CheckInvalidException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                reqLogin.getEmail(), reqLogin.getPassword());
        // Xác thực người dùng => cần viết hàm loadByUsername
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // set thông tin người dùng đăng nhập vào context( có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.getUserByEmail(reqLogin.getEmail());
        res = this.tokenService.convertResLoginDTO(currentUserDB);
        // create access token
        String accessToken = this.securityUtil.createAccessToken(reqLogin.getEmail(), res);
        res.setAccessToken(accessToken);
        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(reqLogin.getEmail(), res);
        // update table token
        Token token = Token.builder().user(currentUserDB).refreshToken(refreshToken).build();
        Token currentToken = this.tokenService.getTokenByUser(currentUserDB);
        if (currentToken == null) {
            this.tokenService.createToken(token);
        } else {
            this.tokenService.updateToken(token);
        }

        // set cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                // .secure(true) :cookie chỉ được sử dụng với https ko
                .path("/")
                .maxAge(refreshTokenExpiration) // thời gian sống cooki
                // .domain("example.com") quy định khi nào sẽ gửi cookie nayuf
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> postLogout() throws TokenException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email.equals("")) {
            throw new TokenException("Access token không hợp lệ!");
        }
        User user = this.userService.getUserByEmail(email);
        Token token = new Token();
        token.setUser(user);
        // update refresh token in database
        this.tokenService.updateToken(token);
        // remove refrefresh token cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true).secure(true).path("/").maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }

    @GetMapping("/account")
    @ApiMessage("Get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        User currentUserDB = this.userService.getUserByEmail(email);
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getFullname());
            userLogin.setRole(currentUserDB.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Refresh")
    public ResponseEntity<ResLoginDTO> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "cookie") String refresh_token) throws TokenException {
        if (refresh_token.equals("cookie")) {
            throw new TokenException("You do not have a refresh token in the cookie");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();
        User user = this.userService.getUserByEmail(email);
        // check user by token + email
        Token currentToken = this.tokenService.getUserByRefreshTokenAndName(refresh_token, user);
        if (currentToken == null) {
            throw new TokenException("Refresh Token not valid!");
        }
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.getUserByEmail(email);
        res = this.tokenService.convertResLoginDTO(currentUserDB);
        // create access token
        String accessToken = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(accessToken);
        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, res);
        // update table token
        Token token = Token.builder().user(currentUserDB).refreshToken(newRefreshToken).build();
        this.tokenService.updateToken(token);

        // set cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                // .secure(true) :cookie chỉ được sử dụng với https ko
                .path("/")
                .maxAge(refreshTokenExpiration) // thời gian sống cooki
                // .domain("example.com") quy định khi nào sẽ gửi cookie nayuf
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/forgot-password")
    @ApiMessage("Forgot password")
    public ResponseEntity<ResForgotPassword> forgotPassword(@RequestBody String email) throws TokenException {
        ResForgotPassword res = this.tokenService.forgotPassword(email);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/reset-password")
    @ApiMessage("Reset password")
    public ResponseEntity<String> resetPassword(@RequestBody String secretKey) throws TokenException {
        String check = this.tokenService.resetPassword(secretKey);
        return ResponseEntity.ok().body(check);
    }

    @PostMapping("/change-password")
    @ApiMessage("Chang password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ResetPasswordDTO request)
            throws TokenException, CheckInvalidException {
        User user = this.tokenService.changePassword(request);
        return ResponseEntity.ok().body("Change password success!");
    }

}
