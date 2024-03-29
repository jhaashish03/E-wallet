package com.ewallet.walletservice.kafka.payloads;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TransactionInitPayload {
    private UUID transactionId;
    private String sender;
    private String receiver;
    private Double amount;
}
