package com.task.api.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MS = 5 * 60 * 1000;

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockTimeCache.put(username, System.currentTimeMillis()); // Guarda el tiempo de bloqueo
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }


    public boolean isBlocked(String username) {
        Long lockTime = lockTimeCache.get(username);

        if (lockTime == null) {
            return false; // No está bloqueado
        }

        if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
            lockTimeCache.remove(username); // Desbloquear después del tiempo
            attemptsCache.remove(username);
            return false;
        }

        return true;
    }
}
