# DSP API Etcd 数据对接文档

## 概述

DSP API 使用 etcd 作为配置中心，实时订阅和更新 DSP 相关的业务配置数据。本文档说明如何向 etcd 中写入数据以供 DSP API 使用。

## 配置说明

### Etcd 连接配置

在配置文件中设置 etcd 连接信息（config.yaml）：

```yaml
etcd: "127.0.0.1:2379"
etcdPrefix: "/dsp/config"
```

- `etcd`: etcd 服务器地址
- `etcdPrefix`: 数据前缀，默认为 `/dsp/config`

## Etcd Key 格式

所有 etcd key 遵循统一格式：

```
{etcdPrefix}/{dataType}/{id}
```

### 示例

- `/dsp/config/dsp/1` - DSP 广告位配置（ID=1）
- `/dsp/config/company/1` - DSP 公司配置（ID=1）
- `/dsp/config/launch/1` - 媒体预算关联配置（ID=1）
- `/dsp/config/sspslot/1` - SSP 广告位配置（ID=1）

## 支持的数据类型

| 数据类型 | Key 前缀 | 说明 | 对应结构体 |
|---------|---------|------|-----------|
| dsp | `/dsp/config/dsp/` | DSP 广告位配置 | DspSlotInfo |
| company | `/dsp/config/company/` | DSP 公司配置 | DspCompany |
| launch | `/dsp/config/launch/` | 媒体预算关联配置 | DspLaunch |
| sspslot | `/dsp/config/sspslot/` | SSP 广告位配置 | SspSlotInfo |

## 事件类型

DSP API 订阅以下两种事件：

### 1. PUT 事件（新增/更新数据）

- **操作**: 向 etcd 写入或更新数据
- **Key 格式**: `{etcdPrefix}/{dataType}/{id}`
- **Value 格式**: JSON 字符串

#### 示例命令（使用 etcdctl）

```bash
# 添加/更新 DSP 广告位配置
etcdctl put /dsp/config/dsp/1 '{
  "id": 1,
  "name": "测试广告位",
  "dsp_slot_code": "DSP_TEST_001",
  "product_name": "测试产品",
  "company_id": 1,
  "product_id": 1,
  "ad_type_id": 1,
  "os_type": 1,
  "dsp_app_key": "app_key_123",
  "dsp_app_id": "app_id_123",
  "dsp_app_pkg": "com.test.app",
  "dsp_app_ver": "1.0.0",
  "dsp_app_store_ver": "1.0.0",
  "price_encrypt_key": "encrypt_key_123",
  "dsp_app_store_link": "https://example.com",
  "dsp_pay_type": 1,
  "dsp_deal_ratio": 0.7
}'

# 添加/更新 DSP 公司配置
etcdctl put /dsp/config/company/1 '{
  "id": 1,
  "name": "测试公司",
  "dsp_code": 1001,
  "url": "https://api.example.com",
  "method": "POST",
  "timeout": 3000000000
}'

# 添加/更新 媒体预算关联配置
etcdctl put /dsp/config/launch/1 '{
  "id": 1,
  "ssp_slot_id": 1,
  "dsp_slot_id": 1,
  "traffic_weight": 100,
  "launch_strategy": 1,
  "floor_price": 0.000100,
  "ip_limit": 1000,
  "track_schwarz": "track_string",
  "log_capture_at": 1,
  "launch_time": 1234567890,
  "crowd_direction": 0,
  "region_direction": 0,
  "brand_direction": 0,
  "indexs": 1
}'

# 添加/更新 SSP 广告位配置
etcdctl put /dsp/config/sspslot/1 '{
  "id": 1,
  "ad_scene": 1,
  "ssp_pay_type": 1,
  "ssp_deal_ratio": 0.6,
  "os_type": 1,
  "app_id": 1
}'
```

### 2. DELETE 事件（删除数据）

- **操作**: 从 etcd 删除数据
- **Key 格式**: `{etcdPrefix}/{dataType}/{id}`

#### 示例命令

```bash
# 删除 DSP 广告位配置
etcdctl del /dsp/config/dsp/1

# 删除 DSP 公司配置
etcdctl del /dsp/config/company/1

# 删除 媒体预算关联配置
etcdctl del /dsp/config/launch/1

# 删除 SSP 广告位配置
etcdctl del /dsp/config/sspslot/1
```

## 数据结构定义

### DspSlotInfo（DSP 广告位）

```json
{
  "id": "int64 (必填)",
  "name": "string (广告位名称)",
  "dsp_slot_code": "string (广告位编码，必填)",
  "product_name": "string (产品名称)",
  "company_id": "int64 (所属公司ID)",
  "product_id": "int64 (产品ID)",
  "ad_type_id": "int64 (广告类型ID)",
  "os_type": "int64 (操作系统类型)",
  "dsp_app_key": "string (应用Key)",
  "dsp_app_id": "string (应用ID)",
  "dsp_app_pkg": "string (应用包名)",
  "dsp_app_ver": "string (应用版本)",
  "dsp_app_store_ver": "string (商店版本)",
  "price_encrypt_key": "string (价格加密密钥)",
  "dsp_app_store_link": "string (商店链接)",
  "dsp_pay_type": "int64 (付费类型)",
  "dsp_deal_ratio": "float64 (分成比例)"
}
```

