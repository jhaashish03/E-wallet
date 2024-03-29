package com.ewallet.notificationservice.kafka.consumers;

import com.ewallet.notificationservice.entities.EmailRequest;
import com.ewallet.notificationservice.entities.WalletUpdatePayLoad;
import com.ewallet.notificationservice.enums.TransactionType;
import com.ewallet.notificationservice.services.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {


    private final MailService mailService;

    @Autowired
    public KafkaConsumer(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = {"user_created"},groupId = "notificationservice")
    public void listenToUserCreationEvent(ConsumerRecord<String, Object> consumerRecord){
        String to= (String) consumerRecord.value();

        EmailRequest emailRequest = EmailRequest.builder()
                .to(to)
                .subject("ACCOUNT CREATION SUCCESSFUL")
                .body("""
                        Hi user,
                        Hope your are doing well!.
                        Your account has been successfully created in e-wallet application.
                        Your wallet is now created and available to be used. You have been provided 100rs of welcome bonus in wallet.

                        Thanks and Regards
                        E-wallet application
                        Developer-Ashish Jha
                        LinkedIn Account- https://www.linkedin.com/in/jhaashish03""").build();
        mailService.triggerMail(emailRequest);


    }

    @KafkaListener(topics = {"wallet_updated"},groupId = "notificationservice")
    public void listenToWalletUpdateEvent(ConsumerRecord<String, Object> consumerRecord) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();

        WalletUpdatePayLoad walletUpdatePayLoad= objectMapper.readValue(consumerRecord.value().toString(), WalletUpdatePayLoad.class);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(walletUpdatePayLoad.getUsername())
                .subject("WALLET TRANSACTION")
                .body("Hi user,\n\n Your wallet has been "+ (walletUpdatePayLoad.getTransactionType().name()+"ED").toLowerCase()+" with Rs. "+ walletUpdatePayLoad.getTransactionAmount()+"\n Total available amount in wallet: "+walletUpdatePayLoad.getTotalAmount()+" Thanks and Regards\nE-wallet Application\nDeveloper-Ashish Jha\nLinkedIn Account- https://www.linkedin.com/in/jhaashish03").build();
        mailService.triggerMail(emailRequest);


    }


}
