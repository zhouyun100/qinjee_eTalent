package com.qinjee.masterdata.dao.staffdao;

import com.qinjee.masterdata.model.entity.PreEmployment;

public interface PreEmploymentDao {
    int deleteByPrimaryKey(Integer employmentId);

    int insert(PreEmployment record);

    int insertSelective(PreEmployment record);

    PreEmployment selectByPrimaryKey(Integer employmentId);

    int updateByPrimaryKeySelective(PreEmployment record);

    int updateByPrimaryKey(PreEmployment record);
}