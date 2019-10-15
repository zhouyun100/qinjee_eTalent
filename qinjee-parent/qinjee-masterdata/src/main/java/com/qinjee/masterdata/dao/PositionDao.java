package com.qinjee.masterdata.dao;

import com.qinjee.masterdata.model.entity.Position;

import java.util.List;

public interface PositionDao {
    int deleteByPrimaryKey(Integer positionId);

    int insert(Position record);

    int insertSelective(Position record);

    Position selectByPrimaryKey(Integer positionId);

    int updateByPrimaryKeySelective(Position record);

    int updateByPrimaryKey(Position record);

    /**
     * 根据职位族id获取职位
     * @param positionGroupId
     * @return
     */
    List<Position> getPositionListByGroupId(Integer positionGroupId);

    /**
     * 根据公司id获取职位
     * @param companyId
     * @return
     */
    List<Position> getPositionList(Integer companyId);

    /**
     * 查询机构下的职位
     * @param orgId
     * @return
     */
    List<Position> getPositionListByOrgId(Integer orgId);
}