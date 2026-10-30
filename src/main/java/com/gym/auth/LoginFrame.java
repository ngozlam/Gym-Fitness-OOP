package com.gym.auth;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final CurrentSession currentSession;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginFrame(AuthService authService, CurrentSession currentSession) {
        this.authService = authService;
        this.currentSession = currentSession;

        initUI();
    }

    private void initUI() {
        setTitle("Gym Membership System - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        // Username field
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        // Password field
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Message label (hiển thị lỗi)
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Login button
        loginButton = new JButton("Login");
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton, gbc);

        add(panel);
        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        // Gọi AuthService để kiểm tra
        Optional<Staff> staff = authService.login(username, password);

        if (staff.isPresent()) {
            // Đăng nhập thành công
            currentSession.setCurrentUser(staff.get());
            messageLabel.setText("Đăng nhập thành công!");
            messageLabel.setForeground(Color.GREEN);

            // Chờ 1 giây rồi mở MainFrame
            Timer timer = new Timer(1000, e -> {
                LoginFrame.this.dispose();
                new MainFrame(currentSession);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Đăng nhập thất bại
            messageLabel.setText("Sai username hoặc password");
            messageLabel.setForeground(Color.RED);
            passwordField.setText("");
        }
    }
}
