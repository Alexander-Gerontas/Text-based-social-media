package com.alg.social_media.configuration.security;

import static com.alg.social_media.configuration.Constants.JWT_EXPIRATION_TIME;
import static com.alg.social_media.configuration.Constants.JWT_SECRET_KEY;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

  private JwtUtil() {}

  private static final String SECRET_KEY = JWT_SECRET_KEY;
  private static final long EXPIRATION_TIME = JWT_EXPIRATION_TIME;

  public static String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public static String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  private static Claims extractClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
