package com.cheese.kafka.d01_quickstart;

import com.cheese.kafka.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 快速开始
 * kafka消费者
 *
 * @author sobann
 */
public class QuickStartConsumer {

    public static void main(String[] args) {
        //加载kafka.properties
        Properties kafkaProperties = KafkaConfig.getKafkaProperties();
        Properties props = new Properties();
        //设置接入点，请通过控制台获取对应Topic的接入点
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getProperty("bootstrap.servers"));
        //可更加实际拉去数据和客户的版本等设置此值，默认30s
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        //每次poll的最大数量
        //注意该值不要改得太大，如果poll太多数据，而不能在下次poll之前消费完，则会触发一次负载均衡，产生卡顿
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        //是否开启自动提交offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //消息的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //当前消费实例所属的消费组，请在控制台申请之后填写
        //属于同一个组的消费实例，会负载消费消息
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getProperty("group.id"));
        //构造消息对象，也即生成一个消费实例
        KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        //设置消费组订阅的Topic，可以订阅多个
        //如果GROUP_ID_CONFIG是一样，则订阅的Topic也建议设置成一样
        List<String> subscribedTopics = new ArrayList<String>();
        //如果需要订阅多个Topic，则在这里add进去即可
        //每个Topic需要先在控制台进行创建
//        String topicStr = kafkaProperties.getProperty("topic");
        String topicStr = kafkaProperties.getProperty("quick_start_topic");
        String[] topics = topicStr.split(",");
        for (String topic : topics) {
            subscribedTopics.add(topic.trim());
        }
        //订阅
        consumer.subscribe(subscribedTopics);

        try {

//            while (true) {

            ConsumerRecords<String, String> records = consumer.poll(1000);
            //必须在下次poll之前消费完这些数据, 且总耗时不得超过SESSION_TIMEOUT_MS_CONFIG
            //建议开一个单独的线程池来消费消息，然后异步返回结果

            // 处理接收到的消息
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Consume partition:%d offset:%d%n, message is %s%n", record.partition(), record.offset(), record.value());
            }
            //异步提交
//                consumer.commitAsync();
            //同步提交 提交失败会重试 直到提交成功
            consumer.commitSync();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
