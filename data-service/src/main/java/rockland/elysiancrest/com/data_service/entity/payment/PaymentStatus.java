package rockland.elysiancrest.com.data_service.entity.payment;

public enum PaymentStatus {
    SUCCESS(2, "Success"),
    PENDING(0, "Pending"),
    CANCELED(-1, "Canceled"),
    FAILED(-2, "Failed"),
    CHARGEDBACK(-3, "Chargeback");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}