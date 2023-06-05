package com.cheese.kafka.config.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author sobann
 */
@Component
public class KafkaMessageListener {

    @KafkaListener(topics = "spring-kafka-topic"
//            , containerFactory = "kafkaManualAckListenerContainerFactory"
    )
    public void onMessage(String message, Acknowledgment ack) {
        System.out.println("receiver message = " + message);
        try {
            //todo business

            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
