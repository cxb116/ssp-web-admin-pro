# SSP / DSP 业务模块路径锚点

生成时间：2026-07-13

后端项目：`D:\javaPract\ssp-web-admin-pro`

前端项目：`D:\javaPract\ssp-ui`

前端实际命中业务代码主要在：`D:\javaPract\ssp-ui\apps\web-antd`

## 1. SSP 媒体管理 / ssp_media

业务要求：删除媒体时校验该媒体下是否存在 `ssp_app`，存在则禁止删除媒体。

后端接口前缀：`/ssp/media`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\media\MediaController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\media\MediaService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\media\MediaServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\mysql\media\MediaMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\media\MediaMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\dataobject\media\MediaDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\media\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\ssp\media\index.ts`
- 页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\media\index.vue`
- 表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\media\modules\form.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\media\data.ts`

改动锚点：

- 删除校验建议放在 `MediaServiceImpl.deleteMedia(Long id)` 和 `deleteMediaListByIds(List<Long> ids)`。
- 需要查询 `ssp_app.media_id`，可复用或扩展 `AppMapper`。

## 2. SSP 应用管理 / ssp_app

业务要求：一个媒体下多个 App。删除 App 时校验该 App 是否已被 `ssp_slot_info` 媒体广告位绑定，已绑定则禁止删除。

后端接口前缀：`/ssp/app`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\app\AppController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\app\AppService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\app\AppServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\mysql\app\AppMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\app\AppMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\dataobject\app\AppDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\app\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\ssp\app\index.ts`
- 页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\app\index.vue`
- 表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\app\modules\form.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\app\data.ts`

改动锚点：

- 删除校验建议放在 `AppServiceImpl.deleteApp(Long id)` 和 `deleteAppListByIds(List<Long> ids)`。
- 需要查询 `ssp_slot_info.app_id`，可扩展 `SspSlotInfoMapper`。

## 3. SSP 媒体广告位管理 / ssp_slot_info

业务要求：

- 一个 App 对多个媒体广告位。
- `enable = 2` 审核中时，把“通过”按钮改成“配置并通过”。
- 点击“配置并通过”后更新 `enable = 1`，按钮恢复为“配置”。
- 搜索区：下拉框支持输入搜索。
- 广告名称、内部广告名称改成下拉搜索。
- 远程下拉条数 1000。

后端接口前缀：`/ssp/slot-info`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\sspSlotInfo\SspSlotInfoController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\sspSlotInfo\SspSlotInfoService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\service\sspSlotInfo\SspSlotInfoServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\mysql\sspSlotInfo\SspSlotInfoMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\sspSlotInfo\SspSlotInfoMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\dal\dataobject\sspSlotInfo\SspSlotInfoDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\ssp\controller\admin\sspSlotInfo\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\ssp\sspSlotInfo\index.ts`
- 列表页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\sspSlotInfo\index.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\sspSlotInfo\data.ts`
- 新增/编辑表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\sspSlotInfo\modules\form.vue`
- 配置页：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\sspSlotInfo\config.vue`
- 路由：`D:\javaPract\ssp-ui\apps\web-antd\src\router\routes\modules\ssp.ts`

改动锚点：

- 按钮文案与跳转：`sspSlotInfo/index.vue` 的 `actions` 插槽，目前配置按钮固定为 `配置`。
- `enable = 2` 时改为 `配置并通过`，点击后可先调用 `updateSlotInfo({ ...row, enable: 1 })`，再跳转配置页或按业务要求只更新状态。
- 搜索区字段在 `sspSlotInfo/data.ts` 的 `useGridFormSchema()`。
- 当前 `name`、`nameAlise` 是 `Input`，需要改成 `ApiSelect` 或支持远程搜索的组件。
- 已有 `getSlotInfoOptions()` 调 `getSlotInfoPage({ pageNo: 1, pageSize: 1000 })`，可作为广告位名称下拉数据来源。
- 媒体、App 下拉已经是 `ApiSelect`，App 下拉已使用 `pageSize: 1000`。

## 4. DSP 预算公司 / dsp_company

业务说明：预算公司和预算产品有关联。

后端接口前缀：`/dsp/company`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\company\CompanyController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\company\CompanyService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\company\CompanyServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\mysql\company\CompanyMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\company\CompanyMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\dataobject\company\CompanyDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\company\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\dsp\company\index.ts`
- 页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\company\index.vue`
- 表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\company\modules\form.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\company\data.ts`

## 5. DSP 预算产品 / dsp_product

业务要求：删除预算产品时，如果已绑定预算公司/被业务引用，禁止删除。

后端接口前缀：`/dsp/product`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\product\ProductController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\product\ProductService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\product\ProductServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\mysql\product\ProductMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\product\ProductMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\dataobject\product\ProductDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\product\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\dsp\product\index.ts`
- 页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\product\index.vue`
- 表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\product\modules\form.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\product\data.ts`

改动锚点：

