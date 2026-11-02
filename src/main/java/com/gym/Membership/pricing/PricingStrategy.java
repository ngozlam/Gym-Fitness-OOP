package com.gym.Membership.pricing;

import java.math.BigDecimal;

import com.gym.members.Member;
import com.gym.Membership.Model.MembershipPlan;

public interface PricingStrategy {

    BigDecimal computePrice(MembershipPlan plan, Member member);

}