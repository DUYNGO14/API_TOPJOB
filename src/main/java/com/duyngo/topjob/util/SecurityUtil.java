package com.duyngo.topjob.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.response.ResLoginDTO;
import com.duyngo.topjob.service.RoleService;
import com.duyngo.topjob.service.UserService;
import com.nimbusds.jose.util.Base64;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;
    private final RoleService roleService;
    private final UserService userService;

    public SecurityUtil(JwtEncoder jwtEncoder, RoleService roleService, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.roleService = roleService;
        this.userService = userService;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${jwt.base64-secret}")
    private String jwtKeySecret;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    @Value("${jwt.reset-token-validity-in-seconds}")
    private long resetTokenExpiration;

    public String createAccessToken(String email, ResLoginDTO resLoginDTO) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(resLoginDTO.getUser().getId());
        userToken.setEmail(resLoginDTO.getUser().getEmail());
        userToken.setName(resLoginDTO.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // set role
        User user = this.userService.getUserByEmail(email);
        Role role = user.getRole();
        List<Permission> permissions = role.getPermissions();
        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();
        if (permissions.size() != 0) {
            for (Permission per : permissions) {
                listAuthority.add(per.getName());
            }
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .claim("role", role.getName())
                .claim("permission", listAuthority)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createResetToken(String email, ResLoginDTO resLoginDTO) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(resLoginDTO.getUser().getId());
        userToken.setEmail(resLoginDTO.getUser().getEmail());
        userToken.setName(resLoginDTO.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.resetTokenExpiration, ChronoUnit.SECONDS);

        // set role
        User user = this.userService.getUserByEmail(email);
        Role role = user.getRole();
        List<Permission> permissions = role.getPermissions();
        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();
        if (permissions.size() != 0) {
            for (Permission per : permissions) {
                listAuthority.add(per.getName());
            }
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .claim("role", role.getName())
                .claim("permission", listAuthority)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO resLoginDTO) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(resLoginDTO.getUser().getId());
        userToken.setEmail(resLoginDTO.getUser().getEmail());
        userToken.setName(resLoginDTO.getUser().getName());
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
        // set role
        User user = this.userService.getUserByEmail(email);
        Role role = user.getRole();
        List<Permission> permissions = role.getPermissions();
        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();
        if (permissions.size() != 0) {
            for (Permission per : permissions) {
                listAuthority.add(per.getName());
            }
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                .claim("role", role.getName())
                .claim("permission", listAuthority)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // tạo key
    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKeySecret).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            System.out.println(">> Refresh token ok");
            return jwtDecoder.decode(token);

        } catch (Exception e) {
            System.out.println(">>> Refresh token error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

}
