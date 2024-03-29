package com.ewallet.transactionservice.kafka.payload;

import jdk.jfr.BooleanFlag;
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
