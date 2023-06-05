package com.cheese.kafka.d02_pubsub;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 通过话题，多个消费者只有一个能消费
 *
 * @author sobann
 */
public class PubSubExample {
    private static final String TOPIC_NAME = "my-topic";

    public static void main(String[] args) {
        // 创建生产者
        Producer<String, String> producer = createProducer();

        // 创建消费者组
        String groupId = "my-consumer-group";
        List<Consumer<String, String>> consumers = createConsumers(groupId);

        // 生产者发布消息
        String message = "Hello Kafka!";
        producer.send(new ProducerRecord<>(TOPIC_NAME, message));

        // 消费者订阅主题
        for (Consumer<String, String> consumer : consumers) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        }

        // 消费者进行轮询
        while (true) {
            for (Consumer<String, String> consumer : consumers) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                // 处理收到的消息
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(consumer.toString() + " -- Received message: " + record.value());
                }
            }
        }
    }

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.18.116:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(props);
    }

    private static List<Consumer<String, String>> createConsumers(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.18.116:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        int numConsumers = 3; // 定义消费者数量
        List<Consumer<String, String>> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            consumers.add(new KafkaConsumer<>(props));
        }
        return consumers;
    }
}
