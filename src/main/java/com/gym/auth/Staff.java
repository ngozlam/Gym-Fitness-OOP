package com.gym.auth;

import java.util.List;

public abstract class Staff {
    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role; // ADMIN, TRAINER, RECEPTIONIST

    public Staff() {}

    public Staff(Long id, String username, String passwordHash, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    // Abstract method - subclass phải override
    public abstract List<String> getAccessibleModules();

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}