package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.PaymentNotification;
import com.commonlibrary.contract.v1.PaymentRequestDto;
import com.commonlibrary.contract.v1.PaymentResult;
import com.commonlibrary.contract.v1.TransactionDto;
import rockland.elysiancrest.com.data_service.dto.PaymentData;
import rockland.elysiancrest.com.data_service.service.impl.PaymentServiceImpl;

public interface PaymentService {
    PaymentResult initiatePayment(PaymentRequestDto paymentRequest);
    void processReturn(Long paymentId,String payerId,  String status);
    void processCancel(Long paymentId);
    void processNotification(PaymentData paymentData);
    public String generatePaymentUrl(PaymentRequestDto paymentRequest);
}
