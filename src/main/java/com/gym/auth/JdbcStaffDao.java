package com.gym.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStaffDao implements StaffDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStaffDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Staff> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, role FROM staff WHERE username = ?";
        List<Staff> results = jdbcTemplate.query(sql, this::mapRow, username);
        return results.stream().findFirst();
    }

    private Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        // Dựa vào role tạo object phù hợp
        switch (role) {
            case "ADMIN":
                return new Admin(id, username, passwordHash, fullName);
            case "TRAINER":
                return new TrainerStaff(id, username, passwordHash, fullName);
            case "RECEPTIONIST":
                return new Receptionist(id, username, passwordHash, fullName);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}