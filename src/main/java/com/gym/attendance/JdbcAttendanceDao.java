
package com.gym.attendance;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Triển khai các thao tác truy xuất dữ liệu điểm danh bằng JDBC.
 *
 * Lớp này chịu trách nhiệm thực hiện các câu lệnh SQL
 * để tạo, cập nhật và truy vấn bản ghi điểm danh.
 */
@Repository
public class JdbcAttendanceDao implements AttendanceDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Ánh xạ dữ liệu từ một dòng trong bảng attendance
     * thành đối tượng AttendanceRecord.
     *
     * Nếu check_out có giá trị NULL thì hội viên vẫn đang tập.
     */
    private final RowMapper<AttendanceRecord> mapper = (rs, rowNum) -> {
        Timestamp out = rs.getTimestamp("check_out");

        return new AttendanceRecord(
            rs.getLong("id"),
            rs.getLong("member_id"),
            rs.getTimestamp("check_in").toLocalDateTime(),
            out == null ? null : out.toLocalDateTime()
        );
    };

    /**
     * Khởi tạo DAO với JdbcTemplate để thực hiện truy vấn SQL.
     *
     * @param jdbcTemplate công cụ hỗ trợ truy cập cơ sở dữ liệu
     */
    public JdbcAttendanceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tạo bản ghi điểm danh mới khi hội viên check-in.
     *
     * @param memberId mã hội viên
     * @return bản ghi điểm danh vừa được tạo
     */
    @Override
    public AttendanceRecord checkIn(long memberId) {
        LocalDateTime now = LocalDateTime.now();

        // Chuẩn bị đối tượng để nhận ID tự tăng sau khi INSERT.
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Thêm bản ghi mới, check_out ban đầu để NULL.
        int inserted = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO attendance (member_id, check_in, check_out) VALUES (?, ?, NULL)",
                new String[]{"id"}
            );

            ps.setLong(1, memberId);
            ps.setTimestamp(2, Timestamp.valueOf(now));

            return ps;
        }, keyHolder);

        // Kiểm tra thao tác INSERT có thành công hay không.
        if (inserted == 0 || keyHolder.getKey() == null) {
            throw new IllegalStateException(
                "Failed to create attendance record."
            );
        }

        long id = keyHolder.getKey().longValue();

        // Truy vấn lại bản ghi vừa tạo để trả về đầy đủ dữ liệu.
        return jdbcTemplate.queryForObject(
            "SELECT id, member_id, check_in, check_out FROM attendance WHERE id = ?",
            mapper,
            id
        );
    }

    /**
     * Cập nhật thời điểm check-out cho một lượt điểm danh.
     *
     * Chỉ cập nhật bản ghi chưa có thời điểm check-out.
     *
     * @param recordId mã bản ghi điểm danh
     */
    @Override
    public void checkOut(long recordId) {
        int updated = jdbcTemplate.update(
            "UPDATE attendance SET check_out = ? WHERE id = ? AND check_out IS NULL",
            Timestamp.valueOf(LocalDateTime.now()),
            recordId
        );

        // Báo lỗi nếu không tìm thấy bản ghi đang mở để cập nhật.
        if (updated == 0) {
            throw new IllegalArgumentException(
                "Open attendance record not found."
            );
        }
    }

    /**
     * Tìm lượt check-in chưa kết thúc của một hội viên.
     *
     * @param memberId mã hội viên
     * @return Optional chứa bản ghi nếu tìm thấy
     */
    @Override
    public Optional<AttendanceRecord> findOpenByMemberId(long memberId) {
        List<AttendanceRecord> rows = jdbcTemplate.query(
            "SELECT id, member_id, check_in, check_out "
            + "FROM attendance "
            + "WHERE member_id = ? AND check_out IS NULL "
            + "ORDER BY check_in DESC LIMIT 1",
            mapper,
            memberId
        );

        // Trả về bản ghi đầu tiên hoặc Optional rỗng nếu không có.
        return rows.stream().findFirst();
    }

    /**
     * Lấy lịch sử điểm danh của một hội viên trong khoảng thời gian.
     *
     * @param memberId mã hội viên
     * @param from thời điểm bắt đầu (bao gồm)
     * @param to thời điểm kết thúc (không bao gồm)
     * @return danh sách bản ghi điểm danh
     */
    @Override
    public List<AttendanceRecord> findByMemberAndDateRange(
            long memberId,
            LocalDateTime from,
            LocalDateTime to) {

        return jdbcTemplate.query(
            "SELECT id, member_id, check_in, check_out "
            + "FROM attendance "
            + "WHERE member_id = ? AND check_in >= ? AND check_in < ? "
            + "ORDER BY check_in DESC",
            mapper,
            memberId,
            Timestamp.valueOf(from),
            Timestamp.valueOf(to)
        );
    }

    /**
     * Lấy tất cả bản ghi điểm danh trong khoảng thời gian.
     *
     * @param from thời điểm bắt đầu (bao gồm)
     * @param to thời điểm kết thúc (không bao gồm)
     * @return danh sách bản ghi điểm danh
     */
    @Override
    public List<AttendanceRecord> findByDateRange(
            LocalDateTime from,
            LocalDateTime to) {

        return jdbcTemplate.query(
            "SELECT id, member_id, check_in, check_out "
            + "FROM attendance "
            + "WHERE check_in >= ? AND check_in < ? "
            + "ORDER BY check_in DESC",
            mapper,
            Timestamp.valueOf(from),
            Timestamp.valueOf(to)
        );
    }

    /**
     * Đếm tổng số lượt điểm danh của hội viên trong khoảng thời gian.
     *
     * @param memberId mã hội viên
     * @param from thời điểm bắt đầu (bao gồm)
     * @param to thời điểm kết thúc (không bao gồm)
     * @return tổng số lượt điểm danh
     */
    @Override
    public int countVisitsBetween(
            long memberId,
            LocalDateTime from,
            LocalDateTime to) {

        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM attendance "
            + "WHERE member_id = ? AND check_in >= ? AND check_in < ?",
            Integer.class,
            memberId,
            Timestamp.valueOf(from),
            Timestamp.valueOf(to)
        );

        // Đảm bảo kết quả trả về luôn là một số nguyên hợp lệ.
        return count == null ? 0 : count;
    }
}
