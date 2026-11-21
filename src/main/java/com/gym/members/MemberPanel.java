package com.gym.members;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MemberPanel extends JPanel {

    private static final String[] COLUMNS = {
            "ID", "Họ tên", "Email", "Điện thoại", "Ngày sinh", "Ngày tham gia", "ID PT"
    };

    private final MemberService service;

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private List<Member> currentMembers = List.of();

    private final JTextField searchField = new JTextField(20);
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField dobField = new JTextField(20);
    private final JTextField trainerIdField = new JTextField(20);

    private Long selectedId = null;

    public MemberPanel(MemberService service) {
        this.service = service;
        setLayout(new BorderLayout(8, 8));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });

        add(buildSearchBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormAndButtons(), BorderLayout.SOUTH);

        runAction(this::reload);
    }

    // ========== Giao diện ==========

    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Tìm");
        JButton resetBtn = new JButton("Hiển thị tất cả");

        searchBtn.addActionListener(e -> runAction(this::search));
        searchField.addActionListener(e -> runAction(this::search)); // nhấn Enter
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            runAction(this::reload);
        });

        panel.add(new JLabel("Tìm (tên hoặc email):"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(resetBtn);
        return panel;
    }

    private JPanel buildFormAndButtons() {
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Họ tên *"));
        form.add(nameField);
        form.add(new JLabel("Email *"));
        form.add(emailField);
        form.add(new JLabel("Điện thoại"));
        form.add(phoneField);
        form.add(new JLabel("Ngày sinh (yyyy-MM-dd)"));
        form.add(dobField);
        form.add(new JLabel("ID huấn luyện viên"));
        form.add(trainerIdField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Thêm");
        JButton editBtn = new JButton("Sửa");
        JButton deleteBtn = new JButton("Xóa");
        JButton clearBtn = new JButton("Xóa form");

        addBtn.addActionListener(e -> runAction(this::addMember));
        editBtn.addActionListener(e -> runAction(this::updateMember));
        deleteBtn.addActionListener(e -> runAction(this::deleteMember));
        clearBtn.addActionListener(e -> clearForm());

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        return south;
    }

    // ========== Thao tác ==========

    private void reload() {
        setRows(service.findAll());
    }

    private void search() {
        String keyword = searchField.getText().trim();
        setRows(keyword.isEmpty() ? service.findAll() : service.search(keyword));
    }

    private void addMember() {
        Member m = readForm();
        service.register(m);
        showMessage("Đã thêm hội viên " + m.getFullName() + " (ID " + m.getId() + ")",
                JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        reload();
    }

    private void updateMember() {
        if (selectedId == null) {
            throw new IllegalArgumentException("Hãy chọn một hội viên trong bảng để sửa");
        }
        Member m = readForm();
        m.setId(selectedId);

        // Form không có ô ngày tham gia, nên lấy lại từ DB để không ghi đè thành NULL
        Member old = service.findById(selectedId)
                .orElseThrow(() -> new IllegalArgumentException("Hội viên không còn tồn tại"));
        m.setJoinedDate(old.getJoinedDate());

        service.update(m);
        showMessage("Đã cập nhật hội viên " + m.getFullName(), JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        reload();
    }

    private void deleteMember() {
        if (selectedId == null) {
            throw new IllegalArgumentException("Hãy chọn một hội viên trong bảng để xóa");
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa hội viên ID " + selectedId + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        service.delete(selectedId);
        showMessage("Đã xóa hội viên", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        reload();
    }

    // ========== Hỗ trợ ==========

    private void setRows(List<Member> members) {
        currentMembers = members;
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getFullName(), m.getEmail(), m.getPhone(),
                    m.getDateOfBirth(), m.getJoinedDate(), m.getTrainerId()
            });
        }
        table.clearSelection();
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentMembers.size()) {
            selectedId = null;
            return;
        }
        Member m = currentMembers.get(row);
        selectedId = m.getId();
        nameField.setText(m.getFullName());
        emailField.setText(m.getEmail());
        phoneField.setText(m.getPhone() == null ? "" : m.getPhone());
        dobField.setText(m.getDateOfBirth() == null ? "" : m.getDateOfBirth().toString());
        trainerIdField.setText(m.getTrainerId() == null ? "" : m.getTrainerId().toString());
    }

    private void clearForm() {
        selectedId = null;
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dobField.setText("");
        trainerIdField.setText("");
        table.clearSelection();
    }

    private Member readForm() {
        Member m = new Member();
        m.setFullName(nameField.getText());
        m.setEmail(emailField.getText());
        m.setPhone(phoneField.getText());
        m.setDateOfBirth(parseDate(dobField.getText()));
        m.setTrainerId(parseTrainerId(trainerIdField.getText()));
        return m;
    }

    private LocalDate parseDate(String text) {
        String s = text.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày sinh phải có dạng yyyy-MM-dd, ví dụ 1995-03-14");
        }
    }

    private Long parseTrainerId(String text) {
        String s = text.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("ID huấn luyện viên phải là số");
        }
    }

    /** Bắt lỗi từ service và DAO, hiển thị cho người dùng thay vì để ứng dụng crash. */
    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage(), JOptionPane.WARNING_MESSAGE);
        } catch (DataIntegrityViolationException ex) {
            showMessage("Không thể thực hiện. Có thể email đã tồn tại, ID huấn luyện viên không hợp lệ, "
                    + "hoặc hội viên còn gói tập/lịch sử điểm danh.", JOptionPane.ERROR_MESSAGE);
        } catch (DataAccessException ex) {
            showMessage("Lỗi cơ sở dữ liệu: " + ex.getMostSpecificCause().getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, int type) {
        JOptionPane.showMessageDialog(this, message, "Quản lý hội viên", type);
    }
}