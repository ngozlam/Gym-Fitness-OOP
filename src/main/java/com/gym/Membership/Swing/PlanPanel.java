package com.gym.Membership.Swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.gym.Membership.JDBCPlan_DAO.PlanDao;
import com.gym.Membership.Model.MembershipPlan;

@Component
@Lazy //
public class PlanPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(PlanPanel.class);
    private static final List<String> STRATEGIES = Arrays.asList("STANDARD", "STUDENT_DISCOUNT", "PROMOTIONAL");
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DURATION_MONTHS = 120;
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000000");
    private static final int MAX_PRICE_SCALE = 2;
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_PRICE = 3;

    private final PlanDao planDao;


    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                            "ID",
                            "Tên gói",
                            "Thời hạn (tháng)",
                            "Giá gốc",
                            "Chiến lược giá"
                    }, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);


    public PlanPanel(PlanDao planDao) {
        this.planDao = planDao;
        setLayout(new BorderLayout(8, 8));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.getColumnModel()
                .getColumn(COL_PRICE)
                .setCellRenderer(new PriceRenderer());
        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setPreferredSize(new Dimension(750, 300));
        add(scrollPane, BorderLayout.CENTER);
        JButton addButton = new JButton("Thêm gói");
        JButton editButton = new JButton("Sửa gói");
        JButton deleteButton = new JButton("Xóa gói");
        JButton refreshButton = new JButton("Làm mới");
        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);
        addButton.addActionListener(e -> addPlan());
        editButton.addActionListener(e -> editPlan());
        deleteButton.addActionListener(e -> deletePlan());
        refreshButton.addActionListener(e -> loadPlans());
        loadPlans();
    }


    private void loadPlans() {
        try {
            List<MembershipPlan> plans = planDao.findAll();
            tableModel.setRowCount(0);
            for (MembershipPlan plan : plans) {
                tableModel.addRow(new Object[]{
                        plan.getId(),
                        plan.getName(),
                        plan.getDurationMonths(),
                        plan.getBasePrice(),
                        plan.getPricingStrategy()
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void addPlan() {
        try {
            MembershipPlan plan = showPlanDialog(null);
            if (plan == null) {
                return;
            }
            planDao.save(plan);
            loadPlans();
            info("Thêm gói tập thành công.");
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void editPlan() {
        Long id = getSelectedId();
        if (id == null) {
            info("Vui lòng chọn gói tập cần sửa.");
            return;
        }

        try {
            Optional<MembershipPlan> found = planDao.findById(id);
            if (found.isEmpty()) {
                info("Gói tập không còn tồn tại (có thể đã bị xóa ở nơi khác).");
                loadPlans();
                return;
            }

            MembershipPlan updated = showPlanDialog(found.get());
            if (updated == null) {
                return;
            }

            if (planDao.update(updated)) {
                loadPlans();
                info("Cập nhật gói tập thành công.");
            } else {
                info("Không tìm thấy gói tập cần sửa.");
                loadPlans();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void deletePlan() {
        int modelRow = getSelectedModelRow();
        if (modelRow < 0) {
            info("Vui lòng chọn gói tập cần xóa.");
            return;
        }

        Long id = idAt(modelRow);
        String name = String.valueOf(tableModel.getValueAt(modelRow, COL_NAME));

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa gói \"" + name + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (planDao.deleteById(id)) {
                loadPlans();
                info("Xóa gói tập thành công.");
            } else {
                info("Không tìm thấy gói tập cần xóa.");
                loadPlans();
            }
        } catch (Exception ex) {
            if (isConstraintViolation(ex)) {
                log.warn("Không thể xóa gói tập id={} vì đang được tham chiếu", id, ex);
                info("Không thể xóa gói \"" + name
                        + "\" vì đang có dữ liệu khác sử dụng gói này.");
            } else {
                showError(ex);
            }
        }
    }


    private int getSelectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return -1;
        }
        return table.convertRowIndexToModel(viewRow);
    }

    private Long idAt(int modelRow) {
        return ((Number) tableModel.getValueAt(modelRow, COL_ID)).longValue();
    }

    private Long getSelectedId() {
        int modelRow = getSelectedModelRow();
        return modelRow < 0 ? null : idAt(modelRow);
    }


    private MembershipPlan showPlanDialog(MembershipPlan existing) {

        String name = existing == null || existing.getName() == null
                ? "" : existing.getName();
        String duration = existing == null
                ? "1" : String.valueOf(existing.getDurationMonths());
        String price = existing == null || existing.getBasePrice() == null
                ? "1000000" : existing.getBasePrice().toPlainString();
        String strategy = existing == null
                ? STRATEGIES.get(0) : existing.getPricingStrategy();

        while (true) {
            JTextField nameField = new JTextField(name);
            JTextField durationField = new JTextField(duration);
            JTextField priceField = new JTextField(price);
            JComboBox<String> strategyBox =
                    new JComboBox<>(STRATEGIES.toArray(new String[0]));
            if (strategy != null && !STRATEGIES.contains(strategy)) {
                strategyBox.addItem(strategy);
            }
            if (strategy != null) {
                strategyBox.setSelectedItem(strategy);
            }
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Tên gói:"));
            form.add(nameField);
            form.add(new JLabel("Thời hạn (tháng):"));
            form.add(durationField);
            form.add(new JLabel("Giá gốc:"));
            form.add(priceField);
            form.add(new JLabel("Chiến lược giá:"));
            form.add(strategyBox);
            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    existing == null ? "Thêm gói tập" : "Sửa gói tập",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return null;
            }
            name = nameField.getText().trim();
            duration = durationField.getText().trim();
            price = priceField.getText().trim();
            strategy = (String) strategyBox.getSelectedItem();
            try {
                return buildPlan(
                        existing == null ? null : existing.getId(),
                        name, duration, price, strategy);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this, ex.getMessage(),
                        "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private MembershipPlan buildPlan(Long id, String name, String durationText,
                                     String priceText, String strategy) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Tên gói không được để trống.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Tên gói tối đa " + MAX_NAME_LENGTH + " ký tự.");
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Thời hạn phải là số nguyên.");
        }
        if (duration <= 0 || duration > MAX_DURATION_MONTHS) {
            throw new IllegalArgumentException(
                    "Thời hạn phải từ 1 đến " + MAX_DURATION_MONTHS + " tháng.");
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Giá phải là số hợp lệ, ví dụ 1500000 "
                            + "(không dùng dấu phân cách hàng nghìn).");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Giá không được âm.");
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException(
                    "Giá không được vượt quá " + MAX_PRICE.toPlainString() + ".");
        }
        if (price.stripTrailingZeros().scale() > MAX_PRICE_SCALE) {
            throw new IllegalArgumentException(
                    "Giá tối đa " + MAX_PRICE_SCALE + " chữ số thập phân.");
        }

        if (strategy == null || strategy.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn chiến lược giá.");
        }

        return new MembershipPlan(id, name, duration, price, strategy);
    }


    private static boolean isConstraintViolation(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException) {
                String state = ((SQLException) t).getSQLState();
                if (state != null && state.startsWith("23")) {
                    return true;
                }
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }


    private void info(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception ex) {
        log.error("Lỗi thao tác gói tập", ex);

        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        String message = ex.getMessage() != null
                ? ex.getMessage()
                : ex.getClass().getSimpleName();

        JOptionPane.showMessageDialog(
                this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }


    private static class PriceRenderer extends DefaultTableCellRenderer {

        private final NumberFormat format =
                NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

        PriceRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
            format.setMaximumFractionDigits(MAX_PRICE_SCALE);
        }

        @Override
        protected void setValue(Object value) {
            if (value instanceof Number) {
                setText(format.format(value));
            } else {
                setText(value == null ? "" : value.toString());
            }
        }
    }
}
