package com.countrychicken.loyalty.service;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.exception.*;
import com.countrychicken.loyalty.dto.*;
import com.countrychicken.loyalty.entity.*;
import com.countrychicken.loyalty.repository.*;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class LoyaltyService {
    private static final long REFERRER_BONUS = 100;
    private static final long REFERRED_BONUS = 50;
    private final ReferralCodeRepository codes;
    private final ReferralRepository referrals;
    private final RewardAccountRepository accounts;
    private final RewardTransactionRepository transactions;
    private final UserRepository users;

    public LoyaltyService(ReferralCodeRepository codes, ReferralRepository referrals, RewardAccountRepository accounts,
                          RewardTransactionRepository transactions, UserRepository users) {
        this.codes = codes; this.referrals = referrals; this.accounts = accounts; this.transactions = transactions; this.users = users;
    }

    @Transactional
    public void initialize(User user, String referralCode) {
        accounts.save(new RewardAccount(user));
        codes.save(new ReferralCode(user, generateCode(user)));
        if (referralCode == null || referralCode.isBlank()) return;
        ReferralCode inviter = codes.findByCode(normalize(referralCode))
                .orElseThrow(() -> new ConflictException("Referral code is invalid"));
        if (inviter.getUser().getId().equals(user.getId())) throw new ConflictException("Self-referral is not allowed");
        referrals.save(new Referral(inviter, user));
    }

    @Transactional
    public void awardForPaidOrder(CustomerOrder order) {
        UUID userId = order.getUser().getId();
        long earned = order.getTotal().longValue() / 100;
        if (earned > 0) credit(userId, earned, "ORDER:" + order.getId(), RewardTransactionType.ORDER_EARNED, order);
        Referral referral = referrals.findByReferredId(userId).orElse(null);
        if (referral == null || referral.getStatus() != ReferralStatus.PENDING) return;
        referral.qualify(order);
        credit(referral.getReferrer().getId(), REFERRER_BONUS, "REFERRAL:REFERRER:" + referral.getId(),
                RewardTransactionType.REFERRAL_BONUS, order);
        credit(userId, REFERRED_BONUS, "REFERRAL:REFERRED:" + referral.getId(),
                RewardTransactionType.REFERRAL_BONUS, order);
    }

    @Transactional
    public void reverseForRefund(CustomerOrder order) {
        UUID userId = order.getUser().getId();
        reverse(userId, "ORDER:" + order.getId(), "REFUND:ORDER:" + order.getId(), order);
        Referral referral = referrals.findByReferredId(userId).orElse(null);
        if (referral == null || referral.getQualifyingOrder() == null || !referral.getQualifyingOrder().getId().equals(order.getId())
                || referral.getStatus() != ReferralStatus.QUALIFIED) return;
        reverse(referral.getReferrer().getId(), "REFERRAL:REFERRER:" + referral.getId(), "REFUND:REFERRER:" + referral.getId(), order);
        reverse(userId, "REFERRAL:REFERRED:" + referral.getId(), "REFUND:REFERRED:" + referral.getId(), order);
        referral.reverse();
    }

    @Transactional(readOnly = true)
    public RewardAccountResponse account(UUID userId) {
        return RewardAccountResponse.from(accounts.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward account was not found")));
    }

    @Transactional(readOnly = true)
    public ReferralResponse referral(UUID userId) {
        ReferralCode code = codes.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Referral code was not found"));
        return ReferralResponse.from(code, referrals.findByReferredId(userId).orElse(null));
    }

    private void credit(UUID userId, long points, String reference, RewardTransactionType type, CustomerOrder order) {
        if (transactions.existsByReferenceKey(reference)) return;
        RewardAccount account = accounts.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Reward account was not found"));
        account.credit(points);
        transactions.save(new RewardTransaction(account, type, points, reference, order));
    }
    private void reverse(UUID userId, String original, String reference, CustomerOrder order) {
        if (!transactions.existsByReferenceKey(original) || transactions.existsByReferenceKey(reference)) return;
        RewardAccount account = accounts.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Reward account was not found"));
        long points = transactions.findByReferenceKey(original).orElseThrow().getPoints();
        if (points <= 0) return;
        account.debit(points);
        transactions.save(new RewardTransaction(account, RewardTransactionType.REFUND_REVERSAL, -points, reference, order));
    }
    private String normalize(String value) { return value.trim().toUpperCase(Locale.ROOT); }
    private String generateCode(User user) { return ("CC" + user.getId().toString().replace("-", "")).substring(0, 12).toUpperCase(Locale.ROOT); }
}
