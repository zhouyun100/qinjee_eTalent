package com.qinjee.masterdata.service.employeenumberrule.impl;

import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.EmployeeNumberRuleDao;
import com.qinjee.masterdata.dao.staffdao.contractdao.ContractParamDao;
import com.qinjee.masterdata.model.entity.ContractParam;
import com.qinjee.masterdata.model.entity.EmployeeNumberRule;
import com.qinjee.masterdata.model.vo.staff.ContractParamVo;
import com.qinjee.masterdata.model.vo.staff.CreatNumberVo;
import com.qinjee.masterdata.model.vo.staff.EmployeeNumberRuleVo;
import com.qinjee.masterdata.service.employeenumberrule.IEmployeeNumberRuleService;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 */
@Service
public class EmployeeNumberRuleServiceImpl implements IEmployeeNumberRuleService {
    @Autowired
    private EmployeeNumberRuleDao employeeNumberRuleDao;
    @Autowired
    private ContractParamDao contractParamDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addEmployeeNumberRule(EmployeeNumberRuleVo employeeNumberRuleVo, UserSession userSession) {
        EmployeeNumberRule employeeNumberRule = new EmployeeNumberRule();
        BeanUtils.copyProperties(employeeNumberRuleVo, employeeNumberRule);
        employeeNumberRule.setOperatorId(userSession.getArchiveId());
        employeeNumberRule.setCompanyId(userSession.getCompanyId());
        employeeNumberRule.setIsDelete((short) 0);
        if(null!=employeeNumberRule.getEnRuleId()&& 0!=employeeNumberRule.getEnRuleId()){
            employeeNumberRuleDao.updateByPrimaryKeySelective (employeeNumberRule);
        }else{
            employeeNumberRuleDao.insertSelective(employeeNumberRule);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addContractParam(ContractParamVo contractParamVo, UserSession userSession) {
        StringBuilder stringBuilder=new StringBuilder (  );
        ContractParam contractParam=new ContractParam();
        List < String > applicationScopeCode = contractParamVo.getApplicationScopeCode ();
        if(CollectionUtils.isNotEmpty ( applicationScopeCode )) {
            for (String s : applicationScopeCode) {
                stringBuilder.append ( s ).append ( "," );
            }
        }
        contractParam.setApplicationScopeCode ( stringBuilder.toString () );
        BeanUtils.copyProperties(contractParamVo,contractParam);
        contractParam.setCompanyId(userSession.getCompanyId());
        contractParam.setIsDelete((short) 0);
        contractParam.setIsEnable((short) 1);
        contractParam.setOperatorId(userSession.getArchiveId());
        if(null!=contractParam.getContractParamId()&& 0!=contractParam.getContractParamId()) {
            contractParamDao.updateContractSelective(contractParam);
        }else{
            contractParamDao.insertSelective(contractParam);
        }
    }

    @Override
    public String createEmpNumber(Integer companyId) {
        List < EmployeeNumberRule > employeeNumberRules = employeeNumberRuleDao.selectByCompanyId ( companyId );
        if(CollectionUtils.isNotEmpty ( employeeNumberRules )) {
            CreatNumberVo creatNumberVo = new CreatNumberVo ();
            BeanUtils.copyProperties ( employeeNumberRules.get ( 0 ), creatNumberVo );
            String employeeNumberPrefix = creatNumberVo.getEmployeeNumberPrefix ();
            String dateModel = getDateModel ( creatNumberVo.getDateRule () );
            String employeeNumberInfix = creatNumberVo.getEmployeeNumberInfix ();
            String employeeNumberSuffix = creatNumberVo.getEmployeeNumberSuffix ();
            String string = employeeNumberPrefix + dateModel + employeeNumberInfix + employeeNumberSuffix;
            String s=employeeNumberRuleDao.selectMaxNumberForEmp(employeeNumberPrefix,dateModel,employeeNumberInfix,employeeNumberSuffix,companyId, string);
            return getString ( creatNumberVo, employeeNumberPrefix, dateModel, employeeNumberInfix, employeeNumberSuffix, s );
        }else {
            return null;
        }
    }

    @Override
    public String createConNumber(Integer companyId)  {
        List < ContractParam > contractParamByCondition = contractParamDao.findContractParamByCompanyId ( companyId );
        if(CollectionUtils.isNotEmpty ( contractParamByCondition )) {
            CreatNumberVo creatNumberVo = new CreatNumberVo ();
            BeanUtils.copyProperties ( contractParamByCondition.get ( 0 ), creatNumberVo );
            String employeeNumberPrefix = creatNumberVo.getContractRulePrefix ();
            String dateModel = getDateModel ( creatNumberVo.getDateRule () );
            String employeeNumberInfix = creatNumberVo.getContractRuleInfix ();
            String employeeNumberSuffix = creatNumberVo.getContractRuleSuffix ();
            //截取来寻找目前最大的流水号
            String string = employeeNumberPrefix + dateModel + employeeNumberInfix + employeeNumberSuffix;
            String s=employeeNumberRuleDao.selectMaxNumberForCon(employeeNumberPrefix,dateModel,employeeNumberInfix,employeeNumberSuffix,companyId, string);
            return getString ( creatNumberVo, employeeNumberPrefix, dateModel, employeeNumberInfix, employeeNumberSuffix, s );
        }
        return null;
    }

    private String getString(CreatNumberVo creatNumberVo, String employeeNumberPrefix, String dateModel, String employeeNumberInfix, String employeeNumberSuffix, String s) {
        String digtaNumber = null;
        if (StringUtils.isNotEmpty ( s )) {
            int i = 0;
            try {
                i = Integer.parseInt ( s );
            } catch (NumberFormatException e) {
                i = 1;
            }
            digtaNumber = getDigtaNumber ( creatNumberVo.getDigitCapacity (), i + 1 );
        } else {
            digtaNumber = getDigtaNumber ( creatNumberVo.getDigitCapacity (), 1 );
        }
        return employeeNumberPrefix + dateModel + employeeNumberInfix + employeeNumberSuffix + digtaNumber;
    }

    @Override
    public List < ContractParam >  showCreateConRule(UserSession userSession) {
        List<ContractParam> contractParams=new ArrayList<>();
        List<ContractParam> contractParamByCompanyId = contractParamDao.findContractParamByCompanyId(userSession.getCompanyId());
        if(CollectionUtils.isNotEmpty(contractParamByCompanyId)){
            contractParams.add(contractParamByCompanyId.get(0));
        }
        return contractParams;
    }

    @Override
    public List < EmployeeNumberRule > showCreateEmpRule(UserSession userSession) {
        List<EmployeeNumberRule> employeeNumberRules=new ArrayList<>();
        List<EmployeeNumberRule> employeeNumberRule= employeeNumberRuleDao.selectByCompanyId(userSession.getCompanyId());
        if(CollectionUtils.isNotEmpty(employeeNumberRule)){
            employeeNumberRules.add(employeeNumberRule.get(0));
        }
       return employeeNumberRules;
    }

    private String getDateModel(String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if ("YY".equals(rule)) {
            return year.substring(2, 4);
        }
        if ("YYYY".equals(rule)) {
            return year;
        }
        if ("YYMM".equals(rule)) {
            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }
            return year.substring(2, 4) + month;
        }
        if ("YYYYMM".equals(rule)) {
            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }
            return year + month;
        }
        if ("YYYYMMDD".equals(rule)) {
            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }
            if (Integer.parseInt(day) < 10) {
                month = "0" + month;
            }
            return year + month + day;
        }
        return "";
    }


    private String getDigtaNumber(short capacity, Integer number)  {
        String t = "";
        String s = String.valueOf(number);
        if (s.length() > capacity) {
           ExceptionCast.cast ( CommonCode.ARCHIVEID_IS_TOOLONG );
        }
        if (s.length() == capacity) {
            return String.valueOf(number);
        }
        if (s.length() < capacity) {
            int i = capacity - s.length();
            for (int j = 0; j < i; j++) {
                t += "0";
            }
            return t + number;
        }
        return "";
    }
}
