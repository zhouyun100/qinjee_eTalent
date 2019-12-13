package com.qinjee.masterdata.service.organation.impl;

import com.github.pagehelper.PageHelper;
import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.organation.PositionDao;
import com.qinjee.masterdata.dao.organation.PositionGradeRelationDao;
import com.qinjee.masterdata.dao.organation.PositionLevelRelationDao;
import com.qinjee.masterdata.dao.organation.PostDao;
import com.qinjee.masterdata.model.entity.Position;
import com.qinjee.masterdata.model.entity.PositionGradeRelation;
import com.qinjee.masterdata.model.entity.PositionLevelRelation;
import com.qinjee.masterdata.model.entity.Post;
import com.qinjee.masterdata.model.vo.organization.PositionVo;
import com.qinjee.masterdata.model.vo.organization.page.PositionPageVo;
import com.qinjee.masterdata.model.vo.organization.query.QueryField;
import com.qinjee.masterdata.service.organation.PositionGradeService;
import com.qinjee.masterdata.service.organation.PositionLevelService;
import com.qinjee.masterdata.service.organation.PositionService;
import com.qinjee.masterdata.utils.QueryFieldUtil;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 高雄
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年09月18日 15:32:00
 */
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionDao positionDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private PositionGradeService positionGradeService;
    @Autowired
    private PositionLevelService positionLevelService;
    @Autowired
    private PositionGradeRelationDao positionGradeRelationDao;
    @Autowired
    private PositionLevelRelationDao positionLevelRelationDao;

    @Override
    public List<Position> getPositionListByGroupId(Integer positionGroupId) {
        return positionDao.getPositionListByGroupId(positionGroupId);
    }

    @Override
    public ResponseResult<PageResult<Position>> getPositionPage(UserSession userSession, PositionPageVo pageVo) {

        String sortFieldStr=null;
        if(Objects.nonNull(pageVo.getQuerFieldVos())){
            Optional<List<QueryField>> querFieldVos = Optional.of(pageVo.getQuerFieldVos());
            sortFieldStr = QueryFieldUtil.getSortFieldStr(querFieldVos, Position.class);
        }
        Integer companyId = userSession.getCompanyId();
        if (pageVo != null && (pageVo.getPageSize() != null && pageVo.getCurrentPage() != null)) {
            PageHelper.startPage(pageVo.getCurrentPage(), pageVo.getPageSize());
        }
        List<Position> positionList = positionDao.getPositionPage(pageVo,sortFieldStr);
        PageResult<Position> pageResult = new PageResult<>(positionList);

        return new ResponseResult<>(pageResult);

        /*if (!CollectionUtils.isEmpty(positionList)) {
            for (Position position : positionList) {
                List<PositionGrade> positionGradeList = positionGradeService.getPositionGradeListByPositionId(position.getPositionId());
                List<PositionLevel> positionLevelList = positionLevelService.getPositionLevelByPositionId(position.getPositionId());
                StringBuilder positionGradeNames = new StringBuilder();
                StringBuilder positionLevelNames = new StringBuilder();

                if (!CollectionUtils.isEmpty(positionGradeList)) {
                    positionGradeList.stream().forEach(positionGrade -> {
                        String positionGradeName = positionGrade.getPositionGradeName();
                        if (StringUtils.isNotBlank(positionGradeName)) {
                            positionGradeNames.append(positionGradeName).append(",");
                        }
                    });
                }

                if (!CollectionUtils.isEmpty(positionLevelList)) {
                    positionLevelList.stream().forEach(positionLevel -> {
                        String positionLevelName = positionLevel.getPositionLevelName();
                        if (StringUtils.isNotBlank(positionLevelName)) {
                            positionLevelNames.append(positionLevelName).append(",");
                        }
                    });
                }
                position.setPositionGradeNames(positionGradeNames.delete(positionGradeNames.toString().length() - 2, positionGradeNames.toString().length()).toString());
                position.setPositionLevelNames(positionLevelNames.delete(positionLevelNames.toString().length() - 2, positionLevelNames.toString().length()).toString());
            }
        }*/

    }

    @Transactional
    @Override
    public ResponseResult addPosition(PositionVo positionVo, UserSession userSession) {
        Position position = new Position();
        BeanUtils.copyProperties(positionVo, position);
        position.setOperatorId(userSession.getArchiveId());
        position.setIsDelete((short) 0);
        //设置排序id
        Integer sortId;
        List<Position> positionList = positionDao.getPositionListByGroupId(positionVo.getPositionGroupId());

        if (!CollectionUtils.isEmpty(positionList)) {
            List<Position> positions = positionList.stream().filter(position1 -> {
                if (positionVo.getPositionName().equals(position1.getPositionName())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(positions)){
                return new ResponseResult(CommonCode.POSITION_NAME_REPEAT);
            }

            Position lastPosition = positionList.get(positionList.size() - 1);
            sortId = lastPosition.getSortId() + 1000;
        } else {
            sortId = 1000;
        }
        position.setSortId(sortId);
        positionDao.insertSelective(position);

       /* List<Integer> positionLevelIds = positionVo.getPositionGradeIds();
        //职位职等关系表
        if (!CollectionUtils.isEmpty(positionLevelIds)) {
            for (Integer positionLevel : positionLevelIds) {
                //新增职位职等关系表
                addPositionGradeRelation(userSession, position, positionLevel);
            }
        }

        List<Integer> positionGradeIds = positionVo.getPositionLevelIds();
        //职位职级关系表
        if (!CollectionUtils.isEmpty(positionGradeIds)) {
            for (Integer positionLevelId : positionGradeIds) {
                //新增职位职级关系表
                addPositionLevelRelation(userSession, position, positionLevelId);
            }
        }*/
        return new ResponseResult();
    }

    /**
     * 新增职位职等关系表
     *
     * @param userSession
     * @param position
     * @param positionLevel
     */
    private void addPositionGradeRelation(UserSession userSession, Position position, Integer positionLevel) {
        PositionGradeRelation positionGradeRelation = new PositionGradeRelation();
        positionGradeRelation.setIsDelete((short) 0);
        positionGradeRelation.setOperatorId(userSession.getArchiveId());
        positionGradeRelation.setPositionGradeId(positionLevel);
        positionGradeRelation.setPositionId(position.getPositionId());
        positionGradeRelationDao.insertSelective(positionGradeRelation);
    }

    /**
     * 新增职位职级关系表
     *
     * @param userSession
     * @param position
     * @param positionLevelId
     */
    private void addPositionLevelRelation(UserSession userSession, Position position, Integer positionLevelId) {
        PositionLevelRelation positionLevelRelation = new PositionLevelRelation();
        positionLevelRelation.setIsDelete((short) 0);
        positionLevelRelation.setOperatorId(userSession.getArchiveId());
        positionLevelRelation.setPositionLevelId(positionLevelId);
        positionLevelRelation.setPositionId(position.getPositionId());
        positionLevelRelationDao.insertSelective(positionLevelRelation);
    }

    @Transactional
    @Override
    public ResponseResult editPosition(PositionVo positionVo, UserSession userSession) {
        Position position = new Position();
        BeanUtils.copyProperties(positionVo, position);
        position.setOperatorId(userSession.getArchiveId());
        positionDao.updateByPrimaryKey(position);

       /* List<PositionGrade> positionGradeList = positionGradeService.getPositionGradeListByPositionId(positionVo.getPositionId());
        List<Integer> position_GradeIds = positionVo.getPositionGradeIds();
        //判断职位是否有职等
        if (!CollectionUtils.isEmpty(positionGradeList)) {
            Set<Integer> positionGradeIds = positionGradeList.stream().map(positionGrade -> positionGrade.getPositionGradeId()).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(position_GradeIds)) {
                for (Integer position_gradeId : position_GradeIds) {
                    boolean contains = positionGradeIds.contains(position_gradeId);
                    if (!contains) {
                        //不存在则新增职位职等关系
                        addPositionGradeRelation(userSession, position, position_gradeId);
                    } else {
                        positionGradeIds.remove(position_gradeId);
                    }
                }

                if (!CollectionUtils.isEmpty(positionGradeIds)) {
                    for (Integer positionGradeId : positionGradeIds) {
                        PositionGradeRelation positionGradeRelation = new PositionGradeRelation();
                        positionGradeRelation.setPositionId(position.getPositionId());
                        positionGradeRelation.setPositionGradeId(positionGradeId);
                        positionGradeRelation.setOperatorId(userSession.getArchiveId());
                        positionGradeRelation.setIsDelete((short) 1);
                        positionGradeRelationDao.updateIsDeleteByPositionGradeRelation(positionGradeRelation);
                    }
                }
            }
        } else {
            if (!CollectionUtils.isEmpty(position_GradeIds)) {
                for (Integer position_gradeId : position_GradeIds) {
                    //不存在则新增职位职等关系
                    addPositionGradeRelation(userSession, position, position_gradeId);
                }
            }
        }
        List<PositionLevel> positionLevelList = positionLevelService.getPositionLevelByPositionId(positionVo.getPositionId());
        List<Integer> position_LevelIds = positionVo.getPositionLevelIds();
        //判断职位是否有职级
        if (!CollectionUtils.isEmpty(positionLevelList)) {
            Set<Integer> positionLevelIds = positionLevelList.stream().map(positionLevel -> positionLevel.getPositionLevelId()).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(positionLevelIds)) {
                for (Integer positionLevelId : position_LevelIds) {
                    boolean contains = positionLevelIds.contains(positionLevelId);
                    if (contains) {
                        //不存在则新增职位职级关系
                        addPositionLevelRelation(userSession, position, positionLevelId);
                    } else {
                        positionLevelIds.remove(positionLevelId);
                    }
                }
                if (!CollectionUtils.isEmpty(positionLevelIds)) {
                    for (Integer positionLevelId : positionLevelIds) {
                        PositionLevelRelation positionLevelRelation = new PositionLevelRelation();
                        positionLevelRelation.setIsDelete((short) 1);
                        positionLevelRelation.setOperatorId(userSession.getArchiveId());
                        positionLevelRelation.setPositionLevelId(positionLevelId);
                        positionLevelRelation.setPositionId(position.getPositionId());
                        positionLevelRelationDao.updateIsDeleteByPositionLevelRelation(positionLevelRelation);
                    }
                }
            }
        }else {
            if(!CollectionUtils.isEmpty(position_LevelIds)){
                for (Integer position_levelId : position_LevelIds) {
                    addPositionLevelRelation(userSession, position, position_levelId);
                }
            }
        }*/
        return new ResponseResult();
    }

    @Transactional
    @Override
    public ResponseResult deletePosition(List<Integer> positionIds, UserSession userSession) {
        if(!CollectionUtils.isEmpty(positionIds)){

            List<Post> posts=postDao.listPostByPisitionId(positionIds);
            if(Objects.nonNull(posts)){
                ExceptionCast.cast(CommonCode.POSITION_USED_NY_POST);
            }
            for (Integer positionId : positionIds) {
                Position position = new Position();
                position.setPositionId(positionId);
                position.setIsDelete((short) 1);
                position.setOperatorId(userSession.getArchiveId());
                positionDao.updateByPrimaryKeySelective(position);
               /* //删除对应的职等
                PositionGradeRelation positionGradeRelation = new PositionGradeRelation();
                positionGradeRelation.setOperatorId(userSession.getArchiveId());
                positionGradeRelation.setIsDelete((short) 1);
                positionGradeRelation.setPositionId(positionId);
                positionGradeRelationDao.updateIsDeleteByPositionGradeRelation(positionGradeRelation);
                //删除对应的职级
                PositionLevelRelation positionLevelRelation = new PositionLevelRelation();
                positionLevelRelation.setPositionId(positionId);
                positionLevelRelation.setIsDelete((short) 1);
                positionLevelRelation.setOperatorId(userSession.getArchiveId());
                positionLevelRelationDao.updateIsDeleteByPositionLevelRelation(positionLevelRelation);*/
            }
        }
        return new ResponseResult();
    }

    @Override
    @Transactional
    public void sortPosition(List<Integer> positionIds) {
        //查询出职位列表
        List<Position> positionList = positionDao.getSinglePositionList(positionIds);
        Set<Integer> parentOrgSet = new HashSet<>();
        for (Position position : positionList) {
            //将父机构id存储在set中
            parentOrgSet.add(position.getPositionGroupId());
        }
        //判断是否在同一级职位族下
        if (parentOrgSet.size() > 1) {
            ExceptionCast.cast(CommonCode.NOT_SAVE_LEVEL_EXCEPTION);
        }
        positionDao.sortPosition(positionIds);
    }

    @Override
    public ResponseResult<List<Position>> getAllPositions(UserSession userSession) {
        List<Position> positionList = positionDao.getPositionList(userSession.getCompanyId());
        return new ResponseResult(positionList);
    }

    @Override
    public void determinePositionNameIsOnly(String positionName, UserSession userSession) {
        Position position = positionDao.getPositionByNameAndCompanyId(positionName, userSession.getCompanyId());
        if (Objects.nonNull(position)){
            ExceptionCast.cast(CommonCode.POSITION_NAME_REPEAT);
        }
    }


/*
    @Override
    public ResponseResult<Position> getPositionLevelAndGrade(Integer positionId) {
        //根据职职位id查询对应的职级
        List<PositionLevel> positionLevels = positionLevelService.getPositionLevelByPositionId(positionId);
        //根据职职位id查询对应的职等
        List<PositionGrade> positionGrades = positionGradeService.getPositionGradeListByPositionId(positionId);
        Position position = new Position();
        position.setPositionLevels(positionLevels);
        position.setPositionGrades(positionGrades);
        return new ResponseResult<>(position);
    }*/
}
