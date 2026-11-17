package com.countrychicken.loyalty.entity;

import com.countrychicken.user.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RewardAccountTest {
    @Test
    void creditsAndDebitsPointsWithoutAllowingNegativeBalance() {
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        RewardAccount account = new RewardAccount(user);

        account.credit(150);
        account.debit(50);

        assertEquals(100, account.getBalancePoints());
        assertThrows(IllegalStateException.class, () -> account.debit(101));
    }
}
