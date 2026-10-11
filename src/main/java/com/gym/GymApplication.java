package com.gym;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GymApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
            new SpringApplicationBuilder(GymApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gym Membership System");
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ctx.close();
                    System.exit(0);
                }
            });
            frame.setVisible(true);
        });
    }
}