package rockland.elysiancrest.com.data_service.entity.quotation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quotation")
@EntityListeners(AuditLogListener.class)
public class QuotationMaster extends BaseEntity {

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "status")
    private String status;

    @Column(name = "total_price")
    private Double totalPrice = 0.0;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "sales_rep")
    private String salesRep;

    @Column(name = "payment_note")
    private String paymentNote;

    @Column(name = "validity_period")
    private LocalDate validityPeriod;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToMany(mappedBy = "quotationMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationItem> items = new ArrayList<>();

    // Add item and set its association with QuotationMaster
    public void addItem(QuotationItem item) {
        items.add(item);
        item.setQuotationMaster(this);
    }

    // Remove item and break the association
    public void removeItem(QuotationItem item) {
        items.remove(item);
        item.setQuotationMaster(null);
    }

    // Calculate total price based on associated QuotationItems
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(QuotationItem::getTotalPriceAfterDiscount)
                .sum();
    }
}

