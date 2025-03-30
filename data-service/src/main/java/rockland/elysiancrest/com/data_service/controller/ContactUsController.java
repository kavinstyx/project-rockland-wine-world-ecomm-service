package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.entity.ContactUs;
import rockland.elysiancrest.com.data_service.service.ContactUsService;

@Slf4j
@RestController
@RequestMapping("api/contactus")
@CrossOrigin
public class ContactUsController {

    private final ContactUsService contactUsService;

    public ContactUsController(ContactUsService contactUsService) {
        this.contactUsService = contactUsService;
    }

    @PostMapping
    public ResponseEntity<ContactUs> saveContactUs(@RequestBody ContactUs contactUs) {
        ContactUs savedContactUs = contactUsService.saveContact(contactUs);

        return ResponseEntity.ok(Response.<ContactUs>builder().code(HttpStatus.OK.value())
                .status(Status.SUCCESS).data(savedContactUs).build().getData());
    }

}
