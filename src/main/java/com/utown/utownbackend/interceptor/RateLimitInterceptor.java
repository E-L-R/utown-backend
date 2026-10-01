package com.utown.utownbackend.interceptor;

import com.utown.utownbackend.exception.RateLimitExceededException;
import com.utown.utownbackend.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIP(request);
        String requestURI = request.getRequestURI();

        Bucket bucket;
        boolean isAuthEndpoint = requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/register");
        
        if (isAuthEndpoint) {
            bucket = rateLimitingService.resolveAuthBucket(clientIp);
        } else {
            bucket = rateLimitingService.resolveStandardBucket(clientIp);
        }

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
