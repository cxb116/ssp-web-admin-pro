package cn.iocoder.yudao.module.dsp.service.dspslotinfo;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DspSlotInfoServiceImplTest {

    @InjectMocks
    private DspSlotInfoServiceImpl slotInfoService;

    @Mock
    private DspSlotInfoMapper slotInfoMapper;
    @Mock
    private LaunchMapper launchMapper;

    @Test
    void deleteSlotInfo_rejectsWhenLaunchReferencesDspSlot() {
        ReflectionTestUtils.setField(slotInfoService, "launchMapper", launchMapper);
        when(slotInfoMapper.selectById(1L)).thenReturn(new DspSlotInfoDO());
        when(launchMapper.selectLaunchByDspSlotId(1L)).thenReturn(Collections.singletonList(new LaunchDO()));

        assertThrows(ServiceException.class, () -> slotInfoService.deleteSlotInfo(1L));

        verify(slotInfoMapper, never()).deleteById(1L);
    }
}
