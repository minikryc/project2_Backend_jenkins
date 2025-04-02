package com.eouil.bank.bankapi.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef"; // HS256, 최소 32자(256bits) 이상
                                                                                // 배포 시 환경변수로 관리해야 함
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // userId로 토큰 생성
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)  // userId로 토큰 생성
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰을 검증하고 userId 추출
    public static String validateTokenAndGetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        return claims.getSubject();
    }
}