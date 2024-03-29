package com.ewallet.walletservice.repositories;

import com.ewallet.walletservice.entities.WalletCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface WalletCreditRepository extends JpaRepository<WalletCredit, UUID> {
}
