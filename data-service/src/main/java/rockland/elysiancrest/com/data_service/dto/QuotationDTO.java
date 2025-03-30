package rockland.elysiancrest.com.data_service.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationDTO {

    private Long id; // ID of the quotation (inherited from BaseEntity)

    private String customerName; // Name of the customer

    private String customerId; // ID of the customer

    private String status; // Status of the quotation (e.g., "PENDING", "APPROVED", etc.)

    private String email;

    private String address;

    private Double totalPrice; // Total price of the quotation (calculated from items)

    private LocalDateTime createdTime;

    private List<QuotationItemDTO> items; // List of associated quotation items

    private String salesRep;

    private String paymentNote;

    private LocalDate validityPeriod;

    private String paymentMethod;

}

