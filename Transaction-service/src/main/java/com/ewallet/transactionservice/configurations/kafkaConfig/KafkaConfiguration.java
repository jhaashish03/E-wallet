package com.ewallet.transactionservice.configurations.kafkaConfig;

import com.ewallet.transactionservice.kafka.payload.TransactionInitPayload;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootStrapServers;

    @Bean("transactionProducerFactory")
    public ProducerFactory<String, TransactionInitPayload> producerFactory(){
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServers);
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String,TransactionInitPayload>(map);
    }
    @Bean("transactionKafkaTemplate")
    public KafkaTemplate<String, TransactionInitPayload> getTransactionTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
