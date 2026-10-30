package com.gym.auth;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    private final CurrentSession currentSession;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame(CurrentSession currentSession) {
        this.currentSession = currentSession;

        // Kiểm tra user đã login chưa
        if (!currentSession.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập trước!");
            new LoginFrame(null, currentSession); // Sẽ fix sau
            this.dispose();
            return;
        }

        initUI();
    }

    private void initUI() {
        Staff user = currentSession.getCurrentUser();

        setTitle("Gym Membership System - " + user.getFullName() + " (" + user.getRole() + ")");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top: Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Center: CardLayout để chuyển giữa các panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Thêm các panel vào (M1-M5)
        // Hiện tại để trống, sau này M1-M5 làm xong sẽ thêm vào
        // cardPanel.add(memberPanel, "members");
        // cardPanel.add(trainerPanel, "trainers");
        // ...

        // Placeholder: hiển thị message để M1-M5 còn chưa làm
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.add(new JLabel("Các module M1-M5 sẽ hiển thị ở đây"));
        cardPanel.add(placeholderPanel, "placeholder");
        cardLayout.show(cardPanel, "placeholder");

        add(cardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        Staff user = currentSession.getCurrentUser();

        // Tạo menu dựa vào role
        if ("ADMIN".equals(user.getRole())) {
            addMenuItem(menuBar, "Members", () -> showPanel("members"));
            addMenuItem(menuBar, "Trainers", () -> showPanel("trainers"));
            addMenuItem(menuBar, "Plans", () -> showPanel("plans"));
            addMenuItem(menuBar, "Payments", () -> showPanel("payments"));
            addMenuItem(menuBar, "Attendance", () -> showPanel("attendance"));
        } else if ("TRAINER".equals(user.getRole())) {
            addMenuItem(menuBar, "Members", () -> showPanel("members"));
            addMenuItem(menuBar, "Attendance", () -> showPanel("attendance"));
        } else if ("RECEPTIONIST".equals(user.getRole())) {
            addMenuItem(menuBar, "Members", () -> showPanel("members"));
            addMenuItem(menuBar, "Subscriptions", () -> showPanel("subscriptions"));
            addMenuItem(menuBar, "Payments", () -> showPanel("payments"));
        }

        // Logout button
        addMenuItem(menuBar, "Logout", this::handleLogout);

        return menuBar;
    }

    private void addMenuItem(JMenuBar menuBar, String text, Runnable action) {
        JMenu menu = new JMenu(text);
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        menu.add(item);
        menuBar.add(menu);
    }

    private void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    private void handleLogout() {
        currentSession.logout();
        this.dispose();
        // Tạo LoginFrame mới
        // (sẽ fix sau để inject AuthService)
        JOptionPane.showMessageDialog(null, "Đã logout thành công");
    }
}