### DspCompany（DSP 公司）

```json
{
  "id": "int64 (必填)",
  "name": "string (公司名称)",
  "dsp_code": "int64 (DSP编码)",
  "url": "string (API地址)",
  "method": "string (HTTP方法，如 POST)",
  "timeout": "int64 (超时时间，纳秒)"
}
```

### DspLaunch（媒体预算关联）

```json
{
  "id": "int64 (必填)",
  "ssp_slot_id": "int64 (SSP广告位ID，必填)",
  "dsp_slot_id": "int64 (DSP广告位ID)",
  "traffic_weight": "int64 (流量权重)",
  "launch_strategy": "int64 (投放策略)",
  "floor_price": "float64 (底价)",
  "ip_limit": "int64 (IP限制)",
  "track_schwarz": "string (追踪字符串)",
  "log_capture_at": "int64 (日志捕获位置)",
  "launch_time": "int64 (投放时间)",
  "crowd_direction": "int64 (人群定向)",
  "region_direction": "int64 (地域定向)",
  "brand_direction": "int64 (品牌定向)",
  "indexs": "int64 (流量分组索引，必填)"
}
```

### SspSlotInfo（SSP 广告位）

```json
{
  "id": "int (必填)",
  "ad_scene": "int64 (广告场景)",
  "ssp_pay_type": "int64 (SSP付费类型)",
  "ssp_deal_ratio": "float64 (SSP分成比例)",
  "os_type": "int64 (操作系统类型)",
  "app_id": "int64 (应用ID)"
}
```

## 初始化流程

DSP API 启动时会执行以下初始化流程：

1. **从数据库加载全量数据**（AllDbData 方法）
   - 查询所有 dsp_slot_info 数据
   - 查询所有 dsp_company 数据
   - 查询所有 dsp_launch 数据
   - 查询所有 ssp_slot_info 数据
   - 将数据加载到内存缓存

2. **启动 etcd 监听**（DspSlotInfoWatchChan 方法）
   - 监听 `{etcdPrefix}/` 前缀下的所有 key 变化
   - 实时更新内存缓存

## 注意事项

### 1. 数据完整性

- DSP API 启动时会先从数据库加载全量数据，etcd 用于增量更新
- 确保 ID 字段正确设置，PUT 和 DELETE 操作都依赖 ID
- 建议先完成数据库写入，再通过 etcd 同步更新

### 2. 权重分配逻辑

- `DspLaunch` 中的 `indexs` 字段用于流量分组
- 相同 `indexs` 的 DspLaunch 属于同一组流量
- `traffic_weight` 用于组间流量分配（加权随机算法）
- 删除 DspLaunch 时需注意关联的流量分配

### 3. 错误处理

- JSON 解析失败时，DSP API 会记录错误日志并忽略该更新
- ID 解析失败时默认使用 0
- 不支持的数据类型会被忽略并记录警告

### 4. 并发控制

- DSP API 使用读写锁（RWMutex）保护内存数据
- 读操作使用读锁，写操作使用写锁
- 确保高并发场景下的数据一致性

## 监控和日志

DSP API 会在以下情况记录日志：

- 启动时加载的数据量
- 接收到 etcd 事件时记录 key 和事件类型
- 数据更新成功时记录相关信息
- 数据解析失败时记录错误信息

## 批量操作示例

### 批量写入脚本（Go）

```go
package main

import (
	"context"
	"encoding/json"
	"fmt"
	"go.etcd.io/etcd/client/v3"
)

func main() {
	cli, err := clientv3.New(clientv3.Config{
		Endpoints:   []string{"127.0.0.1:2379"},
		DialTimeout: 5 * time.Second,
	})
	if err != nil {
		panic(err)
	}
	defer cli.Close()

	ctx := context.Background()

	// 批量写入 DSP 广告位
	dspData := DspSlotInfo{
		Id:          1,
		Name:        "测试广告位",
		DspSlotCode: "DSP_TEST_001",
		CompanyId:   1,
	}
	dspBytes, _ := json.Marshal(dspData)
	cli.Put(ctx, "/dsp/config/dsp/1", string(dspBytes))

	// 批量写入 DSP 公司
	companyData := DspCompany{
		Id:      1,
		Name:    "测试公司",
		DspCode: 1001,
	}
	companyBytes, _ := json.Marshal(companyData)
	cli.Put(ctx, "/dsp/config/company/1", string(companyBytes))
}
```

## 故障排查

### 1. 数据未生效

- 检查 etcd key 格式是否正确：`{etcdPrefix}/{dataType}/{id}`
- 检查 JSON 格式是否正确
- 查看 DSP API 日志确认是否接收到事件

### 2. ID 冲突

- 确保每个数据类型的 ID 唯一
- 删除数据时使用正确的 ID

### 3. 连接问题

- 检查 etcd 服务是否正常运行
- 检查网络连接和防火墙设置
- 验证 etcd 地址配置是否正确

## 相关文件

- etcd watch 实现：`dsp-api/impl/LoopSubscribeMessage.go:276-456`
- 数据结构定义：同文件
- 配置文件：`dsp-api/config.yaml`

## 版本信息

- 文档版本：1.0.0
- 最后更新：2026-07-02