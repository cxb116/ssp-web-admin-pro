package cn.iocoder.yudao.module.ssp.service.media;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.ssp.controller.admin.media.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.media.MediaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.ssp.dal.mysql.media.MediaMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.MEDIA_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 媒体 Service 实现类
 *
 * @author 少年阿宾
 */
@Service
@Validated
public class MediaServiceImpl implements MediaService {

    @Resource
    private MediaMapper mediaMapper;

    @Override
    public Long createMedia(MediaSaveReqVO createReqVO) {
        // 插入
        MediaDO media = BeanUtils.toBean(createReqVO, MediaDO.class);
        mediaMapper.insert(media);

        // 返回
        return media.getId();
    }

    @Override
    public void updateMedia(MediaSaveReqVO updateReqVO) {
        // 校验存在
        validateMediaExists(updateReqVO.getId());
        // 更新
        MediaDO updateObj = BeanUtils.toBean(updateReqVO, MediaDO.class);
        mediaMapper.updateById(updateObj);
    }

    @Override
    public void deleteMedia(Long id) {
        // 校验存在
        validateMediaExists(id);
        // 删除
        mediaMapper.deleteById(id);
    }

    @Override
        public void deleteMediaListByIds(List<Long> ids) {
        // 删除
        mediaMapper.deleteByIds(ids);
        }


    private void validateMediaExists(Long id) {
        if (mediaMapper.selectById(id) == null) {
            throw exception(MEDIA_NOT_EXISTS);
        }
    }

    @Override
    public MediaDO getMedia(Long id) {
        return mediaMapper.selectById(id);
    }

    @Override
    public PageResult<MediaDO> getMediaPage(MediaPageReqVO pageReqVO) {
        return mediaMapper.selectPage(pageReqVO);
    }

}