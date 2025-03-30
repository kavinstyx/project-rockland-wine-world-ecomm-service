package rockland.elysiancrest.com.data_service.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.dto.SubscribeEmailDTO;
import rockland.elysiancrest.com.data_service.entity.SubscribeEmail;
import rockland.elysiancrest.com.data_service.repo.SubscriberEmailRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;

@Slf4j
@RestController
@RequestMapping("api/subscribe")
@CrossOrigin
public class SubscriberEmailController {

    private final SubscriberEmailRepo subscriberEmailRepo;
    private final EmailService emailService;

    public SubscriberEmailController(SubscriberEmailRepo subscriberEmailRepo, EmailService emailService) {
        this.subscriberEmailRepo = subscriberEmailRepo;
        this.emailService = emailService;
    }

    @PostMapping("")
    public ResponseEntity<Response<Void>> subscribeEmail(@RequestBody SubscribeEmailDTO subscribeEmailDTO){
        try {
            //save email in database
            SubscribeEmail emailEntity = new SubscribeEmail();
            emailEntity.setEmail(subscribeEmailDTO.getEmail());
            subscriberEmailRepo.save(emailEntity);

            Context context = new Context();
            context.setVariable("email", emailEntity.getEmail());
                //send email

            emailService.contactUsEmail(
                    emailEntity.getEmail(),
                    "Thank You for Subscribing",
                    "subscriber_email.html",
                    context
            );

            //return success
            return ResponseEntity.ok(
                    Response.<Void>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("Subscription email sent successfully")
                            .data(null)
                            .build()
            );
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Response.<Void>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .status(Status.ERROR)
                                    .message("Failed to send subscription email: " + e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Response<Page<SubscribeEmail>>> searchSubscribers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SubscribeEmail> subscribers;

            if (email != null && !email.trim().isEmpty()) {
                subscribers = subscriberEmailRepo.findByEmailContainingIgnoreCase(email, pageable);
            } else {
                subscribers = subscriberEmailRepo.findAll(pageable);
            }

            return ResponseEntity.ok(
                    Response.<Page<SubscribeEmail>>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("Subscribers retrieved successfully")
                            .data(subscribers)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Response.<Page<SubscribeEmail>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .status(Status.ERROR)
                                    .message("Failed to retrieve subscribers: " + e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }
}
