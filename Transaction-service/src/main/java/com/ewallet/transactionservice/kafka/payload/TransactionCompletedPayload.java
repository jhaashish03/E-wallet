package com.ewallet.transactionservice.kafka.payload;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TransactionCompletedPayload {
    private String sender;
    private UUID transactionId;
    private String status;
}
