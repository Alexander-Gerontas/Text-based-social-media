package com.alg.social_media.utils;

import static com.alg.social_media.account.adapter.domain.constants.Keywords.ROLE;
import static com.alg.social_media.account.adapter.domain.constants.Security.JWT_EXPIRATION_TIME;
import static com.alg.social_media.account.adapter.domain.constants.Security.JWT_SECRET_KEY;

import com.alg.social_media.account.adapter.domain.enums.AccountType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

  private JwtUtil() {}

  private static final String SECRET_KEY = JWT_SECRET_KEY;
  private static final long EXPIRATION_TIME = JWT_EXPIRATION_TIME;

  public static String generateToken(String username, AccountType accountType) {
    return Jwts.builder()
        .setSubject(username)
        .claim(ROLE, accountType.getLiteral())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public static String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public static AccountType extractAccountType(String token) {
    String role = (String) extractClaims(token).get(ROLE);
    return AccountType.valueOf(role);
  }

  private static Claims extractClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
