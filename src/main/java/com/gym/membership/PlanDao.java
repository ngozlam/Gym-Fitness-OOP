package com.gym.membership;

import java.util.List;
import java.util.Optional;

public interface PlanDao {

    List<MembershipPlan> findAll();

    Optional<MembershipPlan> findById(Long id);

    long save(MembershipPlan plan);

    boolean update(MembershipPlan plan);

    boolean deleteById(Long id);
} 
