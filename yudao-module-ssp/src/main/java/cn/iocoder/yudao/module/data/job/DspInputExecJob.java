package cn.iocoder.yudao.module.data.job;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.module.data.dal.mysql.inputexec.InputExecMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.company.CompanyMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * DSP数据导入执行定时任务
 *
 * @author 芋道源码
 */
@Component
public class DspInputExecJob {

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private InputExecMapper inputExecMapper;

    /**
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 09 16 * * ?")
    public void execute() {
        try {
            // 设置租户ID
            TenantContextHolder.setTenantId(1L);

            // 查询所有未删除的公司
            List<CompanyDO> companies = companyMapper.selectList();

            if (companies.isEmpty()) {
                System.out.println("没有找到公司数据");
                return;
            }

            int successCount = 0;
            LocalDateTime now = LocalDateTime.now();

            // 为每个公司创建一条导入记录
            for (CompanyDO company : companies) {
                InputExecDO inputExec = new InputExecDO();
                inputExec.setCompanyId(company.getId());
                inputExec.setTables(0L); // 每个公司创建一条记录
                inputExec.setInputTime(new Date());

                inputExecMapper.insert(inputExec);
                successCount++;
            }

            System.out.println(String.format("成功为 %d 个公司创建导入记录", successCount));
        } finally {
            // 清理租户上下文
            TenantContextHolder.clear();
        }
    }

}