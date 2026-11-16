package com.gym.members;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MemberService {
    private final MemberDao memberDao;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    //them hoi vien
    public Member register(Member member) {
        validate(member);
        ensureEmailValidable(member.getEmail(), null);

        if (member.getJoinedDate() == null) {
            member.setJoinedDate(LocalDate.now());
        }
        return memberDao.save(member);
    }

    //cap nhat thong tin hoi vien
    public void update(Member member) {
        if(member == null || member.getId() == null) {
            throw new IllegalArgumentException("Hội viên chưa có id, không thể cập nhật");
        }
        validate(member);
        ensureEmailValidable(member.getEmail(), member.getId());

        memberDao.update(member);
    }

    //xoa hoi vien
    public void delete(long id) {
        memberDao.delete(id);
    }

    //tim kiem hoi vien theo id
    public Optional<Member> findById(long id) {
        return memberDao.findById(id);
    }

    public List<Member> findAll() {
        return memberDao.findAll();
    }

    public List<Member> search(String keyword) {
        return memberDao.search(keyword);
    }

    private void validate(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Dữ liệu hội viên không được để trống");
        }

        if (isBlank(member.getFullName())) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (isBlank(member.getEmail())) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        String fullName = member.getFullName().trim();
        String email = member.getEmail().trim();

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email không hợp lệ: " + email);
        }

        member.setFullName(fullName);
        member.setEmail(email);

        if(member.getPhone() != null) {
            String phone = member.getPhone().trim();
            member.setPhone(phone.isEmpty() ? null : phone);
        }
    }

    private void ensureEmailValidable(String email, Long currentId) {
        Optional<Member> existing = memberDao.findByEmail(email);

        if (existing.isPresent() && !Objects.equals(existing.get().getId(), currentId)) {
            throw new IllegalArgumentException("Email " + email + " đã được sử dụng bởi hội viên khác");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        int at = email.indexOf("@");
        return at > 0 && at < email.length()-1 && email.indexOf('@', at+1) == -1;
    }
}