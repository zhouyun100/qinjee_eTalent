package com.qinjee.masterdata.service.employeenumberrule;

import com.qinjee.masterdata.model.entity.ContractParam;
import com.qinjee.masterdata.model.entity.EmployeeNumberRule;
import com.qinjee.masterdata.model.vo.staff.ContractParamVo;
import com.qinjee.masterdata.model.vo.staff.EmployeeNumberRuleVo;
import com.qinjee.model.request.UserSession;

import java.util.List;

public interface IEmployeeNumberRuleService {
    /**
     * 新增员工规则
     * @param employeeNumberRuleVo
     * @param userSession
     * @return
     */
    void addEmployeeNumberRule(EmployeeNumberRuleVo employeeNumberRuleVo, UserSession userSession);
    /**
     * 新增合同参数表
     */
    void addContractParam(ContractParamVo contractParamVo, UserSession userSession);
    /**
     * 生成工号编码
     * @param comapnyId
     * @return
     * @throws Exception
     */
    String createEmpNumber(Integer comapnyId) ;

    /**
     *
     * @param comapnyId
     * @return
     * @throws Exception
     */
    String createConNumber(Integer comapnyId) throws Exception;

    List < ContractParam > showCreateConRule(UserSession userSession);

    List< EmployeeNumberRule> showCreateEmpRule(UserSession userSession);
}
