package rockland.elysiancrest.com.data_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String fromAddress;

    @Value("${mail.cc}")
    private String ccAddress;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOrderConfirmation(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


//    public void sendPasswordResetEmail(String email, String subject, String s) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject(subject);
//        message.setText(s);
//        mailSender.send(message);
//    }


    public void orderConfirmation(String to, String subject, String templateName, Context context) throws MessagingException{
        //generate html content from the template
        String htmlContent = templateEngine.process(templateName, context);

        //create a message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        //set email details
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setBcc(ccAddress);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        //send email
        mailSender.send(mimeMessage);
    }
    public void passwordResetEmail(String email, String subject, String templateName, Context context) throws MessagingException{

        //generate html content from the template
        String htmlContent = templateEngine.process(templateName, context);

        //create a message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        //set email details
        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        //send email
        mailSender.send(mimeMessage);
    }

    public void contactUsEmail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }


    public void registerEmail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }

    public void registrationOtpEmail(String email, String subject, String s) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(s);
        mailSender.send(message);
    }

    public void eventOrderEmail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }

    public void OTPEmail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }

    public void sendComplaintMail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }

    public void sendQuotationEmail(String email, String subject, String templateName, Context context) throws MessagingException {
        // Generate HTML content from the template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and configure the MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Send the email
        mailSender.send(mimeMessage);
    }

}
