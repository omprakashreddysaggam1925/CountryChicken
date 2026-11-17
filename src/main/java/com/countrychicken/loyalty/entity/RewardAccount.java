package com.countrychicken.loyalty.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "reward_accounts")
public class RewardAccount extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Column(name = "balance_points", nullable = false) private long balancePoints;
    protected RewardAccount() {}
    public RewardAccount(User user) { this.user = user; }
    public void credit(long points) { if (points <= 0) throw new IllegalArgumentException("Points must be positive"); balancePoints += points; }
    public void debit(long points) { if (points <= 0 || points > balancePoints) throw new IllegalStateException("Insufficient reward points"); balancePoints -= points; }
    public User getUser() { return user; } public long getBalancePoints() { return balancePoints; }
}
