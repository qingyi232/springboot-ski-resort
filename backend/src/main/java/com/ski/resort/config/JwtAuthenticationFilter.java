package com.ski.resort.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ski.resort.common.Result;
import com.ski.resort.common.ResultCode;
import com.ski.resort.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    /**
     * 不需要认证的路径
     */
    private static final String[] EXCLUDE_URLS = {
        "/api/v1/users/login",
        "/api/v1/users/register",
        "/doc.html",
        "/swagger-ui",
        "/swagger-resources",
        "/v2/api-docs",
        "/v3/api-docs",
        "/webjars"
    };
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        
        // 跨域OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 检查是否是不需要认证的路径
        if (isExcludeUrl(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            returnUnauthorized(response, "请先登录");
            return;
        }
        
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            returnUnauthorized(response, "登录已过期，请重新登录");
            return;
        }
        
        // 将用户信息存入请求属性
        Long userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);
        
        // 放行
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头中获取Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
    
    /**
     * 检查是否是不需要认证的路径
     */
    private boolean isExcludeUrl(String uri) {
        for (String excludeUrl : EXCLUDE_URLS) {
            if (uri.contains(excludeUrl)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 返回未授权错误
     */
    private void returnUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Void> result = Result.error(ResultCode.UNAUTHORIZED, message);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();
    }
}







