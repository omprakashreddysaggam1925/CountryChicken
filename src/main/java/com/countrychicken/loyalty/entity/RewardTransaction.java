package com.countrychicken.loyalty.entity;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "reward_transactions")
public class RewardTransaction extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "account_id", nullable = false) private RewardAccount account;
    @Enumerated(EnumType.STRING) @Column(name = "transaction_type", nullable = false, length = 32) private RewardTransactionType transactionType;
    @Column(nullable = false) private long points;
    @Column(name = "reference_key", nullable = false, unique = true, length = 128) private String referenceKey;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") private CustomerOrder order;
    protected RewardTransaction() {}
    public RewardTransaction(RewardAccount account, RewardTransactionType type, long points, String referenceKey, CustomerOrder order) {
        this.account = account; this.transactionType = type; this.points = points; this.referenceKey = referenceKey; this.order = order;
    }
    public long getPoints() { return points; }
}
