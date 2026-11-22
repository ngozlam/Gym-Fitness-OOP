
package com.gym.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PaymentService xử lý các nghiệp vụ liên quan đến thanh toán.
 *
 * Bao gồm:
 * - Ghi nhận thanh toán.
 * - Kiểm tra số tiền thanh toán.
 * - Tính số tiền còn nợ.
 * - Lấy lịch sử thanh toán.
 */
@Service
public class PaymentService {

    // Các phương thức thanh toán được hệ thống chấp nhận.
    private static final List<String> METHODS =
            List.of("CASH", "CARD", "TRANSFER");

    // DAO dùng để truy xuất dữ liệu thanh toán.
    private final PaymentDao paymentDao;

    /**
     * Constructor nhận PaymentDao thông qua Dependency Injection.
     */
    public PaymentService(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    /**
     * Ghi nhận một giao dịch thanh toán mới.
     *
     * Các bước kiểm tra:
     * 1. Subscription ID phải lớn hơn 0.
     * 2. Số tiền thanh toán phải lớn hơn 0.
     * 3. Phương thức thanh toán phải hợp lệ.
     * 4. Subscription phải tồn tại.
     * 5. Số tiền thanh toán không được vượt quá số dư còn nợ.
     *
     * @param subscriptionId mã gói đăng ký
     * @param amount số tiền thanh toán
     * @param method phương thức thanh toán
     * @return giao dịch thanh toán sau khi lưu
     */
    @Transactional
    public Payment recordPayment(
            long subscriptionId,
            BigDecimal amount,
            String method) {

        // Kiểm tra Subscription ID hợp lệ.
        if (subscriptionId <= 0) {
            throw new IllegalArgumentException(
                    "Subscription ID must be positive.");
        }

        // Kiểm tra số tiền thanh toán.
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero.");
        }

        // Kiểm tra phương thức thanh toán.
        if (method == null ||
                !METHODS.contains(method.trim().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Method must be CASH, CARD or TRANSFER.");
        }

        // Lấy số tiền còn nợ của Subscription.
        BigDecimal balance = getOutstandingBalance(subscriptionId);

        // Không cho phép thanh toán vượt quá số dư còn nợ.
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds the outstanding balance.");
        }

        // Tạo đối tượng Payment mới với thời gian hiện tại.
        Payment payment = new Payment(
                null,
                subscriptionId,
                amount,
                LocalDateTime.now(),
                method.trim().toUpperCase()
        );

        // Gọi DAO để lưu giao dịch vào database.
        return paymentDao.save(payment);
    }

    /**
     * Tính số tiền còn nợ của một Subscription.
     *
     * Công thức:
     * Số tiền còn nợ = Giá gói đăng ký - Tổng tiền đã thanh toán.
     *
     * Nếu Subscription không tồn tại thì thông báo lỗi.
     *
     * @param subscriptionId mã gói đăng ký
     * @return số tiền còn nợ
     */
    public BigDecimal getOutstandingBalance(long subscriptionId) {

        BigDecimal finalPrice;

        try {
            // Lấy giá cuối cùng của gói đăng ký.
            finalPrice = paymentDao.findSubscriptionFinalPrice(
                    subscriptionId);

         } catch (EmptyResultDataAccessException e) {

            // Không tìm thấy Subscription trong database.
            throw new IllegalStateException(
                    "Subscription does not exist.", e);
        }

        // Kiểm tra trường hợp DAO trả về null
        // khi Subscription không tồn tại.
        if (finalPrice == null) {
            throw new IllegalStateException(
                    "Subscription does not exist.");
        }

        // Lấy toàn bộ giao dịch thanh toán của Subscription.
        // Sau đó cộng tất cả số tiền đã thanh toán.
        BigDecimal paid = paymentDao
                .findBySubscriptionId(subscriptionId)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính số dư còn nợ.
        BigDecimal balance = finalPrice.subtract(paid);

        // Nếu số tiền đã thanh toán lớn hơn giá gói,
        // trả về số dư bằng 0 thay vì số âm.
        return balance.max(BigDecimal.ZERO);
    }

    /**
     * Lấy lịch sử thanh toán theo Subscription ID.
     *
     * @param subscriptionId mã gói đăng ký
     * @return danh sách giao dịch của Subscription
     */
    public List<Payment> getPaymentsBySubscription(
            long subscriptionId) {

        return paymentDao.findBySubscriptionId(subscriptionId);
    }

    /**
     * Lấy lịch sử thanh toán theo Member ID.
     *
     * @param memberId mã hội viên
     * @return danh sách giao dịch của hội viên
     */
    public List<Payment> getPaymentsByMember(long memberId) {

        return paymentDao.findByMemberId(memberId);
    }
}
