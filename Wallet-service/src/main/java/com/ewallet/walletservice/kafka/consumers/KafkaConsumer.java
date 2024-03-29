package com.ewallet.walletservice.kafka.consumers;


import com.ewallet.walletservice.kafka.payloads.TransactionInitPayload;
import com.ewallet.walletservice.services.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class KafkaConsumer {


    private final WalletService walletService;



    @Autowired
    public KafkaConsumer(WalletService walletService) {
        this.walletService = walletService;

    }
    @KafkaListener(topics = {"user_created"},groupId = "wallet_consumer")
    public void listenUserCreationEvent(ConsumerRecord<String,String> consumerRecord){
        log.info("Read the user creation event with username " + consumerRecord.value());
        walletService.createNewWallet(consumerRecord.value());
        log.info("User creation completed with username " + consumerRecord.value());

    }
    @KafkaListener(topics = {"txn_init"},groupId = "wallet_consumer")
    public void listenTransactionInitEvent(ConsumerRecord<String, Object> consumerRecord) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        TransactionInitPayload transactionInitPayload=objectMapper.readValue(consumerRecord.value().toString(),TransactionInitPayload.class);
      log.info("Payment being processed for txn id "+transactionInitPayload.getTransactionId());
        walletService.processTransaction(transactionInitPayload);


    }


}
