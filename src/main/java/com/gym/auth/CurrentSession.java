package com.gym.auth;

import org.springframework.stereotype.Component;

/**
 * Singleton - lưu thông tin người dùng hiện tại đang login
 */
@Component
public class CurrentSession {

    private Staff currentUser;

    /**
     * Lưu user đã login
     */
    public void setCurrentUser(Staff user) {
        this.currentUser = user;
    }

    /**
     * Lấy user hiện tại
     */
    public Staff getCurrentUser() {
        return currentUser;
    }

    /**
     * Kiểm tra có user nào login chưa
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Xóa user khi logout
     */
    public void logout() {
        this.currentUser = null;
    }
}