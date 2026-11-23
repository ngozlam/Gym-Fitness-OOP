
package com.gym.attendance;

import java.time.LocalDateTime;

/**
 * Đại diện cho một bản ghi điểm danh của hội viên.
 *
 * Mỗi bản ghi lưu thời điểm check-in và check-out.
 * Nếu checkOut là null, nghĩa là hội viên chưa kết thúc buổi tập.
 */
public class AttendanceRecord {

    // Mã định danh duy nhất của bản ghi điểm danh.
    private Long id;

    // Mã hội viên thực hiện điểm danh.
    private long memberId;

    // Thời điểm hội viên bắt đầu buổi tập.
    private LocalDateTime checkIn;

    // Thời điểm hội viên kết thúc buổi tập.
    // Giá trị null thể hiện lượt điểm danh vẫn đang mở.
    private LocalDateTime checkOut;

    /**
     * Constructor mặc định.
     */
    public AttendanceRecord() {
    }

    /**
     * Khởi tạo bản ghi điểm danh với đầy đủ thông tin.
     *
     * @param id mã bản ghi điểm danh
     * @param memberId mã hội viên
     * @param checkIn thời điểm check-in
     * @param checkOut thời điểm check-out
     */
    public AttendanceRecord(
            Long id,
            long memberId,
            LocalDateTime checkIn,
            LocalDateTime checkOut) {

        this.id = id;
        this.memberId = memberId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    /**
     * Lấy mã bản ghi điểm danh.
     *
     * @return mã bản ghi
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã bản ghi điểm danh.
     *
     * @param id mã bản ghi mới
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã hội viên.
     *
     * @return mã hội viên
     */
    public long getMemberId() {
        return memberId;
    }

    /**
     * Cập nhật mã hội viên.
     *
     * @param memberId mã hội viên mới
     */
    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    /**
     * Lấy thời điểm check-in.
     *
     * @return thời điểm bắt đầu buổi tập
     */
    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    /**
     * Cập nhật thời điểm check-in.
     *
     * @param checkIn thời điểm check-in mới
     */
    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    /**
     * Lấy thời điểm check-out.
     *
     * @return thời điểm kết thúc buổi tập, hoặc null nếu chưa check-out
     */
    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    /**
     * Cập nhật thời điểm check-out.
     *
     * @param checkOut thời điểm check-out mới
     */
    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }
}

