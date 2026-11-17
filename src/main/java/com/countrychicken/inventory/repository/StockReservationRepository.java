package com.countrychicken.inventory.repository;

import com.countrychicken.inventory.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.time.Instant;
import java.util.*;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StockReservation r where r.id = :id")
    Optional<StockReservation> findByIdForUpdate(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StockReservation r where r.inventoryItem.id = :itemId and r.status = 'ACTIVE' and r.expiresAt <= :now")
    List<StockReservation> findExpiredForUpdate(UUID itemId, Instant now);
}
