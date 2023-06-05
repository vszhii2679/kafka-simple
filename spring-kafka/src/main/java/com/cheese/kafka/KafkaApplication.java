package com.cheese.kafka;

import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * 入口类
 *
 * @author sobann
 */
@SuppressWarnings("all")
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        KafkaTemplate<String, String> kafkaTemplate = applicationContext.getBean(KafkaTemplate.class);

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("spring-kafka-topic", "hello spring kafka ~");
        // flush方法会在提交消息到kafka前阻塞线程，可以通过配置linger.ms以及batch.size完成消息推送
//        kafkaTemplate.flush();
        // kafka异步回调获取消息是否成功发送到broker
        future.addCallback(new ListenableFutureCallback() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("消息发送成功 :" + result.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("发送异常");
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
