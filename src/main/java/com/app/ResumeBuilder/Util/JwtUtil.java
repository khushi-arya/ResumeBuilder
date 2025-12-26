package com.app.ResumeBuilder.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtsecret;
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String userid){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder().setSubject(userid).setIssuedAt(now).setExpiration(expiryDate).signWith(getsinginkey()).compact();
    }

    private Key getsinginkey() {
        return Keys.hmacShaKeyFor(jwtsecret.getBytes());
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getsinginkey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try{
          Jwts.parser().setSigningKey(getsinginkey()).parseClaimsJws(token);
          return true;
        }catch (JwtException | IllegalArgumentException e){
          return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try{
         Claims claims = Jwts.parser().setSigningKey(getsinginkey()).parseClaimsJws(token).getBody();
         return  claims.getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return true;
        }
    }
}
