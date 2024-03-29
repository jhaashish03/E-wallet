package com.ewallet.walletservice.configurations.kafkaConfigs;

import com.ewallet.walletservice.kafka.payloads.TransactionCompletedPayload;
import com.ewallet.walletservice.kafka.payloads.WalletUpdatePayLoad;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import jakarta.validation.Valid;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootStrapServers;

    @Bean("walletUpdatedProducerFactory")
    public ProducerFactory<String,WalletUpdatePayLoad> producerFactory(){
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServers);
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String,WalletUpdatePayLoad>(map);
    }

    @Bean("walletUpdatedProducerTemplate")
    public KafkaTemplate<String, WalletUpdatePayLoad> getWalletProducerTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
    @Bean("transactionCompletedProducerFactory")
    public ProducerFactory<String, TransactionCompletedPayload> TransactionCompletedPayloadProducerFactory(){
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServers);
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String,TransactionCompletedPayload>(map);
    }

    @Bean("transactionCompletedTemplate")
    public KafkaTemplate<String, TransactionCompletedPayload> getTransactionCompletedPayloadTemplate(){
        return new KafkaTemplate<>(TransactionCompletedPayloadProducerFactory());
    }
}
