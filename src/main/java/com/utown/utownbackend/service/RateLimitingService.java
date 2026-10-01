package com.utown.utownbackend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> standardBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    // Global rate limit: 100 requests per minute
    public Bucket resolveStandardBucket(String ip) {
        return standardBuckets.computeIfAbsent(ip, this::newStandardBucket);
    }

    // Auth rate limit: 5 requests per minute
    public Bucket resolveAuthBucket(String ip) {
        return authBuckets.computeIfAbsent(ip, this::newAuthBucket);
    }

    private Bucket newStandardBucket(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build();
    }

    private Bucket newAuthBucket(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }
}
