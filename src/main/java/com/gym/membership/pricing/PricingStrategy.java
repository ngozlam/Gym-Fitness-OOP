package com.gym.membership.pricing;

import java.math.BigDecimal;

import com.gym.members.Member;
import com.gym.membership.MembershipPlan;


public interface PricingStrategy {

    BigDecimal computePrice(MembershipPlan plan, Member member);

}