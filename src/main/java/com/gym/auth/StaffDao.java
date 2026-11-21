package com.gym.auth;

import java.util.Optional;

public interface StaffDao {
    Optional<Staff> findByUsername(String username);
}