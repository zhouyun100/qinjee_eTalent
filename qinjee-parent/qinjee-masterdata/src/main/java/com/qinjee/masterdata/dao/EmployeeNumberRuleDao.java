package com.qinjee.masterdata.dao;

import com.qinjee.masterdata.model.entity.EmployeeNumberRule;
import com.qinjee.masterdata.model.vo.staff.CreatNumberVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeNumberRuleDao {
    int deleteByPrimaryKey(Integer enRuleId);

    int insert(EmployeeNumberRule record);

    int insertSelective(EmployeeNumberRule record);

    EmployeeNumberRule selectByPrimaryKey(Integer enRuleId);

    int updateByPrimaryKeySelective(EmployeeNumberRule record);

    int updateByPrimaryKey(EmployeeNumberRule record);

    List< EmployeeNumberRule> selectByCompanyId(@Param("companyId") Integer companyId);

    String selectMaxNumberForCon(@Param("employeeNumberPrefix") String employeeNumberPrefix, @Param("dateModel") String dateModel, @Param("employeeNumberInfix") String employeeNumberInfix, @Param("employeeNumberSuffix") String employeeNumberSuffix, @Param("companyId") Integer companyId, @Param("string") String string);
    String selectMaxNumberForEmp(@Param("employeeNumberPrefix") String employeeNumberPrefix, @Param("dateModel") String dateModel, @Param("employeeNumberInfix") String employeeNumberInfix, @Param("employeeNumberSuffix") String employeeNumberSuffix, @Param("companyId") Integer companyId, @Param("string") String string);
}
