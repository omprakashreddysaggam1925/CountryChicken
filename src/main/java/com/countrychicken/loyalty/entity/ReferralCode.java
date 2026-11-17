package com.countrychicken.loyalty.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "referral_codes")
public class ReferralCode extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Column(nullable = false, length = 32, unique = true) private String code;
    protected ReferralCode() {}
    public ReferralCode(User user, String code) { this.user = user; this.code = code; }
    public User getUser() { return user; } public String getCode() { return code; }
}
