package com.aviatickets.profile.util;

import com.aviatickets.profile.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;

public class JwtUtils {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    public static String generateToken(User user, String secret, long ttl) {
        long now = System.currentTimeMillis();
        Date expiration = ttl <= 0 ? null : new Date(now + ttl);
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(now))
                .setExpiration(expiration)
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact();
    }

    public static Long getIdFromToken(String token, String secret) {
        Claims claims = (Claims) Jwts.parser().setSigningKey(secret).parse(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public static boolean isExpired(String token, String secret) {
        Date now = new Date();
        Claims claims = (Claims) Jwts.parser().setSigningKey(secret).parse(token).getBody();
        Date expiration = claims.getExpiration();
        return expiration.before(now);
    }

}
