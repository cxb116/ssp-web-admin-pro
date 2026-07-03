package cn.iocoder.yudao.framework.etcd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Etcd 配置属性
 */
@Data
@ConfigurationProperties(prefix = "yudao.etcd")
public class EtcdProperties {

    /**
     * 是否启用 etcd，默认为 true
     */
    private Boolean enabled = true;

    /**
     * etcd 服务器地址，多个地址用逗号分隔
     * 例如: http://localhost:2379,http://localhost:2380
     */
    private String endpoints = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接超时时间（毫秒），默认 5000
     */
    private Integer connectTimeout = 5000;

    /**
     * 请求超时时间（毫秒），默认 30000
     */
    private Integer requestTimeout = 30000;

    /**
     * 是否使用 TLS，默认为 false
     */
    private Boolean tls = false;

    /**
     * TLS CA 证书路径
     */
    private String tlsCaCertPath;

    /**
     * TLS 客户端证书路径
     */
    private String tlsClientCertPath;

    /**
     * TLS 客户端密钥路径
     */
    private String tlsClientKeyPath;

}