package com.gym.Membership.JDBCPlan_DAO;

import java.util.List;
import java.util.Optional;

import com.gym.Membership.Model.MembershipPlan;

public interface PlanDao {

    List<MembershipPlan> findAll();

    Optional<MembershipPlan> findById(Long id);

    long save(MembershipPlan plan);

    boolean update(MembershipPlan plan);

    boolean deleteById(Long id);
} 
