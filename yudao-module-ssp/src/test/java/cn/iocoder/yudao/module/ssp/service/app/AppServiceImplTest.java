package cn.iocoder.yudao.module.ssp.service.app;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.app.AppMapper;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppServiceImplTest {

    @InjectMocks
    private AppServiceImpl appService;

    @Mock
    private AppMapper appMapper;
    @Mock
    private SspSlotInfoMapper slotInfoMapper;

    @Test
    void deleteApp_rejectsWhenAppHasSspSlots() {
        ReflectionTestUtils.setField(appService, "slotInfoMapper", slotInfoMapper);
        when(appMapper.selectAppById(1L)).thenReturn(new AppDO());
        when(slotInfoMapper.selectCount(any())).thenReturn(1L);

        assertThrows(ServiceException.class, () -> appService.deleteApp(1L));

        verify(appMapper, never()).deleteById(1L);
    }
}
