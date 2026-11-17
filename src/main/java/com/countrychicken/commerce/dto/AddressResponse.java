package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.DeliveryAddress;
import java.util.UUID;
public record AddressResponse(UUID id, String label, String recipientName, String line1, String line2, String city,
                              String state, String postalCode, String country, String phone, boolean defaultAddress) {
    public static AddressResponse from(DeliveryAddress a) { return new AddressResponse(a.getId(), a.getLabel(), a.getRecipientName(), a.getLine1(), a.getLine2(), a.getCity(), a.getState(), a.getPostalCode(), a.getCountry(), a.getPhone(), a.isDefaultAddress()); }
}
