package com.qinjee.masterdata.service.organation.impl;

import com.github.pagehelper.PageHelper;
import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.organation.PositionGradeDao;
import com.qinjee.masterdata.dao.organation.PositionLevelDao;
import com.qinjee.masterdata.model.entity.PositionGrade;
import com.qinjee.masterdata.model.entity.PositionLevel;
import com.qinjee.masterdata.model.entity.Post;
import com.qinjee.masterdata.model.vo.organization.PositionGradeVo;
import com.qinjee.masterdata.model.vo.organization.PositionLevelVo;
import com.qinjee.masterdata.service.organation.PositionGradeService;
import com.qinjee.model.request.PageVo;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author 彭洪思
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年09月18日 15:41:00
 */
@Service
public class PositionGradeServiceImpl implements PositionGradeService {
    @Autowired
    private PositionGradeDao positionGradeDao;
    @Autowired
    private PositionLevelDao positionLevelDao;


    @Override
    public int addPositionGrade(PositionGrade positionGrade, UserSession userSession) {

        //查重
        PositionGrade  pg = positionGradeDao.getByPositionGradeName(positionGrade.getPositionGradeName(),userSession.getCompanyId());
        if(Objects.nonNull(pg)){
            ExceptionCast.cast(CommonCode.NAME_ALREADY_USED);
        }

        positionGrade.setCompanyId(userSession.getCompanyId());
        positionGrade.setOperatorId(userSession.getArchiveId());
        //设置排序id
        Integer lastSortId = positionGradeDao.getLastSortId(userSession.getCompanyId());
        if(null==lastSortId)
            lastSortId=0;

        positionGrade.setSortId(++lastSortId);
        return positionGradeDao.insert(positionGrade);
    }

    @Override
    public int editPositionGrade(PositionGrade positionGrade, UserSession userSession) {
        //查重
        PositionGrade  pg = positionGradeDao.getByPositionGradeName(positionGrade.getPositionGradeName(),userSession.getCompanyId());
        if(Objects.nonNull(pg)&&!pg.getPositionGradeId().equals(positionGrade.getPositionGradeId())){
            ExceptionCast.cast(CommonCode.NAME_ALREADY_USED);
        }



        positionGrade.setOperatorId(userSession.getArchiveId());
        return positionGradeDao.update(positionGrade);
    }

    @Override
    public int batchDeletePositionGrade(UserSession userSession, List<Integer> positionGradeIds) {
        //判断职等是否被职级引用
        List<PositionLevelVo> positionLevels = positionLevelDao.listByPositionGradeIds(positionGradeIds);
        if (CollectionUtils.isNotEmpty(positionLevels))
            ExceptionCast.cast(CommonCode.GRADE_USE_IN_LEVEL);
        return positionGradeDao.batchDelete(positionGradeIds);
    }

    @Override
    public int sortPositionGrade(UserSession userSession, List<Integer> positionGradeIds) {
        return positionGradeDao.sort(userSession.getArchiveId(),positionGradeIds);
    }

    @Override
    public PageResult<PositionGrade> listPositionGrade(PageVo pageVo, UserSession userSession) {
        if (null != pageVo.getCurrentPage() && null != pageVo.getPageSize())
            PageHelper.startPage(pageVo.getCurrentPage(), pageVo.getPageSize());
        List<PositionGrade> positionGradeList = positionGradeDao.list(userSession.getCompanyId());
        PageResult<PositionGrade> pageResult = new PageResult<>(positionGradeList);
        return pageResult;
    }
}
