package com.example.EmailScheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EmailSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailSchedulerApplication.class, args);
    }

    // Define the bean for JavaMailSender
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("kisimi794@gmail.com");
        mailSender.setPassword("ihbswkfdfrdgqbil"); // Replace with your Gmail password
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @RestController
    public static class EmailController {

        private final JavaMailSender javaMailSender;

        @Autowired
        public EmailController(JavaMailSender javaMailSender) {
            this.javaMailSender = javaMailSender;
        }

        @GetMapping("/send-email")
        public String sendTestEmail() {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("boazomare04@gmail.com");
            mailMessage.setSubject("Test Email");
            mailMessage.setText("This is a test email sent via API using Gmail SMTP server.");

            javaMailSender.send(mailMessage);

            return "Test email sent successfully!";
        }
    }

    @Component
    public static class SubscriptionReminder {

        @Autowired
        private JavaMailSender javaMailSender;

        // Schedule reminders at different times before subscription expiry
        @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
        public void sendReminder6Months() {
            sendReminderEmail(6);
        }

        @Scheduled(cron = "0 0 0 1 */3 *") // Every 3 months on the 1st day of the month
        public void sendReminder3Months() {
            sendReminderEmail(3);
        }

        @Scheduled(cron = "0 0 0 1 * *") // Every month on the 1st day of the month
        public void sendReminder1Month() {
            sendReminderEmail(1);
        }

        @Scheduled(cron = "0 0 0 * * MON") // Every Monday
        public void sendReminder1Week() {
            sendReminderEmail(0.25);
        }

        @Scheduled(cron = "0 0/5 * * * *") // Every 5 minutes (for testing)
        public void sendReminder5Minutes() {
            sendReminderEmail(0.00417);
        }

        // Helper method to send reminder email
        private void sendReminderEmail(double monthsBeforeExpiry) {
            LocalDate today = LocalDate.now();
            LocalDate subscriptionExpiryDate = today.plusMonths((int) monthsBeforeExpiry);
            LocalDateTime reminderDateTime = subscriptionExpiryDate.atStartOfDay();

            // List of email addresses
            List<String> emails = new ArrayList<>();
            emails.add("boazomare04@gmail.com");
            emails.add("omareboaz1@gmail.com");
            // Add more email addresses as needed

            // Iterate over each email address and send a reminder email
            for (String email : emails) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email); // Set recipient's email address
                mailMessage.setSubject("Subscription Expiry Reminder");
                mailMessage.setText("Your subscription will expire in " + (int) monthsBeforeExpiry + " months. Please renew your subscription.");

                javaMailSender.send(mailMessage);
                System.out.println("Reminder email sent to " + email + " for " + (int) monthsBeforeExpiry + " months before expiry.");
            }
        }
    }
}
