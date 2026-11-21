package com.gym.membership;

import java.util.List;
import java.util.Optional;

public interface SubscriptionDao {
    
    List<Subscription> findAll();
    
    Optional<Subscription> findById(Long id);

    List<Subscription> findByMemberId(Long memberId);

    List<Subscription> findActiveByMemberId(Long memberId);

    long save(Subscription subscription);

    boolean update(Subscription subscription);

    boolean deleteById(Long id);
} 