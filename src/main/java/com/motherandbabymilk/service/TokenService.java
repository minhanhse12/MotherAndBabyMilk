package com.motherandbabymilk.service;

import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    @Autowired
    UserRepository userRepository;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Users user) {
        return Jwts.builder()
                .subject(user.getId() + "")
                .issuedAt(new Date(System.currentTimeMillis())) //10:30
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigninKey())
                .compact();
    }

    public Users verifyToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String idString = claims.getSubject();
        int id = Integer.parseInt(idString);

        return userRepository.findUsersById(id);
    }
}
