package com.countrychicken.commerce.service;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.entity.DeliveryAddress;
import com.countrychicken.commerce.repository.DeliveryAddressRepository;
import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class AddressService {
    private final DeliveryAddressRepository addresses;
    private final UserRepository users;
    public AddressService(DeliveryAddressRepository addresses, UserRepository users) { this.addresses = addresses; this.users = users; }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(UUID userId) { return addresses.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId).stream().map(AddressResponse::from).toList(); }
    @Transactional
    public AddressResponse create(UUID userId, AddressRequest request) {
        User user = user(userId);
        if (request.defaultAddress()) clearDefaults(userId);
        return AddressResponse.from(addresses.save(new DeliveryAddress(user, clean(request.label()), clean(request.recipientName()), clean(request.line1()), clean(request.line2()), clean(request.city()), clean(request.state()), clean(request.postalCode()), clean(request.country()), clean(request.phone()), request.defaultAddress())));
    }
    @Transactional
    public AddressResponse update(UUID userId, UUID id, AddressRequest request) {
        DeliveryAddress address = address(userId, id);
        if (request.defaultAddress()) clearDefaults(userId);
        address.update(clean(request.label()), clean(request.recipientName()), clean(request.line1()), clean(request.line2()), clean(request.city()), clean(request.state()), clean(request.postalCode()), clean(request.country()), clean(request.phone()));
        address.markDefault(request.defaultAddress());
        return AddressResponse.from(address);
    }
    @Transactional
    public void delete(UUID userId, UUID id) { addresses.delete(address(userId, id)); }
    @Transactional
    public AddressResponse setDefault(UUID userId, UUID id) { DeliveryAddress address = address(userId, id); clearDefaults(userId); address.markDefault(true); return AddressResponse.from(address); }
    private void clearDefaults(UUID userId) { addresses.findByUserId(userId).forEach(a -> a.markDefault(false)); }
    private User user(UUID id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User was not found")); }
    private DeliveryAddress address(UUID userId, UUID id) { return addresses.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Delivery address was not found")); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
