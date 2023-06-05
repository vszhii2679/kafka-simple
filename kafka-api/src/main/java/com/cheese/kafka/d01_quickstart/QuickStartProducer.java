package com.cheese.kafka.d01_quickstart;

import com.cheese.kafka.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 快速开始
 * kafka生产者
 *
 * @author sobann
 */
public class QuickStartProducer {
    public static void main(String[] args) {
        // 加载kafka.properties
        Properties kafkaProperties = KafkaConfig.getKafkaProperties();

        Properties props = new Properties();
        // 设置接入点，请通过控制台获取对应Topic的接入点
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getProperty("bootstrap.servers"));
        // Kafka消息的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 请求的最长等待时间
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        // producer.flush会阻塞，如果对性能要求高，配置linger.ms 大的值 以及batch-size设置批量的大小 默认的批量推送的大小
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // 设置客户端内部重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        // 设置客户端内部重试间隔
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);
        // 构造Producer对象，注意，该对象是线程安全的，一般来说，一个进程内一个Producer对象即可；
        // 如果想提高性能，可以多构造几个对象，但不要太多，最好不要超过5个
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        //构造一个Kafka消息
//        String topic = kafkaProperties.getProperty("topic");
        String topic = kafkaProperties.getProperty("quick_start_topic");
        String value = "this is the message's value"; //消息的内容

        try {
            //批量获取 futures 可以加快速度, 但注意，批量不要太大
            List<Future<RecordMetadata>> futures = new ArrayList<>(128);
            for (int i =0; i < 5; i++) {
                //发送消息，并获得一个Future对象
                ProducerRecord<String, String> kafkaMessage =  new ProducerRecord<>(topic, value + ": " + i);
                Future<RecordMetadata> metadataFuture = producer.send(kafkaMessage);
                futures.add(metadataFuture);
            }
            producer.flush();
            for (Future<RecordMetadata> future: futures) {
                //同步获得Future对象的结果
                try {
                    RecordMetadata recordMetadata = future.get();
                    System.out.println("Produce ok:" + recordMetadata.toString());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Exception e) {
            //客户端内部重试之后，仍然发送失败，业务要应对此类错误
            System.out.println("error occurred");
            e.printStackTrace();
        }
        producer.close();
    }
}
