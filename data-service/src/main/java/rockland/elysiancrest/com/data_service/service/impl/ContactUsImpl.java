package rockland.elysiancrest.com.data_service.service.impl;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.entity.ContactUs;
import rockland.elysiancrest.com.data_service.repo.ContactUsRepo;
import rockland.elysiancrest.com.data_service.service.ContactUsService;
import rockland.elysiancrest.com.data_service.service.EmailService;

@Service
public class ContactUsImpl implements ContactUsService {

    private final ContactUsRepo contactUsRepo;

    @Autowired
    private EmailService emailService;

    @Value("${general.admin.contactus}")
    private String adminEmail;

    public ContactUsImpl(ContactUsRepo contactUsRepo) {
        this.contactUsRepo = contactUsRepo;
    }
    @Override
    public ContactUs saveContact(ContactUs contact) {
        //save in database
        ContactUs savedContactUs =  contactUsRepo.save(contact);

        Context customerContext = new Context();
        customerContext.setVariable("recipientName", contact.getName());
//        customerContext.setVariable("senderName", contact.getName());
        customerContext.setVariable("senderEmail", contact.getEmail());
        customerContext.setVariable("senderPhone", contact.getPhone());
        customerContext.setVariable("messageContent", contact.getMessage());
        customerContext.setVariable("isAdmin", false);


        // Send confirmation email to the customer
        try {
            emailService.contactUsEmail(
                    contact.getEmail(),
                    "Thank You for Contacting Us",
                    "contact_us_email.html",
                    customerContext
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send customer contact email", e);
        }

        //admin notification email
        Context adminContext = new Context();

        adminContext.setVariable("senderName", contact.getName());
        adminContext.setVariable("senderEmail", contact.getEmail());
        adminContext.setVariable("senderPhone", contact.getPhone());
        adminContext.setVariable("messageContent", contact.getMessage());
        adminContext.setVariable("isAdmin", true);

        // Send notification email to the admin
        try {
            emailService.contactUsEmail(
                    adminEmail,
                    "New Contact Us Submission",
                    "contact_us_email.html",
                    adminContext
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send admin contact email", e);
        }

        return savedContactUs;
    }
}
