package com.qinjee.masterdata.dao;

import com.qinjee.masterdata.model.entity.CustomTable;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @author Administrator
 */
@Repository
public interface CustomTableDao {
    int deleteByPrimaryKey(Integer tableId);

    int insert(CustomTable record);

    int insertSelective(CustomTable record);

    CustomTable selectByPrimaryKey(Integer tableId);

    int updateByPrimaryKeySelective(CustomTable record);

    int updateByPrimaryKey(CustomTable record);
    int selectMaxPrimaryKey();
    int PretenddeleteByPrimaryKey(Integer tableId);
    List<CustomTable> selectByPage(Integer currentPage,Integer pageSize);
}
