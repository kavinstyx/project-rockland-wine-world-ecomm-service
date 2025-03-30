package rockland.elysiancrest.com.data_service.dto;

public class InventoryDTO {
    private Long id;
    private int totalQuantity;
    private int reservedQuantity;
    private int availableQuantity;  // Calculated as totalQuantity - reservedQuantity

}
