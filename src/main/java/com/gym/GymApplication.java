package com.gym;

import javax.swing.SwingUtilities;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.gym.attendance.AttendanceService;
import com.gym.auth.AuthService;
import com.gym.auth.CurrentSession;
import com.gym.auth.LoginFrame;
import com.gym.members.MemberDao;
import com.gym.members.MemberService;
import com.gym.membership.PlanDao;
import com.gym.membership.SubscriptionDao;
import com.gym.membership.SubscriptionService;
import com.gym.payments.PaymentService;
import com.gym.trainers.TrainerService;
import com.gym.trainers.TrainerService;
import com.gym.members.MemberDao;
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
        MemberService memberService = ctx.getBean(MemberService.class);
        SubscriptionService subscriptionService = ctx.getBean(SubscriptionService.class);
        PlanDao planDao = ctx.getBean(PlanDao.class);
        SubscriptionDao subscriptionDao = ctx.getBean(SubscriptionDao.class);
        PaymentService paymentService = ctx.getBean(PaymentService.class);
        AttendanceService attendanceService = ctx.getBean(AttendanceService.class);
        TrainerService trainerService = ctx.getBean(TrainerService.class);
        MemberDao memberDao = ctx.getBean(MemberDao.class);
        SwingUtilities.invokeLater(() -> {
            //JFrame frame = new JFrame("Gym Membership System");
            new LoginFrame(authService, currentSession, memberService, subscriptionService, planDao, subscriptionDao, paymentService,attendanceService,trainerService, memberDao);
           
            //frame.pack();
        });
    }
}