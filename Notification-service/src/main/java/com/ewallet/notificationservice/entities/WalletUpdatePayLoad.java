package com.ewallet.notificationservice.entities;

import com.ewallet.notificationservice.enums.TransactionType;
import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletUpdatePayLoad implements Serializable {
    private String username;
    private TransactionType transactionType;
    private Double transactionAmount;
    private Double totalAmount;
}
