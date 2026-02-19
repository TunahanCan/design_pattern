package com.can.behavirol.chainofresponsibility;

public class BruteForceProtectionHandler extends BaseOrderRequestHandler {
    private final LoginAttemptService loginAttemptService;

    public BruteForceProtectionHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public boolean handle(OrderRequest request) {
        if (loginAttemptService.isBlocked(request.getIpAddress())) {
            System.out.println("[BruteForceProtection] IP engellendi: " + request.getIpAddress());
            return false;
        }
        return checkNext(request);
    }
}
