package rockland.elysiancrest.com.data_service.service;


import com.lowagie.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import rockland.elysiancrest.com.data_service.entity.order.OrderAddon;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.util.QRImageUtil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PdfGenerationService {
    private final TemplateEngine templateEngine;

    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(Long orderReference, Date deliveryDate, LocalDateTime orderDate,
                              String currency, BigDecimal addonSubTotal, BigDecimal addonSubTotalUSD,
                              BigDecimal deliveryFee, BigDecimal totalOrderAmount, String customerName,
                              BigDecimal deliveryFeeUSD, BigDecimal totalOrderAmountUSD,
                              String billingAddress, String userPhoneNumber, String userEmail, String shippingAddress,
                              String gifteeName, List<OrderItemMaster> orderItems, List<OrderAddon> orderAddons,
                              boolean isGift, String gifteeContactNumber, String gifterName,
                              String gifterMessage, String deliveryOption, BigDecimal orderItemsSubtotal,
                              BigDecimal orderItemsSubtotalUSD, String cardNumber, String orderNote) throws DocumentException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("orderReference", orderReference);
        context.setVariable("deliveryDate", deliveryDate);
        context.setVariable("orderDate", orderDate);
        context.setVariable("currency", currency);
        context.setVariable("addonSubTotal", addonSubTotal);
        context.setVariable("addonSubTotalUSD", addonSubTotalUSD);
        context.setVariable("deliveryFee", deliveryFee);
        context.setVariable("deliveryFeeUSD", deliveryFeeUSD);
        context.setVariable("totalOrderAmount", totalOrderAmount);
        context.setVariable("totalOrderAmountUSD", totalOrderAmountUSD);
        context.setVariable("customerName", customerName);
        context.setVariable("billingAddress", billingAddress);
        context.setVariable("userPhoneNumber", userPhoneNumber);
        context.setVariable("userEmail1", userEmail);
        context.setVariable("shippingAddress", shippingAddress);
        context.setVariable("gifteeName", gifteeName);
        context.setVariable("orderItems", orderItems);
        context.setVariable("orderAddons", orderAddons);

        context.setVariable("isGift", isGift);
        context.setVariable("gifteeContactNumber", gifteeContactNumber);
        context.setVariable("gifterName", gifterName);
        context.setVariable("gifterMessage", gifterMessage);
        context.setVariable("deliveryOption", deliveryOption);
        context.setVariable("orderItemsSubtotal", orderItemsSubtotal);
        context.setVariable("orderItemsSubtotalUSD", orderItemsSubtotalUSD);
        context.setVariable("cardNumber", cardNumber);
        context.setVariable("orderNote", orderNote);
        try {
            String qrString = QRImageUtil.generateQRCodeBase64(String.valueOf(orderReference));
            context.setVariable("qrCode", qrString);
        } catch (Exception e) {
            log.error("Error in generating QR ",e);
        }


        // Generate HTML content from Thymeleaf template
        String htmlContent = templateEngine.process("invoice_template", context); // "invoice_template" is the name of your Thymeleaf HTML template

        // Convert HTML to PDF
        return convertHtmlToPdf(htmlContent);
    }

    public byte[] convertHtmlToPdf(String htmlContent) throws DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        renderer.createPDF(pdfOutputStream);
        return pdfOutputStream.toByteArray();
    }

}
