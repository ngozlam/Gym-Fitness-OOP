package com.gym.members;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
@Repository 
public class JdbcMemberDao implements MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Member> memberRowMapper = this::mapRow;

    private Member mapRow(ResultSet rs, int rowNum) throws SQLException {
        Member m = new Member();
        m.setId(rs.getLong("id"));
        m.setFullName(rs.getString("full_name"));
        m.setEmail(rs.getString("email"));
        m.setPhone(rs.getString("phone"));
        m.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        m.setJoinedDate(rs.getObject("joined_date", LocalDate.class));

        long trainerId = rs.getLong("trainer_id");
        m.setTrainerId(rs.wasNull() ? null : trainerId);

        return m;
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT id, full_name, email, phone, date_of_birth, joined_date, trainer_id " +
                    "FROM members ORDER BY id";
        return jdbcTemplate.query(sql, memberRowMapper);
    }

    @Override
    public Optional<Member> findById(long id) {
        String sql = "SELECT id, full_name, email, phone, date_of_birth, joined_date, trainer_id " +
                    " FROM members WHERE id = ?";
        List<Member> results = jdbcTemplate.query(sql, memberRowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public List<Member> search(String nameOrEmail) {
        String sql = "SELECT id, full_name, email, phone, date_of_birth, joined_date, trainer_id " +
                    " FROM members" +
                    " WHERE full_name LIKE ? OR email LIKE ? " + "ORDER BY id";
        String keyword = (nameOrEmail == null) ? "" : nameOrEmail.trim();
        String pattern = "%" + escapeLike(keyword) +"%";

        return jdbcTemplate.query(sql, memberRowMapper, pattern, pattern);
    }

    public String escapeLike(String keyword) {
        return keyword.replace("\\", "\\\\")
                      .replace("%", "\\%")
                      .replace("_", "\\_");
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, full_name, email, phone, date_of_birth, joined_date, trainer_id" +
                    " FROM members WHERE email= ? ";
        List<Member> results = jdbcTemplate.query(sql, memberRowMapper, email);
        return results.stream().findFirst();
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO members (full_name, email, phone, date_of_birth, joined_date, trainer_id) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setObject(4, member.getDateOfBirth(), Types.DATE);
            ps.setObject(5, member.getJoinedDate(), Types.DATE);
            if (member.getTrainerId() != null) {
                ps.setLong(6, member.getTrainerId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        member.setId(keyHolder.getKey().longValue());
        return member;
    }

    @Override
    public void update(Member member) {
        if (member.getId() == null) {
            throw new IllegalArgumentException("Không thể cập nhật hội viên chưa có id");
        }

        String sql = "UPDATE members SET full_name = ?, email = ?, phone = ?, " +
                    "date_of_birth = ?, joined_date = ?, trainer_id = ? " +
                    "WHERE id = ?";

        Object[] args = {
            member.getFullName(),
            member.getEmail(),
            member.getPhone(),
            member.getDateOfBirth(),
            member.getJoinedDate(),
            member.getTrainerId(),
            member.getId()
        };

        int[] types = {
            Types.VARCHAR,
            Types.VARCHAR,
            Types.VARCHAR,
            Types.DATE,
            Types.DATE,
            Types.BIGINT,
            Types.BIGINT
        };

        int rows = jdbcTemplate.update(sql, args, types);
        if (rows == 0) {
            throw new EmptyResultDataAccessException(
                    "Không tìm thấy hội viên id=" + member.getId(), 1);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new EmptyResultDataAccessException(
                    "Không tìm thấy hội viên id=" + id, 1);
        }
    }

}
