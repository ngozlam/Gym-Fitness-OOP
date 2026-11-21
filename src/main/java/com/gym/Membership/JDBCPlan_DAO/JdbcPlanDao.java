package com.gym.Membership.JDBCPlan_DAO;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.gym.Membership.Model.MembershipPlan;

@Repository
public class JdbcPlanDao implements PlanDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<MembershipPlan> mapper = (rs, rowNum) -> 
        new MembershipPlan(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("duration_months"),
            rs.getBigDecimal("base_price"),
            rs.getString("pricing_strategy")
        );

    public JdbcPlanDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }   

    @Override 
    public List<MembershipPlan> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM membership_plans ORDER BY id", mapper);
    }

    @Override 
    public Optional<MembershipPlan> findById(Long id) {
        List<MembershipPlan> plans = jdbcTemplate.query(
            "SELECT * FROM membership_plans WHERE id = ?", mapper, id);
        return plans.stream().findFirst();
    }

    @Override 
    public long save(MembershipPlan plan) {
        String sql = """
            INSERT INTO membership_plans
                (name, duration_months, base_price, pricing_strategy)
            VALUES (?, ?, ?, ?)
            """;

        //keyHolder nhận ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS);
    
            ps.setString(1, plan.getName());
            ps.setInt(2, plan.getDurationMonths());
            ps.setBigDecimal(3, plan.getBasePrice());
            ps.setString(4, plan.getPricingStrategy());
            
            return ps;
        } ,keyHolder );
        
        Number key = keyHolder.getKey();
        if(key == null) {
            throw new IllegalArgumentException(
                "SQL không trả về ID của gói tập"
            );
        }

        long id = key.longValue();
        plan.setId(id);
        return id;
    }

    @Override 
    public boolean update(MembershipPlan plan) {
        String sql = """
                UPDATE membership_plans
                SET name = ?,
                    duration_months = ?,
                    base_price = ?,
                    pricing_strategy = ?
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(
            sql,
            plan.getName(),
            plan.getDurationMonths(),
            plan.getBasePrice(),
            plan.getPricingStrategy(),
            plan.getId()
        );
        
        return rows > 0;
    }

    @Override 
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM membership_plans WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
}
