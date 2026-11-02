package com.gym.Membership.pricing;


import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.gym.Membership.Model.MembershipPlan;
import com.gym.members.Member;

@Component("STANDARD")
public class StandardPricing implements PricingStrategy {
    
    public BigDecimal computePrice(MembershipPlan plan, Member member) {
        return plan.getBasePrice();
    }
}


