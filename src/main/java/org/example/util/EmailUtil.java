package org.example.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    // Your Gmail credentials (USE ENVIRONMENT VARIABLES OR CONFIG FILE!)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USERNAME = System.getenv("GMAIL_USERNAME");
    private static final String SMTP_PASSWORD = System.getenv("GMAIL_APP_PASSWORD");

    public static boolean sendEmail(String toEmail, String subject, String messageBody) {
        // Check if credentials are set
        if (SMTP_USERNAME == null || SMTP_PASSWORD == null) {
            System.err.println("ERROR: Gmail credentials not set. Use environment variables:");
            System.err.println("  GMAIL_USERNAME=your.email@gmail.com");
            System.err.println("  GMAIL_APP_PASSWORD=your-16char-app-password");
            return false;
        }

        try {
            // Setup SMTP properties
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.ssl.trust", SMTP_HOST);

            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);

            // Set email content (HTML format)
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #3498db; color: white; padding: 20px; text-align: center; }
                        .content { padding: 30px; background-color: #f9f9f9; }
                        .footer { margin-top: 20px; padding: 20px; text-align: center; color: #7f8c8d; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Habit System Verification</h1>
                        </div>
                        <div class="content">
                            %s
                        </div>
                        <div class="footer">
                            <p>This email was sent from Habit System Application</p>
                            <p>Your email gave been verified.</p>
                            <p>© 2025 Habit System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(messageBody.replace("\n", "<br>"));

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Send email
            Transport.send(message);

            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.err.println("❌ Authentication failed. Check your Gmail credentials.");
            System.err.println("Make sure:");
            System.err.println("1. You enabled 2-Factor Authentication");
            System.err.println("2. You generated an App Password (16 characters)");
            System.err.println("3. You're using the App Password, not your regular password");
            return false;
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}