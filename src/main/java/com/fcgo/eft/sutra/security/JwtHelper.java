package com.fcgo.eft.sutra.security;

import com.fcgo.eft.sutra.dto.res.LoginRes;
import com.fcgo.eft.sutra.entity.oracle.ApplicationUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Service
public class JwtHelper {

    private static final String SECRET_KEY = "WnQY3Rrs7d9aK5r7FqfM7kTeh1+OJd5x3P4u9s6a2lM=";


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Claims decodeToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ignored) {
        }
        return null;

    }


    public LoginRes generateToken(ApplicationUser user) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return LoginRes.builder().token(Jwts.builder()
                .claim("A", user.getId()+"")
                .claim("B", user.getUsername())
                .claim("C", user.getIpAddress())
                .claim("D", user.getPaymentUser())
                .claim("E", user.getAppName())
                .claim("F", user.getType())
                .setIssuedAt(date)
                .setExpiration(c.getTime())
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact()).expires(c.getTime().getTime() - date.getTime()).build();

    }
}
