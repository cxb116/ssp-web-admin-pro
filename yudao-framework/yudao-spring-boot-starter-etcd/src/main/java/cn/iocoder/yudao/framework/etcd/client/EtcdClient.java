package cn.iocoder.yudao.framework.etcd.client;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Etcd 客户端工具类
 */
@Slf4j
public class EtcdClient {

    private final Client client;

    public EtcdClient(Client client) {
        this.client = client;
    }

    /**
     * 字符串转 ByteSequence
     */
    private ByteSequence toByteSequence(String value) {
        return ByteSequence.from(value, StandardCharsets.UTF_8);
    }

    /**
     * ByteSequence 转字符串
     */
    private String toString(ByteSequence byteSequence) {
        return byteSequence.toString(StandardCharsets.UTF_8);
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        try {
            GetResponse response = client.getKVClient()
                    .get(toByteSequence(key))
                    .get();
            List<KeyValue> kvs = response.getKvs();
            if (kvs.isEmpty()) {
                return null;
            }
            return toString(kvs.get(0).getValue());
        } catch (Exception e) {
            log.error("Etcd get 失败, key: {}", key, e);
            throw new RuntimeException("Etcd get 失败", e);
        }
    }

    /**
     * 获取值（异步）
     *
     * @param key 键
     * @return CompletableFuture
     */
    public CompletableFuture<String> getAsync(String key) {
        return client.getKVClient()
                .get(toByteSequence(key))
                .thenApply(response -> {
                    List<KeyValue> kvs = response.getKvs();
                    if (kvs.isEmpty()) {
                        return null;
                    }
                    return toString(kvs.get(0).getValue());
                })
                .exceptionally(e -> {
                    log.error("Etcd getAsync 失败, key: {}", key, e);
                    throw new RuntimeException("Etcd getAsync 失败", e);
                });
    }

    /**
     * 获取前缀匹配的所有键值对
     *
     * @param prefix 前缀
     * @return 键值对列表
     */
    public List<KeyValue> getByPrefix(String prefix) {
        try {
            GetResponse response = client.getKVClient()
                    .get(toByteSequence(prefix), GetOption.newBuilder().withPrefix(toByteSequence(prefix)).build())
                    .get();
            return response.getKvs();
        } catch (Exception e) {
            log.error("Etcd getByPrefix 失败, prefix: {}", prefix, e);
            throw new RuntimeException("Etcd getByPrefix 失败", e);
        }
    }

    /**
     * 获取前缀匹配的所有键值对（异步）
     *
     * @param prefix 前缀
     * @return CompletableFuture
     */
    public CompletableFuture<List<KeyValue>> getByPrefixAsync(String prefix) {
        return client.getKVClient()
                .get(toByteSequence(prefix), GetOption.newBuilder().withPrefix(toByteSequence(prefix)).build())
                .thenApply(GetResponse::getKvs)
                .exceptionally(e -> {
                    log.error("Etcd getByPrefixAsync 失败, prefix: {}", prefix, e);
                    throw new RuntimeException("Etcd getByPrefixAsync 失败", e);
                });
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, String value) {
        try {
            PutResponse response = client.getKVClient()
                    .put(toByteSequence(key), toByteSequence(value))
                    .get();
            log.debug("Etcd put 成功, key: {}, value: {}", key, value);
        } catch (Exception e) {
            log.error("Etcd put 失败, key: {}, value: {}", key, value, e);
            throw new RuntimeException("Etcd put 失败", e);
        }
    }

    /**
     * 设置值（异步）
     *
     * @param key   键
     * @param value 值
     * @return CompletableFuture
     */
    public CompletableFuture<PutResponse> putAsync(String key, String value) {
        return client.getKVClient()
                .put(toByteSequence(key), toByteSequence(value))
                .thenApply(response -> {
                    log.debug("Etcd putAsync 成功, key: {}, value: {}", key, value);
                    return response;
                })
                .exceptionally(e -> {
                    log.error("Etcd putAsync 失败, key: {}, value: {}", key, value, e);
                    throw new RuntimeException("Etcd putAsync 失败", e);
                });
    }

