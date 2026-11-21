package com.gym.auth;

import java.util.Arrays;
import java.util.List;

public class TrainerStaff extends Staff {

    public TrainerStaff() {}

    public TrainerStaff(Long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, "TRAINER");
    }

    @Override
    public List<String> getAccessibleModules() {
        // Trainer thấy Member + Attendance
        return Arrays.asList("members", "attendance");
    }
}