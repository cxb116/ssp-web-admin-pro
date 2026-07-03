package cn.iocoder.yudao.framework.etcd.config;

import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Etcd auto configuration.
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(EtcdProperties.class)
@ConditionalOnProperty(prefix = "yudao.etcd", name = "enabled", havingValue = "true", matchIfMissing = true)
public class YudaoEtcdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Client jetcdClient(EtcdProperties properties) {
        try {
            ClientBuilder builder = Client.builder()
                    .endpoints(properties.getEndpoints().split(","));

            if (properties.getConnectTimeout() != null) {
                builder.connectTimeout(Duration.ofMillis(properties.getConnectTimeout()));
            }

            if (properties.getUsername() != null && !properties.getUsername().isEmpty()) {
                builder.user(ByteSequence.from(properties.getUsername(), StandardCharsets.UTF_8))
                        .password(ByteSequence.from(properties.getPassword(), StandardCharsets.UTF_8));
            }

            if (properties.getTls() != null && properties.getTls()) {
                log.warn("Etcd TLS configuration is not implemented yet, please configure it manually");
            }

            Client client = builder.build();
            log.info("Etcd client initialized, endpoints: {}", properties.getEndpoints());
            return client;
        } catch (Exception e) {
            log.error("Etcd client initialization failed", e);
            throw new RuntimeException("Etcd client initialization failed", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdClient etcdClient(Client jetcdClient) {
        log.info("EtcdClient initialized");
        return new EtcdClient(jetcdClient);
    }

}