- 删除校验建议放在 `ProductServiceImpl.deleteProduct(Long id)` 和 `deleteProductListByIds(List<Long> ids)`。
- 当前 `ProductMapper.xml` 已经 join `dsp_company`，产品表自身有 `company_id`。
- 被业务引用主要可查 `dsp_slot_info.product_id`。
- 如需校验投放关系，可通过 `dsp_slot_info.id -> dsp_launch.dsp_slot_id` 间接判断。

## 6. DSP 预算广告位管理 / dsp_slot_info

业务要求：

- 搜索区：预算方广告位输入框改下拉。
- 产品、公司名称支持输入搜索。
- 下拉数据 1000 条。

后端接口前缀：`/dsp/slot-info`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\dspslotinfo\DspSlotInfoController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\dspslotinfo\DspSlotInfoService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\dspslotinfo\DspSlotInfoServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\mysql\dspslotinfo\DspSlotInfoMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\dspslotinfo\DspSlotInfoMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\dataobject\dspslotinfo\DspSlotInfoDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\dspslotinfo\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\dsp\dspslotinfo\index.ts`
- 列表页面：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\dspslotinfo\index.vue`
- 列表/搜索配置：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\dspslotinfo\data.ts`
- 新增/编辑表单：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\dspslotinfo\modules\form.vue`
- 配置页：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\dspslotinfo\config.vue`

改动锚点：

- 搜索区在 `dspslotinfo/data.ts` 的 `useGridFormSchema()`。
- 当前 `dspSlotCode` 是 `Input`，需要改成下拉搜索。
- `productId`、`companyId` 已是 `ApiSelect`，且 `getProductOptions()` / `getCompanyOptions()` 已使用 `pageSize: 1000`，也已配置 `showSearch: true` 和 `filterOption: false`。
- 预算广告位下拉可复用 `getSlotInfoPage({ pageNo: 1, pageSize: 1000 })` 生成 `dspSlotCode` 选项。

## 7. 预算与媒体绑定 / dsp_launch

业务说明：这是绑定关系表锚点，主要用于判断 `ssp_slot_info` 和 `dsp_slot_info` 是否已被投放关系引用。

后端接口前缀：`/dsp/launch`

后端路径：

- Controller：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\launch\LaunchController.java`
- Service：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\launch\LaunchService.java`
- ServiceImpl：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\service\launch\LaunchServiceImpl.java`
- Mapper：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\mysql\launch\LaunchMapper.java`
- Mapper XML：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\resources\mapper\launch\LaunchMapper.xml`
- DO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\dal\dataobject\launch\LaunchDO.java`
- VO：`D:\javaPract\ssp-web-admin-pro\yudao-module-ssp\src\main\java\cn\iocoder\yudao\module\dsp\controller\admin\launch\vo`

前端路径：

- API：`D:\javaPract\ssp-ui\apps\web-antd\src\api\dsp\launch\index.ts`
- SSP 广告位配置绑定页：`D:\javaPract\ssp-ui\apps\web-antd\src\views\ssp\sspSlotInfo\config.vue`
- DSP 广告位配置绑定页：`D:\javaPract\ssp-ui\apps\web-antd\src\views\dsp\dspslotinfo\config.vue`

已有后端查询锚点：

- `LaunchMapper.selectLaunchBySspSlotId(Long sspSlotId)`：判断 `ssp_slot_info` 是否被投放关系引用。
- `LaunchMapper.selectLaunchByDspSlotId(Long dspSlotId)`：判断 `dsp_slot_info` 是否被投放关系引用。
- `LaunchMapper.selectDspSlotInfoBySspSlotId(Long sspSlotId)`：根据媒体广告位查绑定的预算广告位。
- `LaunchMapper.selectSspSlotInfoByDspSlotId(Long dspSlotId)`：根据预算广告位查绑定的媒体广告位。
- `LaunchMapper.getLaunchSspSlotList(Long id)`：根据 `sspSlotId` 查绑定关系列表。

前端已有 API：

- `getLaunchSspSlotIdQuery(sspSlotId)` -> `/dsp/launch/sspslotid/{sspSlotId}`
- `getLaunchDspSlotIdQuery(dspSlotId)` -> `/dsp/launch/dspslotid/{dspSlotId}`
- `getLaunchSspSlotList(sspSlotId)` -> `/dsp/launch/sspslotid_group/{sspSlotId}`
- `createLaunch(data)` -> `/dsp/launch/create`
- `updateLaunch(data)` -> `/dsp/launch/update`
- `deleteLaunch(id)` -> `/dsp/launch/delete?id={id}`

## 8. 额外注意

- 后端代码里部分中文注释/Swagger 文案显示为乱码，但路径和接口映射不受影响。
- `D:\javaPract\ssp-ui` 是多应用前端仓库；本次业务模块命中的是 `apps\web-antd`。
- `apps\web-ele` 未命中这些 SSP/DSP 业务路径。
- 如果删除校验要批量删除也生效，单删和批删都要加校验。
- 建议后端禁止删除作为最终兜底，前端只做提示体验增强。
