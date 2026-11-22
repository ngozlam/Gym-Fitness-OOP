package com.gym.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model đại diện cho một giao dịch thanh toán.
 * Mỗi Payment thuộc về một Subscription.
 */
public class Payment {

    private Long id;
    private Long subscriptionId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String method;

    // Constructor không tham số
    public Payment() {
    }

    // Constructor đầy đủ tham số
    public Payment(Long id, Long subscriptionId,
                   BigDecimal amount,
                   LocalDateTime paymentDate,
                   String method) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", subscriptionId=" + subscriptionId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", method='" + method + '\'' +
                '}';
    }
}
