package com.gym.payments;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface định nghĩa các thao tác
 * truy cập dữ liệu thanh toán.
 */
public interface PaymentDao {

    // Lưu một giao dịch thanh toán
    Payment save(Payment payment);

    // Tìm lịch sử thanh toán theo Subscription
    List<Payment> findBySubscriptionId(long subscriptionId);

    // Tìm lịch sử thanh toán theo Member
    List<Payment> findByMemberId(long memberId);

    // Lấy giá cuối cùng của gói đăng ký
    BigDecimal findSubscriptionFinalPrice(long subscriptionId);
}
