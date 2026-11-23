
package com.gym.attendance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Định nghĩa các thao tác truy xuất và cập nhật dữ liệu điểm danh.
 *
 * Interface này quy định các phương thức mà lớp DAO
 * cần triển khai để phục vụ nghiệp vụ Attendance.
 */
public interface AttendanceDao {

    /**
     * Tạo bản ghi điểm danh khi hội viên bắt đầu buổi tập.
     *
     * @param memberId mã hội viên
     * @return bản ghi điểm danh vừa được tạo
     */
    AttendanceRecord checkIn(long memberId);

    /**
     * Cập nhật bản ghi điểm danh khi hội viên kết thúc buổi tập.
     *
     * @param recordId mã bản ghi điểm danh cần cập nhật
     */
    void checkOut(long recordId);

    /**
     * Tìm lượt check-in chưa có thời điểm check-out của hội viên.
     *
     * @param memberId mã hội viên
     * @return Optional chứa bản ghi nếu tìm thấy, ngược lại trả về Optional rỗng
     */
    Optional<AttendanceRecord> findOpenByMemberId(long memberId);

    /**
     * Lấy lịch sử điểm danh của một hội viên trong khoảng thời gian.
     *
     * @param memberId mã hội viên
     * @param from thời điểm bắt đầu
     * @param to thời điểm kết thúc
     * @return danh sách bản ghi điểm danh phù hợp
     */
    List<AttendanceRecord> findByMemberAndDateRange(
        long memberId,
        LocalDateTime from,
        LocalDateTime to
    );

    /**
     * Lấy tất cả bản ghi điểm danh trong khoảng thời gian.
     *
     * @param from thời điểm bắt đầu
     * @param to thời điểm kết thúc
     * @return danh sách bản ghi điểm danh
     */
    List<AttendanceRecord> findByDateRange(
        LocalDateTime from,
        LocalDateTime to
    );

    /**
     * Đếm số lượt điểm danh của hội viên trong khoảng thời gian.
     *
     * @param memberId mã hội viên
     * @param from thời điểm bắt đầu
     * @param to thời điểm kết thúc
     * @return tổng số lượt điểm danh
     */
    int countVisitsBetween(
        long memberId,
        LocalDateTime from,
        LocalDateTime to
    );
}