    /**
     * 设置值（带租约）
     *
     * @param key      键
     * @param value    值
     * @param leaseId  租约 ID
     */
    public void put(String key, String value, long leaseId) {
        try {
            PutResponse response = client.getKVClient()
                    .put(toByteSequence(key), toByteSequence(value),
                            PutOption.newBuilder().withLeaseId(leaseId).build())
                    .get();
            log.debug("Etcd put 成功（带租约）, key: {}, value: {}, leaseId: {}", key, value, leaseId);
        } catch (Exception e) {
            log.error("Etcd put 失败（带租约）, key: {}, value: {}, leaseId: {}", key, value, leaseId, e);
            throw new RuntimeException("Etcd put 失败", e);
        }
    }

    /**
     * 删除值
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            DeleteResponse response = client.getKVClient()
                    .delete(toByteSequence(key))
                    .get();
            log.debug("Etcd delete 成功, key: {}", key);
        } catch (Exception e) {
            log.error("Etcd delete 失败, key: {}", key, e);
            throw new RuntimeException("Etcd delete 失败", e);
        }
    }

    /**
     * 删除值（异步）
     *
     * @param key 键
     * @return CompletableFuture
     */
    public CompletableFuture<DeleteResponse> deleteAsync(String key) {
        return client.getKVClient()
                .delete(toByteSequence(key))
                .thenApply(response -> {
                    log.debug("Etcd deleteAsync 成功, key: {}", key);
                    return response;
                })
                .exceptionally(e -> {
                    log.error("Etcd deleteAsync 失败, key: {}", key, e);
                    throw new RuntimeException("Etcd deleteAsync 失败", e);
                });
    }

    /**
     * 删除前缀匹配的所有键值对
     *
     * @param prefix 前缀
     */
    public void deleteByPrefix(String prefix) {
        try {
            DeleteResponse response = client.getKVClient()
                    .delete(toByteSequence(prefix),
                            DeleteOption.newBuilder().withPrefix(toByteSequence(prefix)).build())
                    .get();
            log.debug("Etcd deleteByPrefix 成功, prefix: {}", prefix);
        } catch (Exception e) {
            log.error("Etcd deleteByPrefix 失败, prefix: {}", prefix, e);
            throw new RuntimeException("Etcd deleteByPrefix 失败", e);
        }
    }

    /**
     * 授予租约
     *
     * @param ttl 租约时间（秒）
     * @return 租约 ID
     */
    public long grantLease(long ttl) {
        try {
            return client.getLeaseClient()
                    .grant(ttl)
                    .get()
                    .getID();
        } catch (Exception e) {
            log.error("Etcd grantLease 失败, ttl: {}", ttl, e);
            throw new RuntimeException("Etcd grantLease 失败", e);
        }
    }

    /**
     * 授予租约（异步）
     *
     * @param ttl 租约时间（秒）
     * @return CompletableFuture
     */
    public CompletableFuture<Long> grantLeaseAsync(long ttl) {
        return client.getLeaseClient()
                .grant(ttl)
                .thenApply(response -> response.getID())
                .exceptionally(e -> {
                    log.error("Etcd grantLeaseAsync 失败, ttl: {}", ttl, e);
                    throw new RuntimeException("Etcd grantLeaseAsync 失败", e);
                });
    }

    /**
     * 撤销租约
     *
     * @param leaseId 租约 ID
     */
    public void revokeLease(long leaseId) {
        try {
            client.getLeaseClient()
                    .revoke(leaseId)
                    .get();
            log.debug("Etcd revokeLease 成功, leaseId: {}", leaseId);
        } catch (Exception e) {
            log.error("Etcd revokeLease 失败, leaseId: {}", leaseId, e);
            throw new RuntimeException("Etcd revokeLease 失败", e);
        }
    }

    /**
     * 保持租约存活
     *
     * @param leaseId 租约 ID
     * @return StreamObserver
     */
    public StreamObserver<LeaseKeepAliveResponse> keepAlive(long leaseId, StreamObserver<LeaseKeepAliveResponse> observer) {
        return client.getLeaseClient().keepAlive(leaseId, observer);
    }

    /**
     * 关闭客户端
     */
    public void close() {
        if (client != null) {
            client.close();
            log.info("Etcd 客户端已关闭");
        }
    }

}