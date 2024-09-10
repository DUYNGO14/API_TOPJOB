package com.duyngo.topjob.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.duyngo.topjob.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class JwtConfiguration {

    @Value("${jwt.base64-secret}")
    private String jwtKeySecret;

    // tạo key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKeySecret).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    // Mã hóa token
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.getSecretKey()));
    }

    // Giải mã token
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                this.getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    // khi decode thành công
    // quyền hạn
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
