package com.gym.auth;

import java.util.Arrays;
import java.util.List;

public class Receptionist extends Staff {

    public Receptionist() {}

    public Receptionist(Long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, "RECEPTIONIST");
    }

    @Override
    public List<String> getAccessibleModules() {
        // Receptionist thấy Member + Subscription + Payment
        return Arrays.asList("members", "subscriptions", "payments");
    }
}