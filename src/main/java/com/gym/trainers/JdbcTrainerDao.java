package com.gym.trainers;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTrainerDao implements TrainerDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTrainerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Trainer> rowMapper = (rs , rowNum) ->{
        Trainer t = new Trainer();
        t.setId(rs.getLong("id"));
        t.setFullName(rs.getString("full_name"));
        t.setSpecialty(rs.getString("specialty"));
        t.setPhone(rs.getString("phone"));
        return t;
    };
    
    @Override
    public List<Trainer> findAll() {
        String sql = "SELECT * FROM trainers ";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Trainer> findById(long id) {
        String sql = "SELECT * FROM trainers WHERE id = ?";
        List<Trainer> result = jdbcTemplate.query(sql,rowMapper,id);
        return result.stream().findFirst();
    }

    @Override
    public Trainer save(Trainer t) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO trainers (full_name, specialty, phone) VALUES (?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, t.getFullName());
            ps.setString(2, t.getSpecialty());
            ps.setString(3, t.getPhone());
            return ps;
        }, keys);
        Number key = keys.getKey();
        if (key == null) {
            throw new IllegalStateException("Không lấy được id vừa tạo");
        }
        t.setId(key.longValue());
        return t;
    }

    @Override
    public Trainer update(Trainer t) {
    int rows = jdbcTemplate.update("UPDATE trainers SET full_name = ?, specialty = ?, phone = ? WHERE id = ?",
            t.getFullName(), t.getSpecialty(), t.getPhone(), t.getId());
    if (rows == 0) {
        throw new IllegalArgumentException("Trainer not found: id=" + t.getId());
    }
    return t;
}
    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM trainers WHERE id = ?", id);
    }
}

