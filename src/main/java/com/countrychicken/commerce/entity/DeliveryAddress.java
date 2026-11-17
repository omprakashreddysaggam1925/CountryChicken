package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "delivery_addresses")
public class DeliveryAddress extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 50) private String label;
    @Column(name = "recipient_name", nullable = false, length = 200) private String recipientName;
    @Column(nullable = false, length = 200) private String line1;
    @Column(length = 200) private String line2;
    @Column(nullable = false, length = 100) private String city;
    @Column(nullable = false, length = 100) private String state;
    @Column(name = "postal_code", nullable = false, length = 20) private String postalCode;
    @Column(nullable = false, length = 100) private String country;
    @Column(nullable = false, length = 32) private String phone;
    @Column(name = "default_address", nullable = false) private boolean defaultAddress;

    protected DeliveryAddress() {}
    public DeliveryAddress(User user, String label, String recipientName, String line1, String line2,
                           String city, String state, String postalCode, String country, String phone, boolean defaultAddress) {
        this.user = user; update(label, recipientName, line1, line2, city, state, postalCode, country, phone);
        this.defaultAddress = defaultAddress;
    }
    public void update(String label, String recipientName, String line1, String line2, String city, String state,
                       String postalCode, String country, String phone) {
        this.label = label; this.recipientName = recipientName; this.line1 = line1; this.line2 = line2;
        this.city = city; this.state = state; this.postalCode = postalCode; this.country = country; this.phone = phone;
    }
    public void markDefault(boolean value) { this.defaultAddress = value; }
    public User getUser() { return user; }
    public String getLabel() { return label; } public String getRecipientName() { return recipientName; }
    public String getLine1() { return line1; } public String getLine2() { return line2; }
    public String getCity() { return city; } public String getState() { return state; }
    public String getPostalCode() { return postalCode; } public String getCountry() { return country; }
    public String getPhone() { return phone; } public boolean isDefaultAddress() { return defaultAddress; }
}
