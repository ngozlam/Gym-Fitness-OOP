package com.gym.Membership.Subscription_DAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.gym.Membership.Model.Subscription;

@Repository 
public class JdbcSubscriptionDao implements SubscriptionDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Subscription> mapper = (rs, rowNum) -> new Subscription(
        rs.getLong("id"),
        rs.getLong("member_id"),
        rs.getLong("plan_id"),
        rs.getDate("start_date").toLocalDate(),
        rs.getDate("end_date").toLocalDate(),
        rs.getString("status"),
        rs.getBigDecimal("final_price")
    );
        
    public JdbcSubscriptionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override 
    public List<Subscription> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM subscriptions ORDER BY id DESC", mapper);
    }

    @Override 
    public Optional<Subscription> findById(Long id) {
        List<Subscription> result = jdbcTemplate.query(
            "SELECT * FROM subscriptions WHERE id = ?", mapper, id);
        return result.stream().findFirst();    
    }

    //nhập id tra danh sách người đã đăng kí gói gì từ trước giờ của người đó
    @Override 
    public List<Subscription> findByMemberId(Long memberId) {
        return jdbcTemplate.query(
            """
            SELECT * FROM subscriptions
            WHERE member_id = ?
            ORDER BY id DESC
            """,
            mapper,memberId);
    }

    //tìm gói đang còn hiệu lực của người đó qua memberId
    @Override
    public List<Subscription> findActiveByMemberId(Long memberId) {
        return jdbcTemplate.query(
                """
                SELECT * FROM subscriptions
                WHERE member_id = ?
                  AND status = 'ACTIVE'
                  AND end_date >= CURRENT_DATE
                ORDER BY end_date DESC
                """,
                mapper,memberId
        );
    }

    @Override 
    public long save(Subscription subscription) {
        String sql = """
            INSERT INTO subscriptions
                (member_id, plan_id, start_date, end_date,
                    status, final_price)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, subscription.getMemberId());
            ps.setLong(2, subscription.getPlanId());
            ps.setDate(3, Date.valueOf(subscription.getStartDate()));
            ps.setDate(4, Date.valueOf(subscription.getEndDate()));
            ps.setString(5, subscription.getStatus());
            ps.setBigDecimal(6, subscription.getFinalPrice());

            return ps;
        },keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException(
                    "MySQL không trả về ID của subscription vừa tạo."
            );
        }
        long id = key.longValue();
        subscription.setId(id);
        return id;
    }

    @Override 
    public boolean update(Subscription subscription) {
        String sql = """
            UPDATE subscriptions
            SET member_id = ?,
                plan_id = ?,
                start_date = ?,
                end_date = ?,
                status = ?,
                final_price = ?
            WHERE id = ?
            """;

        int rows = jdbcTemplate.update(sql,
            subscription.getMemberId(),
            subscription.getPlanId(),
            Date.valueOf(subscription.getStartDate()),
            Date.valueOf(subscription.getEndDate()),
            subscription.getStatus(),
            subscription.getFinalPrice(),
            subscription.getId() 
        );

        return rows > 0;
    }
    

    @Override 
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM subscriptions WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
}
