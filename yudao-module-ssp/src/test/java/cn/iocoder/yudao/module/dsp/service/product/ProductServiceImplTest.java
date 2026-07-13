package cn.iocoder.yudao.module.dsp.service.product;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.product.ProductDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.product.ProductMapper;
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
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private DspSlotInfoMapper slotInfoMapper;

    @Test
    void deleteProduct_rejectsWhenDspSlotReferencesProduct() {
        ReflectionTestUtils.setField(productService, "slotInfoMapper", slotInfoMapper);
        when(productMapper.selectById(1L)).thenReturn(new ProductDO());
        when(slotInfoMapper.selectList(any())).thenReturn(Collections.singletonList(new DspSlotInfoDO()));

        assertThrows(ServiceException.class, () -> productService.deleteProduct(1L));

        verify(productMapper, never()).deleteById(1L);
    }
}
