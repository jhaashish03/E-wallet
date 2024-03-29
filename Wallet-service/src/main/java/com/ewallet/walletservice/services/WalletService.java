package com.ewallet.walletservice.services;

import com.ewallet.walletservice.dtos.WalletCreditDto;
import com.ewallet.walletservice.entities.Wallet;
import com.ewallet.walletservice.entities.WalletCredit;
import com.ewallet.walletservice.enums.PaymentStatus;
import com.ewallet.walletservice.enums.TransactionType;
import com.ewallet.walletservice.exceptions.InsufficientAmountException;
import com.ewallet.walletservice.kafka.payloads.TransactionCompletedPayload;
import com.ewallet.walletservice.kafka.payloads.TransactionInitPayload;
import com.ewallet.walletservice.kafka.payloads.WalletUpdatePayLoad;
import com.ewallet.walletservice.repositories.WalletCreditRepository;
import com.ewallet.walletservice.repositories.WalletRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


@Service
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final KafkaTemplate<String, WalletUpdatePayLoad> walletUpdatePayLoadKafkaTemplate;
    private final KafkaTemplate<String, TransactionCompletedPayload> transactionCompletedPayloadKafkaTemplate;
    private final WalletCreditRepository walletCreditRepository;
    private final Environment environment;
    private final HashMap<String,PaymentStatus> stringPaymentStatusHashMap=new HashMap<>();

    @Autowired
    public WalletService(WalletRepository walletRepository, @Qualifier("walletUpdatedProducerTemplate") KafkaTemplate<String, WalletUpdatePayLoad> walletUpdatePayLoadKafkaTemplate, @Qualifier("transactionCompletedTemplate") KafkaTemplate<String, TransactionCompletedPayload> transactionCompletedPayloadKafkaTemplate, WalletCreditRepository walletCreditRepository, Environment environment) {
        this.walletRepository = walletRepository;
        this.walletUpdatePayLoadKafkaTemplate = walletUpdatePayLoadKafkaTemplate;
        this.transactionCompletedPayloadKafkaTemplate = transactionCompletedPayloadKafkaTemplate;
        this.walletCreditRepository = walletCreditRepository;
        this.environment = environment;
        stringPaymentStatusHashMap.put("paid",PaymentStatus.PAID);
        stringPaymentStatusHashMap.put("cancelled",PaymentStatus.CANCELLED);
        stringPaymentStatusHashMap.put("expired",PaymentStatus.EXPIRED);
        stringPaymentStatusHashMap.put("created",PaymentStatus.CREATED);
    }

    @Transactional
    public void createNewWallet(String username){
       Wallet wallet= Wallet.builder().amount(100.0)
                .userName(username).build();

        walletRepository.saveAndFlush(wallet);
        WalletUpdatePayLoad walletUpdatePayLoad=WalletUpdatePayLoad.builder().username(username)
                        .transactionType(TransactionType.CREDIT)
                                .transactionAmount(100.0)
                                        .totalAmount(100.0)
                                                .build();
        walletUpdatePayLoadKafkaTemplate.send("wallet_updated",username,walletUpdatePayLoad);
    }
    public Wallet fetchWalletDetail(String userName){
        return walletRepository.findByUserNameIgnoreCase(userName);
    }

    @Transactional(dontRollbackOn={InsufficientAmountException.class} )
    public void processTransaction(TransactionInitPayload transactionInitPayload){
        //sender and receiver being checked they exit or not in db
        Wallet senderWallet=walletRepository.findByUserNameIgnoreCase(transactionInitPayload.getSender());
        Wallet receiverWallet=walletRepository.findByUserNameIgnoreCase(transactionInitPayload.getReceiver());

        // amount being checked in the wallet of the sender in enough or not
        if(senderWallet.getAmount()>=transactionInitPayload.getAmount()){
            // processing transaction
            senderWallet.setAmount(senderWallet.getAmount()-transactionInitPayload.getAmount());
            receiverWallet.setAmount(receiverWallet.getAmount()+transactionInitPayload.getAmount());
            WalletUpdatePayLoad sendWalletUpdatePayLoad=WalletUpdatePayLoad.builder().username(transactionInitPayload.getSender())
                    .transactionType(TransactionType.DEBIT)
                    .transactionAmount(transactionInitPayload.getAmount())
                    .totalAmount(senderWallet.getAmount())
                    .build();
            WalletUpdatePayLoad receiverWalletUpdatePayLoad=WalletUpdatePayLoad.builder().username(transactionInitPayload.getReceiver())
                    .transactionType(TransactionType.CREDIT)
                    .transactionAmount(transactionInitPayload.getAmount())
                    .totalAmount(receiverWallet.getAmount())
                    .build();

            // push the wallet update event to kafka to notify users regards to transaction
            publishWalletUpdateEventToKafka(sendWalletUpdatePayLoad, transactionInitPayload.getSender());
            publishWalletUpdateEventToKafka(receiverWalletUpdatePayLoad, transactionInitPayload.getReceiver());
            TransactionCompletedPayload transactionCompletedPayload=TransactionCompletedPayload.builder()
                            .transactionId(transactionInitPayload.getTransactionId())
                                    .sender(transactionInitPayload.getSender())
                                            .status("COMPLETED").build();
            publishTransactionCompletedEventToKafka(transactionCompletedPayload,transactionInitPayload.getSender());
        } else{
            // payment failed notification pushed to kafka
            TransactionCompletedPayload transactionCompletedPayload=TransactionCompletedPayload.builder()
                    .transactionId(transactionInitPayload.getTransactionId())
                    .sender(transactionInitPayload.getSender())
                    .status("FAILED").build();
            publishTransactionCompletedEventToKafka(transactionCompletedPayload,transactionInitPayload.getSender());
            throw new InsufficientAmountException("Insufficient amount in wallet to perform this transaction");
        }
    }

    private void publishWalletUpdateEventToKafka(WalletUpdatePayLoad walletUpdatePayLoad,String username){
        walletUpdatePayLoadKafkaTemplate.send("wallet_updated",username,walletUpdatePayLoad);
    }
    private void publishTransactionCompletedEventToKafka(TransactionCompletedPayload transactionCompletedPayload, String username){
        transactionCompletedPayloadKafkaTemplate.send("txn_completed",username,transactionCompletedPayload);
    }



    @Transactional
    public String createPaymentLink(WalletCreditDto walletCreditDto) throws RazorpayException {

        WalletCredit walletCredit=WalletCredit.builder().userName(walletCreditDto.userName())
                .contactNumber(walletCreditDto.contactNumber())
                .name(walletCreditDto.name())
                .amount(walletCreditDto.amount())
                .paymentStatus(PaymentStatus.CREATED)
                .build();
        walletCredit=walletCreditRepository.save(walletCredit);


        RazorpayClient razorpayClient=new RazorpayClient(environment.getProperty("razorpay.merchant-id"),environment.getProperty("razorpay.merchant-secret"));
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount",(walletCredit.getAmount().intValue())*100+(walletCredit.getAmount()-walletCredit.getAmount().intValue())*100);
        paymentLinkRequest.put("currency","INR");
        paymentLinkRequest.put("accept_partial",false);
//        paymentLinkRequest.put("first_min_partial_amount",100);
        long currentTimestamp = Instant.now().getEpochSecond();

        // Add 20 minutes to the current time
        Instant newTimestamp = Instant.ofEpochSecond(currentTimestamp).plus(Duration.ofMinutes(20));

        paymentLinkRequest.put("expire_by",newTimestamp.getEpochSecond());
        paymentLinkRequest.put("reference_id",walletCredit.getWalletCreditId().toString());
        paymentLinkRequest.put("description","Adding amount to wallet");
        JSONObject customer = new JSONObject();
        customer.put("name",walletCredit.getName());
        customer.put("contact", walletCredit.getContactNumber());
        customer.put("email",walletCredit.getUserName());
        paymentLinkRequest.put("customer",customer);
        JSONObject notify = new JSONObject();
        notify.put("sms",false);
        notify.put("email",false);
        paymentLinkRequest.put("notify",notify);
        paymentLinkRequest.put("reminder_enable",true);
        JSONObject notes = new JSONObject();
        notes.put("policy_name","E-wallet");
        paymentLinkRequest.put("notes",notes);
        paymentLinkRequest.put("callback_url","http://localhost:7072/api/wallet-service/payment-status-capture");
        paymentLinkRequest.put("callback_method","get");

        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
        walletCredit.setRazorpayPaymentLinkId(payment.get("id"));
        return payment.get("short_url");

    }

    @Transactional
    public PaymentStatus verifyPayment( String razorpay_payment_id,  String razorpay_payment_link_id,  String razorpay_payment_link_reference_id,  String razorpay_payment_link_status,  String  razorpay_signature) throws RazorpayException {

        String secret=environment.getProperty("razorpay.merchant-secret");
        RazorpayClient razorpayClient=new RazorpayClient(environment.getProperty("razorpay.merchant-id"),secret);
      WalletCredit walletCredit=  walletCreditRepository.findById(UUID.fromString(razorpay_payment_link_reference_id)).get();
        if(walletCredit==null) {
            return PaymentStatus.INVALID;
        }

        JSONObject options = new JSONObject();
        options.put("payment_link_reference_id", walletCredit.getWalletCreditId().toString());
        options.put("razorpay_payment_id", razorpay_payment_id);
        options.put("payment_link_status", razorpay_payment_link_status);
        options.put("payment_link_id", walletCredit.getRazorpayPaymentLinkId());
        options.put("razorpay_signature", razorpay_signature);

        assert secret != null;
        boolean status =  Utils.verifyPaymentLink(options, secret);

        if(status) {
            PaymentStatus paymentStatus=stringPaymentStatusHashMap.get(razorpay_payment_link_status);
            if(PaymentStatus.PAID.equals(paymentStatus)) {
              Wallet wallet=  walletRepository.findByUserNameIgnoreCase(walletCredit.getUserName());
              wallet.setAmount(wallet.getAmount()+walletCredit.getAmount());
                WalletUpdatePayLoad walletUpdatePayLoad=WalletUpdatePayLoad.builder().username(walletCredit.getUserName())
                        .transactionType(TransactionType.CREDIT)
                        .transactionAmount(walletCredit.getAmount())
                        .totalAmount(wallet.getAmount())
                        .build();
              publishWalletUpdateEventToKafka(walletUpdatePayLoad,walletCredit.getUserName());
            }
           return paymentStatus;
        } else {
            return PaymentStatus.INVALID;
        }
    }
}
