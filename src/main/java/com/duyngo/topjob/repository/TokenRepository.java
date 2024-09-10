package com.duyngo.topjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.duyngo.topjob.domain.Token;
import com.duyngo.topjob.domain.User;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    Optional<Token> findById(long id);

    Optional<Token> findByUser(User user);

    Optional<Token> findByRefreshTokenAndUser(String refreshToken, User user);
}
