package cn.iocoder.yudao.module.ssp.service.media;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.media.MediaDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.app.AppMapper;
import cn.iocoder.yudao.module.ssp.dal.mysql.media.MediaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @InjectMocks
    private MediaServiceImpl mediaService;

    @Mock
    private MediaMapper mediaMapper;
    @Mock
    private AppMapper appMapper;

    @Test
    void deleteMedia_rejectsWhenMediaHasApps() {
        ReflectionTestUtils.setField(mediaService, "appMapper", appMapper);
        when(mediaMapper.selectById(1L)).thenReturn(new MediaDO());
        when(appMapper.selectList(any())).thenReturn(Collections.singletonList(new AppDO()));

        assertThrows(ServiceException.class, () -> mediaService.deleteMedia(1L));

        verify(mediaMapper, never()).deleteById(1L);
    }
}
