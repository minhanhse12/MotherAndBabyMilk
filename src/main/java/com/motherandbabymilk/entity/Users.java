package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@ToString(exclude = {"password"})
@Entity
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Email(message = "Username must be a valid email!")
    @NotBlank(message = "Username (email) cannot be blank!")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password can not be blank!")
    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;

    private String fullName;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    private String phone;

    private String address;

    private Date registration_date;

    boolean status = true;

    @Enumerated(EnumType.STRING)
    private Roles roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.roles != null) {
            authorities.add(new SimpleGrantedAuthority(this.roles.name()));
        }
        return authorities;
    }
}