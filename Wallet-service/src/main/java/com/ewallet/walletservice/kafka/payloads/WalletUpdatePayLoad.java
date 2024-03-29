package com.ewallet.walletservice.kafka.payloads;

import com.ewallet.walletservice.enums.TransactionType;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletUpdatePayLoad {
    private String username;
    private TransactionType transactionType;
    private Double transactionAmount;
    private Double totalAmount;
}
