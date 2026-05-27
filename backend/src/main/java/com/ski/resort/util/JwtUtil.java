package com.ski.resort.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {
    
    /**
     * 密钥（实际项目中应该放在配置文件中）
     */
    private static final String SECRET_KEY = "ski-resort-management-system-secret-key-2025-very-long-key";
    
    /**
     * Token有效期（7天）
     */
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    
    /**
     * 生成密钥
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return Token字符串
     */
    public static String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 解析Token
     * @param token Token字符串
     * @return Claims对象
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 验证Token是否有效
     * @param token Token字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }
            
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从Token中获取用户ID
     * @param token Token字符串
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
    
    /**
     * 从Token中获取用户名
     * @param token Token字符串
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }
    
    /**
     * 从Token中获取角色
     * @param token Token字符串
     * @return 角色
     */
    public static String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return (String) claims.get("role");
    }
    
    /**
     * 判断Token是否即将过期（剩余时间少于1天）
     * @param token Token字符串
     * @return 是否即将过期
     */
    public static boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return true;
            }
            
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            
            // 剩余时间少于1天
            return remainingTime < 24 * 60 * 60 * 1000;
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 刷新Token
     * @param token 旧Token
     * @return 新Token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        
        Long userId = getUserIdFromToken(token);
        String username = getUsernameFromToken(token);
        String role = getRoleFromToken(token);
        
        return generateToken(userId, username, role);
    }
}







