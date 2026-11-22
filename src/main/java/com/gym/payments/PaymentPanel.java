
package com.gym.payments;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * PaymentPanel là giao diện quản lý thanh toán.
 *
 * Các chức năng:
 * - Nhập Subscription ID.
 * - Nhập số tiền thanh toán.
 * - Chọn phương thức thanh toán.
 * - Ghi nhận giao dịch.
 * - Xem lịch sử thanh toán.
 * - Hiển thị số tiền còn nợ.
 */
public class PaymentPanel extends JPanel {

    // Service xử lý nghiệp vụ thanh toán.
    private final PaymentService paymentService;

    // Ô nhập mã Subscription.
    private final JTextField subscriptionIdField = new JTextField(8);

    // Ô nhập số tiền thanh toán.
    private final JTextField amountField = new JTextField(10);

    // Danh sách phương thức thanh toán.
    private final JComboBox<String> methodBox =
            new JComboBox<>(new String[]{
                "CASH", "CARD", "TRANSFER"
            });

    // Label hiển thị số tiền còn nợ.
    private final JLabel balanceLabel =
            new JLabel("Outstanding: -");

    // Model quản lý dữ liệu hiển thị trên bảng thanh toán.
    // Không cho phép người dùng chỉnh sửa trực tiếp dữ liệu.
    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                new Object[]{
                    "ID", "Subscription",
                    "Amount", "Date", "Method"
                }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Constructor khởi tạo giao diện PaymentPanel.
     *
     * @param paymentService service xử lý nghiệp vụ thanh toán
     */
    public PaymentPanel(PaymentService paymentService) {

        this.paymentService = paymentService;

        // Thiết lập bố cục chính của giao diện.
        setLayout(new BorderLayout(8, 8));

        // Tạo khu vực nhập thông tin thanh toán.
        JPanel form = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        // Thêm các thành phần nhập liệu.
        form.add(new JLabel("Subscription ID:"));
        form.add(subscriptionIdField);

        form.add(new JLabel("Amount:"));
        form.add(amountField);

        form.add(new JLabel("Method:"));
        form.add(methodBox);

        // Nút ghi nhận thanh toán.
        JButton recordButton =
                new JButton("Record payment");

        // Nút xem lịch sử và số dư còn nợ.
        JButton refreshButton =
                new JButton("View history / balance");

        form.add(recordButton);
        form.add(refreshButton);
        form.add(balanceLabel);

        // Đưa khu vực nhập liệu lên phía trên.
        add(form, BorderLayout.NORTH);

        // Tạo bảng hiển thị lịch sử thanh toán.
        JTable paymentTable = new JTable(tableModel);

        // Đưa bảng vào JScrollPane để hỗ trợ cuộn.
        add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        // Xử lý sự kiện khi nhấn nút Record Payment.
        recordButton.addActionListener(e -> recordPayment());

        // Xử lý sự kiện khi nhấn nút View History / Balance.
        refreshButton.addActionListener(e -> refreshHistory());
    }

    /**
     * Đọc và kiểm tra Subscription ID từ ô nhập liệu.
     *
     * @return Subscription ID hợp lệ
     */
    private long readSubscriptionId() {

        String text = subscriptionIdField.getText().trim();

        // Không cho phép bỏ trống Subscription ID.
        if (text.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please enter Subscription ID."
            );
        }

        try {
            long id = Long.parseLong(text);

            // ID phải lớn hơn 0.
            if (id <= 0) {
                throw new IllegalArgumentException(
                        "Subscription ID must be positive."
                );
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Subscription ID must be a valid number."
            );
        }
    }

    /**
     * Xử lý sự kiện ghi nhận thanh toán.
     *
     * Các bước:
     * 1. Đọc Subscription ID.
     * 2. Đọc số tiền thanh toán.
     * 3. Lấy phương thức thanh toán.
     * 4. Gọi PaymentService để kiểm tra và lưu giao dịch.
     * 5. Cập nhật lại lịch sử thanh toán và số dư.
     */
    private void recordPayment() {

        try {

            // Đọc Subscription ID.
            long subscriptionId = readSubscriptionId();

            // Đọc số tiền từ ô nhập.
            String amountText = amountField.getText().trim();

            if (amountText.isEmpty()) {
                throw new IllegalArgumentException(
                        "Please enter payment amount."
                );
            }

            // Chuyển chuỗi nhập vào thành BigDecimal.
            BigDecimal amount;

            try {
                amount = new BigDecimal(amountText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Payment amount must be a valid number."
                );
            }

            // Lấy phương thức thanh toán được chọn.
            String method =
                    (String) methodBox.getSelectedItem();

            // Gọi Service để thực hiện nghiệp vụ thanh toán.
            paymentService.recordPayment(
                    subscriptionId,
                    amount,
                    method
            );

            // Xóa số tiền vừa nhập sau khi thanh toán thành công.
            amountField.setText("");

            // Cập nhật lịch sử và số tiền còn nợ.
            refreshHistory();

            // Thông báo kết quả.
            JOptionPane.showMessageDialog(
                    this,
                    "Payment recorded successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            // Hiển thị thông báo khi xảy ra lỗi.
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Payment error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Tải lịch sử thanh toán theo Subscription ID.
     *
     * Đồng thời cập nhật số tiền còn nợ trên giao diện.
     */
    private void refreshHistory() {

        try {

            // Lấy Subscription ID.
            long id = readSubscriptionId();

            // Xóa dữ liệu cũ trên bảng trước khi tải dữ liệu mới.
            tableModel.setRowCount(0);

            // Định dạng ngày giờ hiển thị.
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm:ss"
                    );

            // Lấy danh sách giao dịch từ PaymentService.
            for (Payment payment :
                    paymentService.getPaymentsBySubscription(id)) {

                // Thêm từng giao dịch vào bảng.
                tableModel.addRow(new Object[]{
                    payment.getId(),
                    payment.getSubscriptionId(),
                    payment.getAmount(),
                    payment.getPaymentDate().format(formatter),
                    payment.getMethod()
                });
            }

            // Lấy số tiền còn nợ và cập nhật Label.
            BigDecimal balance =
                    paymentService.getOutstandingBalance(id);

            balanceLabel.setText(
                    "Outstanding: " + balance
            );

        } catch (Exception ex) {

            // Hiển thị lỗi khi không thể tải lịch sử.
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Load error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
