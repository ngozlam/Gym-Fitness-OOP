package com.gym.Membership.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.Membership.JDBCPlan_DAO.PlanDao;
import com.gym.Membership.Model.MembershipPlan;
import com.gym.Membership.Model.Subscription;
import com.gym.Membership.Subscription_DAO.SubscriptionDao;
import com.gym.Membership.pricing.PricingStrategy;
import com.gym.members.Member;
import com.gym.members.MemberDao;


@Service 
public class SubscriptionService {
    //kiểm tra nhập dữ liệu gói tập vào database
    private void validatePlan(MembershipPlan plan) {
        if(plan.getDurationMonths() <=0 ) {
            throw new IllegalArgumentException("Thời hạn gói tập phải lớn hơn 0 tháng!");
        }

        if(plan.getBasePrice() == null || plan.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá gốc của gói tập không được âm");
        }
    }
    
    private final MemberDao memberDao;
    private final SubscriptionDao subscriptionDao;
    private final PlanDao planDao;
    private Map<String, PricingStrategy> pricingStrategies;

    public SubscriptionService(MemberDao memberDao,
        SubscriptionDao subscriptionDao,
        PlanDao planDao,
        Map<String, PricingStrategy> pricingStrategies) {
            this.memberDao = memberDao;
            this.subscriptionDao = subscriptionDao;
            this.planDao = planDao;
            this.pricingStrategies = pricingStrategies;
        }

    @Transactional 
    public Subscription enroll(Long memberId, Long planId) {
        Member member = memberDao.findById(memberId).orElseThrow(()-> new IllegalArgumentException(
            "Không tìm thấy thành viên ID: " + memberId )
        );

        MembershipPlan plan = planDao.findById(planId).orElseThrow(() -> new IllegalArgumentException(
            "Không tìm thấy gói tập ID: " + planId)
        );

        validatePlan(plan);
        String strategyName = plan.getPricingStrategy();
        PricingStrategy strategy = pricingStrategies.get(strategyName);
        if(strategy == null) {
            throw new IllegalArgumentException("Chiến lược tính giá không hợp lệ " + strategy);
        }

        BigDecimal finalPrice = strategy.computePrice(plan, member);
        if(finalPrice == null || finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá trị cuối cùng không hợp lệ!");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate =
                startDate.plusMonths(plan.getDurationMonths());

        Subscription subscription = new Subscription();
        subscription.setMemberId(memberId);
        subscription.setPlanId(planId);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus("ACTIVE");
        subscription.setFinalPrice(finalPrice);

        subscriptionDao.save(subscription);

        return subscription;
    }

    @Transactional
    public Subscription renew(Long subscriptionId) {
        Subscription subscription = subscriptionDao.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy subscription ID: " + subscriptionId));

        if (!"ACTIVE".equals(subscription.getStatus())) {
            throw new IllegalArgumentException("Không thể gia hạn gói tập");
        }

        MembershipPlan plan = planDao.findById(subscription.getPlanId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Không tìm thấy gói tập của subscription ID: " + subscriptionId));

        LocalDate today = LocalDate.now();
        LocalDate newStartDate = subscription.getEndDate().isBefore(today)? today: subscription.getEndDate();

        LocalDate newEndDate = newStartDate.plusMonths(plan.getDurationMonths());
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscriptionDao.update(subscription);
        return subscription;
    }

    @Transactional 
    public Subscription cancel(Long subscriptionId) {
        Subscription subscription = subscriptionDao.findById(subscriptionId).orElseThrow(() -> new IllegalArgumentException(
            "Không tìm thấy subscription ID: " + subscriptionId
        ));

        if("CANCELLED".equals(subscription.getStatus())) {
            throw new IllegalArgumentException("Subscription này đã bị hủy.");
        }

        subscription.setStatus("CANCELLED");
        subscriptionDao.update(subscription);
        return subscription;
    }
}
