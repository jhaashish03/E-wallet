package com.ewallet.walletservice.repositories;

import com.ewallet.walletservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet,UUID> {
    Wallet findByUserNameIgnoreCase(String userName);

}
