package com.can.behavirol.chainofresponsibility;

public class OrderRequest
{
    private final String username;
    private final String password;
    private final String ipAddress;
    private final OrderOperation operation;
    private String payload;
    private User authenticatedUser;

    public OrderRequest(String username, String password, String ipAddress, OrderOperation operation, String payload) {
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
        this.operation = operation;
        this.payload = payload;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public OrderOperation getOperation() {
        return operation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
}
