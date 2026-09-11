package com.springboot.sandbox.user.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.springboot.sandbox.common.entity.BaseEntity;
import com.springboot.sandbox.common.enumeration.AccountRole;
import com.springboot.sandbox.common.enumeration.AccountStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE users SET deleted_date = NOW(), deleted_by = 'SYSTEM', status = 'DELETED' WHERE id = ?")
@SQLRestriction("status <> 'DELETED' AND deleted_date IS NULL")
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
