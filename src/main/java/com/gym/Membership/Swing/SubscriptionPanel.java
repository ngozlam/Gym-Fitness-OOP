package com.gym.Membership.Swing;



import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import com.gym.Membership.JDBCPlan_DAO.PlanDao;
import com.gym.Membership.Model.MembershipPlan;
import com.gym.Membership.Model.Subscription;
import com.gym.Membership.Service.SubscriptionService;
import com.gym.Membership.Subscription_DAO.SubscriptionDao;

@Component
public class SubscriptionPanel extends JPanel {

    private final SubscriptionService subscriptionService;
    private final SubscriptionDao subscriptionDao;
    private final PlanDao planDao;

    private final JTextField memberIdField =
            new JTextField(15);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                            "ID",
                            "Member ID",
                            "Plan ID",
                            "Ngày bắt đầu",
                            "Ngày kết thúc",
                            "Trạng thái",
                            "Giá cuối"
                    }, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);

    public SubscriptionPanel(
            SubscriptionService subscriptionService,
            SubscriptionDao subscriptionDao,
            PlanDao planDao) {

        this.subscriptionService = subscriptionService;
        this.subscriptionDao = subscriptionDao;
        this.planDao = planDao;

        setLayout(new BorderLayout(8, 8));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));

        JLabel memberLabel = new JLabel("ID thành viên:");

        JButton enrollButton = new JButton("Đăng ký");
        JButton refreshButton = new JButton("Làm mới");
        JButton renewButton = new JButton("Gia hạn");
        JButton cancelButton = new JButton("Hủy gói");

        topPanel.add(memberLabel);
        topPanel.add(memberIdField);
        topPanel.add(enrollButton);
        topPanel.add(refreshButton);
        topPanel.add(renewButton);
        topPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);


        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setRowHeight(34);

        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setPreferredSize(new Dimension(1100, 550));

        add(scrollPane, BorderLayout.CENTER);

        enrollButton.addActionListener(e -> enroll());

        refreshButton.addActionListener(e -> loadSubscriptions());

        renewButton.addActionListener(e -> renew());

        cancelButton.addActionListener(e -> cancel());

        loadSubscriptions();
    }


    private void loadSubscriptions() {
        try {
            tableModel.setRowCount(0);
            DateTimeFormatter formatter =
                    DateTimeFormatter.ISO_LOCAL_DATE;
            for (Subscription s : subscriptionDao.findAll()) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getMemberId(),
                        s.getPlanId(),
                        s.getStartDate().format(formatter),
                        s.getEndDate().format(formatter),
                        s.getStatus(),
                        s.getFinalPrice()
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void enroll() {
        try {
            String memberText = memberIdField.getText().trim();
            if (memberText.isEmpty()) {
                throw new IllegalArgumentException(
                        "Vui lòng nhập ID thành viên."
                );
            }

            Long memberId = Long.parseLong(memberText);
            if (memberId <= 0) {
                throw new IllegalArgumentException(
                        "ID thành viên phải lớn hơn 0."
                );
            }


            List<MembershipPlan> plans =planDao.findAll();
            if (plans.isEmpty()) {
                showMessage( "Chưa có gói tập. Hãy thêm gói trước.");
                return;
            }


            JComboBox<String> planBox = new JComboBox<>();
            for (MembershipPlan plan : plans) {
                planBox.addItem(
                        plan.getName()+ " ("+ plan.getDurationMonths()+ " tháng)");
            }

            JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
            panel.add(new JLabel("Chọn gói tập:"));
            panel.add(planBox);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Đăng ký gói tập",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            int selectedIndex = planBox.getSelectedIndex();

            MembershipPlan selectedPlan = plans.get(selectedIndex);

            Subscription subscription =subscriptionService.enroll(memberId,selectedPlan.getId());

            loadSubscriptions();


            JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký thành công!\n\n"
                            + "Subscription ID: "
                            + subscription.getId()
                            + "\nGiá cuối: "
                            + subscription.getFinalPrice()
                            + "\nNgày hết hạn: "
                            + subscription.getEndDate(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            showMessage("ID thành viên phải là số nguyên hợp lệ.");
        } catch (Exception ex) {
                showError(ex);
        }
    }


    private void renew() {
        Subscription selected = getSelectedSubscription();
        if (selected == null) {
            showMessage("Vui lòng chọn subscription cần gia hạn.");
            return;
        }


        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn gia hạn subscription "
                        + selected.getId() + "?",
                "Gia hạn gói tập",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Subscription updated =subscriptionService.renew(selected.getId());
            loadSubscriptions();
            JOptionPane.showMessageDialog(
                    this,
                    "Gia hạn thành công!\n\n"
                            + "Subscription ID: "
                            + updated.getId()
                            + "\nNgày hết hạn mới: "
                            + updated.getEndDate(),
                    "Thành công",JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void cancel() {
        Subscription selected =
                getSelectedSubscription();
        if (selected == null) {
            showMessage("Vui lòng chọn subscription cần hủy.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,"Bạn có chắc muốn hủy subscription " + selected.getId() + "?\n\n" + 
                "Gói tập sẽ không còn hiệu lực.",
                "Hủy gói tập",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            subscriptionService.cancel(selected.getId());
            loadSubscriptions();
            JOptionPane.showMessageDialog(this,"Đã hủy subscription thành công!","Thông báo",JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError(ex);
        }
    }


    private Subscription getSelectedSubscription() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        Long id = ((Number) tableModel.getValueAt(row, 0)).longValue();
        return subscriptionDao.findById(id).orElse(null);
    }


    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this,message,"Thông báo",JOptionPane.INFORMATION_MESSAGE);
     }


    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

