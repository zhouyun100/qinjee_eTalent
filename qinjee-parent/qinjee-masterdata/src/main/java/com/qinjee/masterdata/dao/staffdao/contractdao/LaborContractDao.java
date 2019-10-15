package com.qinjee.masterdata.dao.staffdao.contractdao;

import com.qinjee.masterdata.model.entity.LaborContract;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaborContractDao {


    int deleteByPrimaryKey(Integer contractId);

    int insert(LaborContract record);

    int insertSelective(LaborContract record);

    LaborContract selectByPrimaryKey(Integer contractId);

    int updateByPrimaryKeySelective(LaborContract record);

    int updateByPrimaryKey(LaborContract record);

    List<Integer> seleltByArcId(List<Integer> arcList);

    List<Integer> seleltByArcIdIn(List<Integer> arcList);

    Integer seleltByArcIdSingle(Integer integer);

    List<LaborContract> selectLabByorgId(@Param("orgId") Integer orgId);


    List<Integer> selectArcByCon(@Param("conList") List<Integer> conList);

    List<Integer> selectByorgId(@Param("orgId") Integer orgId);
}