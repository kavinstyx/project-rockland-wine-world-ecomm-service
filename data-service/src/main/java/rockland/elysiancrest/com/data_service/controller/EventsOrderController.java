package rockland.elysiancrest.com.data_service.controller;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.EventOrderRequest;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.dto.UserDTO;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.EventOrderService;

@Slf4j
@RestController
@RequestMapping("api/events")
@CrossOrigin

public class EventsOrderController {

    private final EventOrderService eventOrderService;

    public EventsOrderController(EventOrderService eventOrderService) {
        this.eventOrderService = eventOrderService;

    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<Response<Long>> create(@RequestBody EventOrderRequest request) {

        try {
            Long orderId = eventOrderService.processAndSaveOrder(request);
            return ResponseEntity.ok(Response.<Long>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Event order created successfully")
                    .data(orderId)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message("Failed to create event order: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
