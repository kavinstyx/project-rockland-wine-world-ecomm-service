package rockland.elysiancrest.com.data_service.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartAddon;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cart")
public class CartMaster extends BaseEntity {

    @Column(name = "cart_type")
    private String cartType; // e.g., "normal", "wedding", "corporate"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference  // Child side of the relationship
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // Forward side of the relationship
    private List<CartItem> cartItems = new ArrayList<>(); // Initialize as an empty list

    @Column(name = "status")
    private String status; // e.g., "active", "completed", "abandoned"

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = true)
    private CityMaster cityMaster;

    @Column(name = "delivery_charges_in_rupee")
    private BigDecimal deliveryChargesInRupee;

    @Column(name = "delivery_charges_in_dollar")
    private BigDecimal deliveryChargesInDollar;

    @Getter
    @Column(name = "total_price_in_rupee")
    private BigDecimal totalPriceInRupee;

    @Getter
    @Column(name = "total_price_in_dollar")
    private BigDecimal totalPriceInDollar;

    @Column(name = "session_id")
    private String sessionId;  // For guest users

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "delivery_option")
    private String deliveryOption;

    @Column(name = "gifter_name")
    private String gifterName;

    @Column(name = "giftee_name")
    private String gifteeName;

    @Column(name = "giftee_contact_number")
    private String gifteeContactNumber;

    @Column(name = "gifter_message")
    private String gifterMessage;

    @Column(name = "is_gift")
    private Boolean isGift = false;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartAddon> cartAddons = new ArrayList<>();

    @Column(name = "addon_total_in_rupee")
    private BigDecimal addonTotalInRupee;

    @Column(name = "addon_total_in_dollar")
    private BigDecimal addonTotalInDollar;

    @Column(name = "order_note")
    private String orderNote;


    public void setDeliveryChargesInRupee(BigDecimal deliveryCharges) {
        this.deliveryChargesInRupee = deliveryCharges;
        updateTotalPriceInRupee(); // Update total price whenever delivery charges are set
    }

    public void setDeliveryChargesInDollar(BigDecimal deliveryCharges) {
        this.deliveryChargesInDollar = deliveryCharges;
        updateTotalPriceInDollar(); // Update total price whenever delivery charges are set
    }

    public void updateTotalPriceInRupee() {
        BigDecimal itemsTotal = cartItems.stream()
                .map(CartItem::getTotalPriceInRupee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal addonsTotal = cartAddons.stream()
                .map(CartAddon::getTotalPriceInRupee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalPriceInRupee = itemsTotal
                .add(deliveryChargesInRupee != null ? deliveryChargesInRupee : BigDecimal.ZERO)
                .add(addonsTotal);
    }

    public void updateTotalPriceInDollar() {
        BigDecimal itemsTotal = cartItems.stream()
                .map(CartItem::getTotalPriceInDollar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal addonsTotal = cartAddons.stream()
                .map(CartAddon::getTotalPriceInDollar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalPriceInDollar = itemsTotal
                .add(deliveryChargesInDollar != null ? deliveryChargesInDollar : BigDecimal.ZERO)
                .add(addonsTotal);
    }

    public void setAddonsChargesInRupee() {
        this.addonTotalInRupee = cartAddons.stream()
                .map(CartAddon::getTotalPriceInRupee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setAddonsChargesInDollar() {
        this.addonTotalInDollar = cartAddons.stream()
                .map(CartAddon::getTotalPriceInDollar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    // Add an addon to the cart
    public void addAddon(CartAddon cartAddon) {
        cartAddons.add(cartAddon);
        cartAddon.setCart(this);
        updateTotalPriceInRupee();
        updateTotalPriceInDollar();
    }

    public void removeAllAddons() {
        for (CartAddon cartAddon : cartAddons) {
            cartAddon.setCart(null);
        }
        cartAddons.clear();
        updateTotalPriceInRupee();
        updateTotalPriceInDollar();
    }


}
