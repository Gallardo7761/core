package net.miarma.api.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

public class RateLimiter {
    private static final int MAX_REQUESTS = 5; // max 5 requests
    private static final long WINDOW_MS = 60_000; // 1 minuto
    private Map<String, UserRequests> requests = new ConcurrentHashMap<>();

    static class UserRequests {
        int count;
        long windowStart;
    }

    public boolean allow(String ip) {
        long now = Instant.now().toEpochMilli();
        UserRequests ur = requests.getOrDefault(ip, new UserRequests());
        if (now - ur.windowStart > WINDOW_MS) {
            ur.count = 1;
            ur.windowStart = now;
        } else {
            ur.count++;
        }
        requests.put(ip, ur);
        return ur.count <= MAX_REQUESTS;
    }
}
