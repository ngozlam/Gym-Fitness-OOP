package com.gym;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.gym.auth.AuthService;
import com.gym.auth.CurrentSession;
import com.gym.auth.LoginFrame;

@SpringBootApplication
public class GymApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
            new SpringApplicationBuilder(GymApplication.class)
                .web(WebApplicationType.NONE)   
                .headless(false)
                .run(args);
        AuthService authService = ctx.getBean(AuthService.class);
        CurrentSession currentSession = ctx.getBean(CurrentSession.class);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gym Membership System");
            new LoginFrame(authService, currentSession);
           
            frame.pack();
        });
    }
}