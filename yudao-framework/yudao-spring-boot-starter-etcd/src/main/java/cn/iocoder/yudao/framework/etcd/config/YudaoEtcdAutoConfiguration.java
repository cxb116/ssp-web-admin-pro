package cn.iocoder.yudao.framework.etcd.config;

import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Etcd 自动配置类
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(EtcdProperties.class)
@ConditionalOnProperty(prefix = "yudao.etcd", name = "enabled", havingValue = "true", matchIfMissing = true)
public class YudaoEtcdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Client etcdClient(EtcdProperties properties) {
        try {
            ClientBuilder builder = Client.builder()
                    .endpoints(properties.getEndpoints().split(","));

            // 设置超时时间
            if (properties.getConnectTimeout() != null) {
                builder.connectTimeoutMillis(properties.getConnectTimeout());
            }
            if (properties.getRequestTimeout() != null) {
                builder.requestTimeoutMillis(properties.getRequestTimeout());
            }

            // 设置用户名密码
            if (properties.getUsername() != null && !properties.getUsername().isEmpty()) {
                builder.user(io.etcd.jetcd.ByteSequence.from(properties.getUsername()))
                      .password(io.etcd.jetcd.ByteSequence.from(properties.getPassword()));
            }

            // TLS 配置
            if (properties.getTls() != null && properties.getTls()) {
                // TODO: 配置 TLS
                log.warn("Etcd TLS 配置暂未实现，请手动配置");
            }

            Client client = builder.build();
            log.info("Etcd 客户端初始化成功，endpoints: {}", properties.getEndpoints());
            return client;
        } catch (Exception e) {
            log.error("Etcd 客户端初始化失败", e);
            throw new RuntimeException("Etcd 客户端初始化失败", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdClient etcdTemplate(Client client) {
        log.info("EtcdTemplate 初始化成功");
        return new EtcdClient(client);
    }

}