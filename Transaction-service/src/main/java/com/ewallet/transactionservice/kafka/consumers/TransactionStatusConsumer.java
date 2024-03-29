package com.ewallet.transactionservice.kafka.consumers;

import com.ewallet.transactionservice.kafka.payload.TransactionCompletedPayload;
import com.ewallet.transactionservice.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableKafka
public class TransactionStatusConsumer {

    private final TransactionService transactionService;

    @Autowired
    public TransactionStatusConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = {"txn_completed"},groupId = "transactionService")
    public void listenTransactionInitEvent(ConsumerRecord<String, Object> consumerRecord) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        TransactionCompletedPayload transactionInitPayload=objectMapper.readValue(consumerRecord.value().toString(),TransactionCompletedPayload.class);
        transactionService.completeTransaction(transactionInitPayload);
        log.info("Transaction status updated");

    }
}
