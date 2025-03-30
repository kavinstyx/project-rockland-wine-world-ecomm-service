package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.PaymentRequestDto;
import com.commonlibrary.contract.v1.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.PaymentData;
import rockland.elysiancrest.com.data_service.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequestDto paymentRequestDto){
        try {
            PaymentResult result = paymentService.initiatePayment(paymentRequestDto);

            // Creating a response that includes both the transaction information and the payment URL
            Map<String, Object> response = new HashMap<>();
            response.put("transaction", result.getTransactionDto());
            response.put("paymentUrl", result.getPaymentUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to initiate payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate payment: " + e.getMessage());
        }
    }


    @PostMapping(value = "/notify", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> captureData(
            @RequestParam(required = false , value = "merchant_id") String merchant_id,
            @RequestParam(required = false , value = "order_id") String order_id,
            @RequestParam(required = false , value = "payment_id") String payment_id,
            @RequestParam(required = false , value = "payhere_amount") String payhere_amount,
            @RequestParam(required = false , value = "payhere_currency") String payhere_currency,
            @RequestParam(required = false , value = "status_code") String status_code,
            @RequestParam(required = false , value = "md5sig") String md5sig,
            @RequestParam(required = false , value = "custom_1") String custom_1,
            @RequestParam(required = false , value = "custom_2") String custom_2,
            @RequestParam(required = false , value = "method") String method,
            @RequestParam(required = false , value = "card_holder_name") String card_holder_name,
            @RequestParam(required = false , value = "card_no") String card_no,
            @RequestParam(required = false , value = "card_expiry") String card_expiry,
            @RequestParam(required = false , value = "customer_email") String customer_email,
            @RequestParam(required = false , value = "customer_phone") String customer_phone,
            @RequestParam(required = false , value = "customer_address") String customer_address,
            @RequestParam(required = false , value = "customer_city") String customer_city,
            @RequestParam(required = false , value = "customer_country") String customer_country,
            @RequestParam(required = false , value = "payment_hash") String payment_hash,
            @RequestParam(required = false , value = "gateway_version") String gateway_version,
            @RequestParam(required = false , value = "hash_version") String hash_version,
            @RequestParam(required = false , value = "status_message") String status_message
    ) {

        // Map parameters to PaymentData
        PaymentData paymentData = new PaymentData();
        paymentData.setMerchant_id(merchant_id);
        paymentData.setOrder_id(order_id);
        paymentData.setPayment_id(payment_id);
        paymentData.setPayhere_amount(payhere_amount);
        paymentData.setPayhere_currency(payhere_currency);
        paymentData.setStatus_code(status_code);
        paymentData.setMd5sig(md5sig);
        paymentData.setCustom_1(custom_1);
        paymentData.setCustom_2(custom_2);
        paymentData.setMethod(method);
        paymentData.setCard_holder_name(card_holder_name);
        paymentData.setCard_no(card_no);
        paymentData.setCard_expiry(card_expiry);
        paymentData.setCustomer_email(customer_email);
        paymentData.setCustomer_phone(customer_phone);
        paymentData.setCustomer_address(customer_address);
        paymentData.setCustomer_city(customer_city);
        paymentData.setCustomer_country(customer_country);
        paymentData.setPayment_hash(payment_hash);
        paymentData.setGateway_version(gateway_version);
        paymentData.setHash_version(hash_version);
        paymentData.setStatus_message(status_message);

        // Print PaymentData object
        System.out.println("Capture Data Payment:");
        System.out.println(paymentData);

        paymentService.processNotification(paymentData);

        return ResponseEntity.ok("Data logged successfully!");
    }


    @GetMapping("/return")
   public ResponseEntity<?> processReturn(
            @RequestParam(value = "paymentId", required = false) Long paymentId,
            @RequestParam(value = "payerId", required = false) String payerId,
            @RequestParam(value = "status", required = false) String status
          ){
        System.out.println("Received return Payment");
        System.out.println(paymentId);
        System.out.println(payerId);
        System.out.println(status);
        paymentService.processReturn(paymentId, payerId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> processCancel(@RequestParam Long paymentId) {
        paymentService.processCancel(paymentId);
        return new ResponseEntity<>(HttpStatus.OK);

    }



}
