package com.gym.auth;

import java.util.Arrays;
import java.util.List;

public class Admin extends Staff {

    public Admin() {}

    public Admin(Long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, "ADMIN");
    }

    @Override
    public List<String> getAccessibleModules() {
        // Admin thấy tất cả
        return Arrays.asList("members", "trainers", "plans", "payments", "attendance");
    }
}