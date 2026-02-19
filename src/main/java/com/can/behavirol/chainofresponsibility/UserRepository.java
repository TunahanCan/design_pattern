package com.can.behavirol.chainofresponsibility;

import java.util.Map;

public class UserRepository {
    private final Map<String, User> users;

    public UserRepository(Map<String, User> users) {
        this.users = Map.copyOf(users);
    }

    public User findByUsername(String username) {
        return users.get(username);
    }
}
