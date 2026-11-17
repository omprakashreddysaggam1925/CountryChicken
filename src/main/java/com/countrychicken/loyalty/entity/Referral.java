package com.countrychicken.loyalty.entity;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "referral_code_id", nullable = false) private ReferralCode referralCode;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "referrer_user_id", nullable = false) private User referrer;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "referred_user_id", nullable = false, unique = true) private User referred;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private ReferralStatus status;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "qualifying_order_id", unique = true) private CustomerOrder qualifyingOrder;
    protected Referral() {}
    public Referral(ReferralCode code, User referred) {
        this.referralCode = code; this.referrer = code.getUser(); this.referred = referred; this.status = ReferralStatus.PENDING;
    }
    public void qualify(CustomerOrder order) { status = ReferralStatus.QUALIFIED; qualifyingOrder = order; }
    public void reverse() { status = ReferralStatus.REVERSED; }
    public User getReferrer() { return referrer; } public User getReferred() { return referred; }
    public ReferralStatus getStatus() { return status; } public CustomerOrder getQualifyingOrder() { return qualifyingOrder; }
}
