package com.gym.Membership.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import com.gym.Membership.Model.MembershipPlan;
import com.gym.members.Member;

@Component("STUDENT_DISCOUNT")
public class StudentDiscountPricing implements PricingStrategy {

    private static final BigDecimal PUPIL_DISCOUNT = new BigDecimal("0.20");
    private static final BigDecimal STUDENT_DISCOUNT = new BigDecimal("0.10");

    @Override
    public BigDecimal computePrice(MembershipPlan plan, Member member) {
        LocalDate dob = member.getDateOfBirth();
        if (dob == null || dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        BigDecimal rate;
        if (age < 18) {
            rate = PUPIL_DISCOUNT;
        } else if (age <= 22) {
            rate = STUDENT_DISCOUNT;
        } else {
            throw new IllegalArgumentException("Gói này chỉ dành cho học sinh hoặc sinh viên.");
        }

        BigDecimal basePrice = plan.getBasePrice();
        return basePrice.subtract(basePrice.multiply(rate)).setScale(2, RoundingMode.HALF_UP);
    }
}