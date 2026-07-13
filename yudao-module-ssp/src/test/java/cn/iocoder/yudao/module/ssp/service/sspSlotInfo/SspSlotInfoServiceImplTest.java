package cn.iocoder.yudao.module.ssp.service.sspSlotInfo;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SspSlotInfoServiceImplTest {

    @InjectMocks
    private SspSlotInfoServiceImpl slotInfoService;

    @Mock
    private SspSlotInfoMapper slotInfoMapper;
    @Mock
    private LaunchMapper launchMapper;

    @Test
    void deleteSlotInfo_rejectsWhenLaunchReferencesSspSlot() {
        when(slotInfoMapper.selectSlotInfoById(1L)).thenReturn(new SspSlotInfoDO());
        when(launchMapper.selectLaunchBySspSlotId(1L)).thenReturn(Collections.singletonList(new LaunchDO()));

        assertThrows(ServiceException.class, () -> slotInfoService.deleteSlotInfo(1L));

        verify(slotInfoMapper, never()).deleteById(1L);
    }
}
