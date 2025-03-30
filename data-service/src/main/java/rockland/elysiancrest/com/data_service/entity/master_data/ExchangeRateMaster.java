package rockland.elysiancrest.com.data_service.entity.master_data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditLogListener.class)
public class ExchangeRateMaster extends BaseEntity {
    @Column(name = "echange_rate_type")
    private String exchangeRateType;

    @Column(name = "exchange_rate")
    private Double exRate; // Exchange Rate

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "from_currency")
    private String fromCurrency;

    @Column(name = "to_currency")
    private String toCurrency;
}
