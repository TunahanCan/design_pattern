package com.can.behavirol.chainofresponsibility;

import java.util.HashMap;
import java.util.Map;

public class LoginAttemptService {
    private final int maxFailedAttempts;
    private final Map<String, Integer> failedAttemptsByIp = new HashMap<>();

    public LoginAttemptService(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public boolean isBlocked(String ipAddress) {
        return failedAttemptsByIp.getOrDefault(ipAddress, 0) >= maxFailedAttempts;
    }

    public void registerFailedAttempt(String ipAddress) {
        failedAttemptsByIp.merge(ipAddress, 1, Integer::sum);
    }

    public void reset(String ipAddress) {
        failedAttemptsByIp.remove(ipAddress);
    }
}
