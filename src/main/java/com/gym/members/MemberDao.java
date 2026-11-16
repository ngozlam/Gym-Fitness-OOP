package com.gym.members;

import java.util.List;
import java.util.Optional;

public interface MemberDao {
        List<Member> findAll();
        Optional<Member> findById(long id);
        List<Member> search(String nameOrEmail);
        Optional<Member> findByEmail(String email);
        Member save(Member member);
        void update(Member member);
        void delete(long id);
}
