package com.ewallet.transactionservice.services;

import com.ewallet.transactionservice.dtos.TransactionDto;
import com.ewallet.transactionservice.entities.Transaction;
import com.ewallet.transactionservice.enums.TransactionStatus;
import com.ewallet.transactionservice.kafka.payload.TransactionCompletedPayload;
import com.ewallet.transactionservice.kafka.payload.TransactionInitPayload;
import com.ewallet.transactionservice.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, TransactionInitPayload> kafkaTemplate;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, KafkaTemplate<String, TransactionInitPayload> kafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public UUID initiatePayment(TransactionDto transactionDto){
        Transaction transaction=Transaction.builder()
                .sender(transactionDto.sender())
                .receiver(transactionDto.receiver())
                .message(transactionDto.message())
                .amount(transactionDto.amount())
                .transactionStatus(TransactionStatus.PENDING)
                .build();
        transaction=transactionRepository.saveAndFlush(transaction);
        log.info("Transaction initiated with txn id: {}", transaction.getTransactionId());
        publishTransactionInitiationToKafka(transaction);

        return transaction.getTransactionId();
    }

    private void publishTransactionInitiationToKafka(Transaction transaction){
        TransactionInitPayload transactionInitPayload=TransactionInitPayload.builder()
                .transactionId(transaction.getTransactionId())
                        .sender(transaction.getSender())
                                .receiver(transaction.getReceiver())
                                        .amount(transaction.getAmount())
                                                .build();

        kafkaTemplate.send("txn_init",transaction.getSender(),transactionInitPayload);
    }

    @Transactional
    public void completeTransaction(TransactionCompletedPayload transactionCompletedPayload){
       Transaction transaction= transactionRepository.findById(transactionCompletedPayload.getTransactionId()).get();
       transaction.setTransactionStatus(TransactionStatus.valueOf(transactionCompletedPayload.getStatus()));
    }

    public TransactionStatus transactionStatus(String transactionId){
      return   transactionRepository.findById(UUID.fromString(transactionId)).get().getTransactionStatus();
    }
}
