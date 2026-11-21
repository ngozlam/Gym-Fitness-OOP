package com.gym.membership.pricing;


import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.gym.members.Member;
import com.gym.membership.Model.MembershipPlan;

@Component("STANDARD")
public class StandardPricing implements PricingStrategy {
    
    public BigDecimal computePrice(MembershipPlan plan, Member member) {
        return plan.getBasePrice();
    }
}


