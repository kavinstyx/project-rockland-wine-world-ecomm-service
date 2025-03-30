package rockland.elysiancrest.com.data_service.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.master_data.PlantMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductMaster product;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "buffer_quantity", nullable = false)
    private int bufferQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private PlantMaster plantMaster;

    public boolean isAvailableForReservation(int requestedQuantity, Long plantId) {
        // Check that the plantId matches and calculate available quantity
        if (!this.plantMaster.getId().equals(plantId)) {
            return false; // Plant ID mismatch, cannot reserve from this inventory
        }

        int availableForSale = totalQuantity - reservedQuantity - bufferQuantity;
        return availableForSale >= requestedQuantity;
    }

    // Reserve a quantity if available, considering plantId
    public boolean reserveQuantity(int quantity, Long plantId) {
        if (isAvailableForReservation(quantity, plantId)) {
            this.reservedQuantity += quantity;
            return true;
        }
        return false;
    }

    public void releaseReservedQuantity(int quantity, Long plantId) {
        // Ensure the plant ID matches
        if (!this.plantMaster.getId().equals(plantId)) {
            throw new IllegalArgumentException("Invalid plant ID for releasing reserved quantity.");
        }

        this.reservedQuantity -= quantity;
        if (this.reservedQuantity < 0) {
            this.reservedQuantity = 0; // Ensuring reserved quantity does not go negative
        }
    }


    public void deductReservedQuantity(int quantity, Long plantId) {
        // Ensure the plant ID matches
        if (!this.plantMaster.getId().equals(plantId)) {
            throw new IllegalArgumentException("Invalid plant ID for deducting reserved quantity.");
        }

        // Deduct reserved quantity from the total stock
        this.totalQuantity -= quantity;

        // Release the reserved quantity
        this.releaseReservedQuantity(quantity, plantId);
    }


    public void finalizePurchase(int quantity, Long plantId) {
        // Ensure the plant ID matches
        if (!this.plantMaster.getId().equals(plantId)) {
            throw new IllegalArgumentException("Invalid plant ID for finalizing the purchase.");
        }

        // Ensure quantity to finalize does not exceed reserved quantity
        if (quantity <= this.reservedQuantity) {
            // Deduct reserved quantity from the total stock
            this.totalQuantity -= quantity;

            // Adjust reserved quantity accordingly
            this.reservedQuantity -= quantity;

            // Ensure total quantity does not go negative
            if (this.totalQuantity < 0) {
                this.totalQuantity = 0;
            }
        } else {
            throw new IllegalArgumentException("Insufficient reserved quantity to finalize the purchase.");
        }
    }

}