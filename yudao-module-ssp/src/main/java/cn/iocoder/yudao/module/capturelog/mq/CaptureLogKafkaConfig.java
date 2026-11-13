package cn.iocoder.yudao.module.capturelog.mq;

import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * 捕获日志 Kafka 批量监听容器工厂
 *
 * Spring Boot 2.7 尚无 spring.kafka.listener.type=batch 配置（2.8+ 才支持），
 * 批量监听必须自定义 ContainerFactory 并 setBatchListener(true)。
 * 写法与 Spring Boot 官方文档"自定义监听容器工厂"一致（通配符泛型 + configurer）。
 * broker/反序列化/组id/max-poll-records 等仍沿用 application.yaml 中
 * spring.kafka.consumer.* 的自动配置（configurer + consumerFactory 注入继承）。
 * 运行时值为 String 反序列化产物，监听方法按 List<ConsumerRecord<String,String>> 接收（泛型擦除安全）。
 */
@Configuration
public class CaptureLogKafkaConfig {

    @Bean("captureLogBatchFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> captureLogBatchFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory) {
        // 泛型须为 <Object,Object>：SB2.7 的 configurer.configure 签名如此（javap 实测）；
        // 运行时值为 String 反序列化产物，监听方法按 List<ConsumerRecord<String,String>> 接收（泛型擦除安全）
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.setBatchListener(true); // 每次回调交付一批 List<ConsumerRecord>
        return factory;
    }

}
