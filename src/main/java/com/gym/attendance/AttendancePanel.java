package com.gym.attendance;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Swing panel for member check-in/check-out and the daily attendance log. */
public class AttendancePanel extends JPanel {
    private final AttendanceService attendanceService;
    private final JTextField memberIdField = new JTextField(8);
    private final JLabel visitsLabel = new JLabel("Visits this month: -");
    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[]{"Record ID", "Member ID", "Check-in", "Check-out"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

    public AttendancePanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Member ID:")); controls.add(memberIdField);
        JButton inButton = new JButton("Check in");
        JButton outButton = new JButton("Check out");
        JButton countButton = new JButton("Visits this month");
        JButton logButton = new JButton("Today's log");
        controls.add(inButton); controls.add(outButton); controls.add(countButton); controls.add(logButton);
        controls.add(visitsLabel);
        add(controls, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        inButton.addActionListener(e -> runMemberAction(true));
        outButton.addActionListener(e -> runMemberAction(false));
        countButton.addActionListener(e -> showVisitCount());
        logButton.addActionListener(e -> loadTodayLog());
    }

    private long readMemberId() {
        long id = Long.parseLong(memberIdField.getText().trim());
        if (id <= 0) throw new IllegalArgumentException("Member ID must be positive.");
        return id;
    }

    private void runMemberAction(boolean checkIn) {
        try {
            long memberId = readMemberId();
            if (checkIn) attendanceService.checkIn(memberId);
            else attendanceService.checkOut(memberId);
            JOptionPane.showMessageDialog(this, checkIn ? "Check-in successful." : "Check-out successful.");
            loadTodayLog();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attendance error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showVisitCount() {
        try { visitsLabel.setText("Visits this month: " + attendanceService.visitsThisMonth(readMemberId())); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Attendance error", JOptionPane.ERROR_MESSAGE); }
    }

    private void loadTodayLog() {
        try {
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (AttendanceRecord r : attendanceService.todayLog()) {
                tableModel.addRow(new Object[]{r.getId(), r.getMemberId(), r.getCheckIn().format(formatter),
                    r.getCheckOut() == null ? "Not checked out" : r.getCheckOut().format(formatter)});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
