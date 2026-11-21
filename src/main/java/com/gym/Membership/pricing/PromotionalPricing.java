
package com.gym.Membership.pricing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.gym.Membership.Model.MembershipPlan;
import com.gym.Membership.Model.Subscription;
import com.gym.Membership.Subscription_DAO.SubscriptionDao;
import com.gym.members.Member;

@Component("PROMOTIONAL")
public class PromotionalPricing implements PricingStrategy {

    private static final BigDecimal PROMOTION_AMOUNT = new BigDecimal("100000");
    private static final BigDecimal FIRST_MONTH_DISCOUNT = new BigDecimal("0.30");

    private final SubscriptionDao subscriptionDao;

    public PromotionalPricing(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    @Override 
    public BigDecimal computePrice(MembershipPlan plan, Member member) {
        List<Subscription> subscription = subscriptionDao.findByMemberId(member.getId());
        boolean isFirstRegistration = subscription.isEmpty();
        
        //giảm cố định 100k
        BigDecimal finalPrice = plan.getBasePrice().subtract(PROMOTION_AMOUNT);

        if(finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        if(isFirstRegistration) {
            finalPrice = finalPrice.multiply(BigDecimal.ONE.subtract(FIRST_MONTH_DISCOUNT));
        }

        return finalPrice;
    
    }
}
