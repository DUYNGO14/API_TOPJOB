package com.duyngo.topjob.domain;

import java.time.Instant;

import com.duyngo.topjob.util.SecurityUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Full name not blank")
    private String fullname;
    @NotBlank(message = "Username not blank")
    private String username;
    @NotBlank(message = "Email not blank")
    @Email(message = "Email not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    @NotBlank(message = "Password not blank")
    private String password;
    private String address;
    // @Pattern(regexp = "0[3|5|7|8|9])+([0-9]{8}", message = "Enter a valid Mobile
    // Number")
    private String phonenumber;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user")
    private Token token;

    @PrePersist
    public void handleBeforeCreate() {
        // lấy name từ token
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        // lấy name từ token
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }
}
