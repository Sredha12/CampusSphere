package com.campussphere.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory rate limiter to mitigate brute-force password guessing and setup abuse.
 * <p>
 * Keyed by:
 * <ul>
 *   <li>{@code <lowercase_email>:<client_ip>} for login attempts</li>
 *   <li>{@code setup<client_ip>} for /setup bootstrap attempts</li>
 * </ul>
 * <p>
 * Policy: 5 failed attempts within 15 minutes block further attempts for that key for 15 minutes.
 * Successful authentication resets the counter for that key.
 * <p>
 * NOTE FOR ACADEMIC PROJECT: This throttle stores state entirely in-memory using a
 * {@link ConcurrentHashMap}. It will reset if the web application or Tomcat server
 * restarts, which is expected and standard for this college project architecture.
 */
public final class LoginThrottle {

    public static final String THROTTLE_ERROR_MESSAGE = "Too many attempts. Please try again in a few minutes.";

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = 15L * 60L * 1000L; // 15 minutes

    private static final ConcurrentMap<String, AttemptTracker> ATTEMPTS = new ConcurrentHashMap<>();

    private LoginThrottle() {
        // Prevent instantiation
    }

    /**
     * Checks if the given key is currently blocked from further attempts.
     *
     * @param key unique identifier (e.g. lowercase email + ":" + client IP)
     * @return true if currently blocked; false otherwise
     */
    public static boolean isBlocked(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }

        AttemptTracker tracker = ATTEMPTS.get(key);
        if (tracker == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        synchronized (tracker) {
            if (tracker.blockedUntil > now) {
                return true;
            }
            // If block period has expired, reset tracker
            if (tracker.blockedUntil > 0 && tracker.blockedUntil <= now) {
                ATTEMPTS.remove(key);
                return false;
            }
            // If attempt window expired without blocking, reset
            if (now - tracker.firstAttemptTime > WINDOW_MILLIS) {
                ATTEMPTS.remove(key);
                return false;
            }
            return false;
        }
    }

    /**
     * Records a failed attempt for the given key.
     * Increments attempt count or triggers a 15-minute block if maximum attempts reached.
     *
     * @param key unique identifier
     */
    public static void recordFailure(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        long now = System.currentTimeMillis();
        ATTEMPTS.compute(key, (k, existing) -> {
            if (existing == null) {
                return new AttemptTracker(1, now, 0);
            }

            synchronized (existing) {
                // If existing window expired, restart counter
                if (now - existing.firstAttemptTime > WINDOW_MILLIS) {
                    existing.failedCount = 1;
                    existing.firstAttemptTime = now;
                    existing.blockedUntil = 0;
                    return existing;
                }

                existing.failedCount++;
                if (existing.failedCount >= MAX_ATTEMPTS) {
                    existing.blockedUntil = now + WINDOW_MILLIS;
                }
                return existing;
            }
        });
    }

    /**
     * Resets failure counter upon a successful authentication.
     *
     * @param key unique identifier
     */
    public static void recordSuccess(String key) {
        if (key != null) {
            ATTEMPTS.remove(key);
        }
    }

    /**
     * Helper to build a standard login throttle key.
     *
     * @param email    email address
     * @param clientIp client IP address from request.getRemoteAddr()
     * @return normalized key string
     */
    public static String buildLoginKey(String email, String clientIp) {
        String normalizedEmail = (email != null) ? email.trim().toLowerCase() : "unknown";
        String normalizedIp = (clientIp != null && !clientIp.isBlank()) ? clientIp.trim() : "0.0.0.0";
        return normalizedEmail + ":" + normalizedIp;
    }

    /**
     * Helper to build a setup throttle key.
     *
     * @param clientIp client IP address
     * @return key formatted as "setup" + IP
     */
    public static String buildSetupKey(String clientIp) {
        String normalizedIp = (clientIp != null && !clientIp.isBlank()) ? clientIp.trim() : "0.0.0.0";
        return "setup" + normalizedIp;
    }

    private static class AttemptTracker {
        private int failedCount;
        private long firstAttemptTime;
        private long blockedUntil;

        private AttemptTracker(int failedCount, long firstAttemptTime, long blockedUntil) {
            this.failedCount = failedCount;
            this.firstAttemptTime = firstAttemptTime;
            this.blockedUntil = blockedUntil;
        }
    }
}
