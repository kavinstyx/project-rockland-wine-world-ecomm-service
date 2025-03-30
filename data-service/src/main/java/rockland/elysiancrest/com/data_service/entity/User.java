package rockland.elysiancrest.com.data_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.otp.Otp;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditLogListener.class)
public class User extends BaseEntity implements UserDetails {

    private String name;
    private String address;
    private String username;
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String token;

    @Column(name = "customer_code", unique = true, nullable = true)
    private String customerCode;

    @Column(name = "contact_numbers")
    private String contactNumbers;

    @Column(name = "nic", unique = true)
    private String nic;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "live_in")
    private String liveIn;

    @Column(name = "customer_group_type")
    private String customerGroupType;  // Example values: "Corporate", "Retail", etc.

    @Column(name = "credit_term")
    private String creditTerm;

    @Column(name = "delivery_plant")
    private String deliveryPlant;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "profile_pic_path")
    private String profilePicPath;

    @Column(name = "profile_pic_temp_link")
    private String profilePicTemporaryLink;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // Parent side of the relationship
    private List<CartMaster> cartMasters;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> billingAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> deliveryAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Otp> otps;

    @Column(name = "user_role")
    private String userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // No need for username as we're using email
    @Override
    public String getUsername() {
        return email; // Map username to email
    }
}
