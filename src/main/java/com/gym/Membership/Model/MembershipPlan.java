package com.gym.Membership.Model;

import java.math.BigDecimal;

//   Gói thành viên
public class MembershipPlan {
    private Long id;
    private String name;
    private int durationMonths;     // thời hạn
    private BigDecimal basePrice;   // giá gốc
    private String pricingStrategy; // chiến lược định giá

    public MembershipPlan() {}

    public MembershipPlan(Long id, String name, int durationMonths, BigDecimal basePrice, String pricingStrategy) {
        this.id = id;
        this.name = name;
        this.durationMonths = durationMonths;
        this.basePrice = basePrice;
        this.pricingStrategy = pricingStrategy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getPricingStrategy() {
        return pricingStrategy;
    }

    public void setPricingStrategy(String pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

}