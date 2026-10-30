package com.gym.auth;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final StaffDao staffDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(StaffDao staffDao, PasswordEncoder passwordEncoder) {
        this.staffDao = staffDao;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Kiểm tra username/password có đúng không
     * @param username tên đăng nhập
     * @param password mật khẩu nhập vào (plaintext)
     * @return Staff nếu đúng, empty nếu sai
     */
    public Optional<Staff> login(String username, String password) {
        // Tìm staff theo username
        Optional<Staff> staff = staffDao.findByUsername(username);

        // Nếu tìm được và password đúng
        if (staff.isPresent()) {
            Staff s = staff.get();
            // So sánh password nhập vào với hash trong DB
            if (passwordEncoder.matches(password, s.getPasswordHash())) {
                return Optional.of(s);
            }
        }

        // Sai username hoặc password
        return Optional.empty();
    }
}
