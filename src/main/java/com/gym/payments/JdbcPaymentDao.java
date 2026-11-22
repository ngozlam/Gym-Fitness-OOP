package com.gym.payments;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcPaymentDao implements PaymentDao {

    // Đối tượng hỗ trợ thực hiện các truy vấn SQL.
    private final JdbcTemplate jdbcTemplate;

    /**
     * Chuyển một dòng dữ liệu trong bảng payments
     * thành đối tượng Payment.
     */
    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) ->
        new Payment(
            rs.getLong("id"),
            rs.getLong("subscription_id"),
            rs.getBigDecimal("amount"),
            rs.getTimestamp("payment_date").toLocalDateTime(),
            rs.getString("method")
        );

    /**
     * Constructor nhận JdbcTemplate từ Spring thông qua
     * Dependency Injection.
     */
    public JdbcPaymentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lưu một giao dịch thanh toán mới vào bảng payments.
     *
     * Sau khi INSERT thành công, lấy ID tự tăng do MySQL tạo
     * và gán lại vào đối tượng Payment.
     *
     * @param payment thông tin giao dịch cần lưu
     * @return đối tượng Payment đã có ID
     */
    @Override
    public Payment save(Payment payment) {

        String sql = """
            INSERT INTO payments
                (subscription_id, amount, payment_date, method)
            VALUES (?, ?, ?, ?)
            """;

        // Dùng KeyHolder để nhận ID tự tăng sau khi INSERT.
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            // Yêu cầu JDBC trả về khóa chính vừa được tạo.
            PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            );

            // Gán các giá trị vào dấu ? trong câu SQL.
            ps.setLong(1, payment.getSubscriptionId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setTimestamp(
                3, Timestamp.valueOf(payment.getPaymentDate())
            );
            ps.setString(4, payment.getMethod());

            return ps;

        }, keyHolder);

        // Lấy ID của giao dịch vừa được thêm vào database.
        Number key = keyHolder.getKey();

        // Nếu MySQL không trả về ID thì thông báo lỗi.
        if (key == null) {
            throw new IllegalStateException(
                "MySQL không trả về ID của payment vừa tạo."
            );
        }

        // Cập nhật ID mới cho đối tượng Payment.
        payment.setId(key.longValue());

        return payment;
    }

    /**
     * Lấy toàn bộ lịch sử thanh toán của một Subscription.
     *
     * Kết quả được sắp xếp theo ngày thanh toán giảm dần,
     * nghĩa là giao dịch mới nhất xuất hiện trước.
     *
     * @param subscriptionId mã gói đăng ký
     * @return danh sách các giao dịch thanh toán
     */
    @Override
    public List<Payment> findBySubscriptionId(long subscriptionId) {

        String sql = """
            SELECT id, subscription_id, amount, payment_date, method
            FROM payments
            WHERE subscription_id = ?
            ORDER BY payment_date DESC
            """;

        return jdbcTemplate.query(
            sql, paymentRowMapper, subscriptionId
        );
    }

    /**
     * Lấy toàn bộ lịch sử thanh toán của một hội viên.
     *
     * JOIN bảng payments với subscriptions thông qua
     * subscription_id để xác định các giao dịch thuộc hội viên.
     *
     * @param memberId mã hội viên
     * @return danh sách giao dịch thanh toán của hội viên
     */
    @Override
    public List<Payment> findByMemberId(long memberId) {

        String sql = """
            SELECT p.id, p.subscription_id, p.amount,
                   p.payment_date, p.method
            FROM payments p
            JOIN subscriptions s
                ON p.subscription_id = s.id
            WHERE s.member_id = ?
            ORDER BY p.payment_date DESC
            """;

        return jdbcTemplate.query(
            sql, paymentRowMapper, memberId
        );
    }

    /**
     * Lấy tổng giá tiền cuối cùng của một Subscription.
     *
     * Dữ liệu được lấy từ cột final_price trong bảng subscriptions.
     *
     * @param subscriptionId mã gói đăng ký
     * @return giá cuối cùng của gói đăng ký
     */
    @Override
    public BigDecimal findSubscriptionFinalPrice(long subscriptionId) {

        String sql = """
            SELECT final_price
            FROM subscriptions
            WHERE id = ?
            """;

        return jdbcTemplate.queryForObject(
            sql, BigDecimal.class, subscriptionId
        );
    }
}
