
package com.gym.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Xử lý các nghiệp vụ liên quan đến điểm danh hội viên.
 *
 * Bao gồm check-in, check-out, thống kê lượt tập
 * và truy xuất lịch sử điểm danh.
 */
@Service
public class AttendanceService {

    private final AttendanceDao attendanceDao;

    /**
     * Khởi tạo service với DAO dùng để truy xuất dữ liệu điểm danh.
     *
     * @param attendanceDao đối tượng xử lý dữ liệu điểm danh
     */
    public AttendanceService(AttendanceDao attendanceDao) {
        this.attendanceDao = Objects.requireNonNull(attendanceDao);
    }

    /**
     * Ghi nhận thời điểm hội viên bắt đầu buổi tập.
     *
     * Mỗi hội viên chỉ được có một lượt check-in chưa check-out.
     *
     * @param memberId mã hội viên
     * @return thông tin lượt điểm danh vừa tạo
     */
    @Transactional
    public AttendanceRecord checkIn(long memberId) {
        validateMemberId(memberId);

        // Kiểm tra hội viên có lượt check-in chưa kết thúc hay không.
        if (attendanceDao.findOpenByMemberId(memberId).isPresent()) {
            throw new IllegalStateException(
                "Member already has an open check-in. Check out first."
            );
        }

        // Tạo bản ghi điểm danh mới thông qua DAO.
        AttendanceRecord record = attendanceDao.checkIn(memberId);

        // Báo lỗi nếu DAO không tạo được bản ghi điểm danh.
        if (record == null) {
            throw new IllegalStateException(
                "Failed to create attendance record."
            );
        }

        return record;
    }

    /**
     * Ghi nhận thời điểm hội viên kết thúc buổi tập.
     *
     * @param memberId mã hội viên
     */
    @Transactional
    public void checkOut(long memberId) {
        validateMemberId(memberId);

        // Tìm lượt check-in đang mở của hội viên.
        AttendanceRecord openRecord = attendanceDao.findOpenByMemberId(memberId)
            .orElseThrow(() -> new IllegalStateException(
                "This member has no open check-in."
            ));

        // Cập nhật thời điểm check-out cho bản ghi hiện tại.
        attendanceDao.checkOut(openRecord.getId());
    }

    /**
     * Đếm số lượt tập của hội viên trong tháng hiện tại.
     *
     * @param memberId mã hội viên
     * @return tổng số lượt tập trong tháng
     */
    public int visitsThisMonth(long memberId) {
        validateMemberId(memberId);

        LocalDate today = LocalDate.now();

        // Lấy thời điểm bắt đầu tháng hiện tại.
        LocalDateTime from = today
            .withDayOfMonth(1)
            .atStartOfDay();

        // Lấy thời điểm bắt đầu tháng tiếp theo làm mốc kết thúc.
        LocalDateTime to = today
            .withDayOfMonth(1)
            .plusMonths(1)
            .atStartOfDay();

        // Đếm số lượt điểm danh trong khoảng thời gian đã xác định.
        return attendanceDao.countVisitsBetween(memberId, from, to);
    }

    /**
     * Lấy lịch sử điểm danh của một hội viên trong khoảng thời gian.
     *
     * @param memberId mã hội viên
     * @param from thời điểm bắt đầu
     * @param to thời điểm kết thúc
     * @return danh sách bản ghi điểm danh
     */
    public List<AttendanceRecord> findByMemberAndDateRange(
            long memberId,
            LocalDateTime from,
            LocalDateTime to) {

        validateMemberId(memberId);
        validateDateRange(from, to);

        // Truy vấn lịch sử điểm danh theo hội viên và khoảng thời gian.
        return attendanceDao.findByMemberAndDateRange(memberId, from, to);
    }

    /**
     * Lấy danh sách điểm danh trong ngày hôm nay.
     *
     * @return danh sách bản ghi điểm danh trong ngày
     */
    public List<AttendanceRecord> todayLog() {
        LocalDate today = LocalDate.now();

        // Truy vấn từ đầu ngày hôm nay đến đầu ngày mai.
        return attendanceDao.findByDateRange(
            today.atStartOfDay(),
            today.plusDays(1).atStartOfDay()
        );
    }

    /**
     * Kiểm tra mã hội viên có hợp lệ hay không.
     *
     * @param memberId mã hội viên cần kiểm tra
     */
    private void validateMemberId(long memberId) {
        if (memberId <= 0) {
            throw new IllegalArgumentException(
                "Member ID must be positive."
            );
        }
    }

    /**
     * Kiểm tra khoảng thời gian truy vấn có hợp lệ hay không.
     *
     * @param from thời điểm bắt đầu
     * @param to thời điểm kết thúc
     */
    private void validateDateRange(
            LocalDateTime from,
            LocalDateTime to) {

        // Không chấp nhận khoảng thời gian bị thiếu dữ liệu.
        if (from == null || to == null) {
            throw new IllegalArgumentException(
                "Date range must not be null."
            );
        }

        // Thời điểm bắt đầu phải trước thời điểm kết thúc.
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(
                "Start date must be before end date."
            );
        }
    }
}
