package taylor.project.projecttracker.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();;
        return  buildToken(userDetails.getUsername(), expiration, authorities);
    }

    public String generateToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();
        return  buildToken(auth.getPrincipal().toString(), expiration, authorities);
    }

    public String refreshToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();;
        return  buildToken(auth.getPrincipal().toString(), expiration, authorities);
    }


    public String refreshToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());;

        return  buildToken(userDetails.getUsername(), refreshTokenExpiration, authorities);
    }

    private String buildToken( String username, long expiration, List<String> authorities) {
        return Jwts.builder()
                .setClaims(Map.of("username", username, "authorities", authorities))
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.get("username", String.class);

            boolean isUsernameValid = username.equals(userDetails.getUsername());
            boolean isNotExpired = !claims.getExpiration().before(new Date());

            return isUsernameValid && isNotExpired;

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
}