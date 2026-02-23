package com.can.behavirol.chainofresponsibility;

public class AuthenticationHandler extends BaseOrderRequestHandler {

    private final UserRepository userRepository;

    private final LoginAttemptService loginAttemptService;

    public AuthenticationHandler(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public boolean handle(OrderRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !user.password().equals(request.getPassword())) {
            loginAttemptService.registerFailedAttempt(request.getIpAddress());
            System.out.println("[Authentication] Kimlik doğrulama başarısız: " + request.getUsername());
            return false;
        }

        loginAttemptService.reset(request.getIpAddress());
        request.setAuthenticatedUser(user);
        return checkNext(request);
    }
}
