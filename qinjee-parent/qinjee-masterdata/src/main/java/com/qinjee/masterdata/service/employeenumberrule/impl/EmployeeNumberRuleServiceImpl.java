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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        employeeNumberRuleDao.updateByPrimaryKeySelective (employeeNumberRule);
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
        try {
            contractParamDao.updateContractSelective(contractParam);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    public String createEmpNumber(Integer id,Integer archiveId) throws Exception {
        EmployeeNumberRule employeeNumberRule = employeeNumberRuleDao.selectByPrimaryKey ( id );
        if(employeeNumberRule!=null) {
            CreatNumberVo creatNumberVo = new CreatNumberVo ();
            BeanUtils.copyProperties ( employeeNumberRule, creatNumberVo );
            String employeeNumberPrefix = creatNumberVo.getEmployeeNumberPrefix ();
            String dateModel = getDateModel ( creatNumberVo.getDateRule () );
            String employeeNumberInfix = creatNumberVo.getEmployeeNumberInfix ();
            String employeeNumberSuffix = creatNumberVo.getEmployeeNumberSuffix ();
            String digtaNumber = getDigtaNumber ( creatNumberVo.getDigitCapacity (),archiveId );
            return employeeNumberPrefix + dateModel + employeeNumberInfix + employeeNumberSuffix + digtaNumber;
        }else {
            ExceptionCast.cast ( CommonCode.PARAM_IS_NULL );
            return null;
        }
    }

    @Override
    public String createConNumber(Integer id, UserSession userSession) throws Exception {
        List < ContractParam > contractParamByCondition = contractParamDao.findContractParamByCondition ( id );
        CreatNumberVo creatNumberVo=new CreatNumberVo ();
        BeanUtils.copyProperties (contractParamByCondition.get(0),creatNumberVo);
        return getString ( creatNumberVo );
    }

    @Override
    public List < ContractParam >  showCreateConRule(UserSession userSession) {
        return  contractParamDao.findContractParamByCompanyId ( userSession.getCompanyId () );
    }

    @Override
    public List < EmployeeNumberRule > showCreateEmpRule(UserSession userSession) {
       return employeeNumberRuleDao.selectByCompanyId ( userSession.getCompanyId () );
    }

    private String getString( CreatNumberVo creatNumberVo) throws Exception {
        String employeeNumberPrefix = creatNumberVo.getContractRulePrefix ();
        String dateModel = getDateModel ( creatNumberVo.getDateRule () );
        String employeeNumberInfix = creatNumberVo.getContractRuleInfix ();
        String employeeNumberSuffix = creatNumberVo.getContractRuleSuffix ();
        Date date=new Date (  );
        String s = String.valueOf ( date.getTime () );
        int i = Integer.parseInt ( s.substring ( s.length () - creatNumberVo.getDigitCapacity () ) );
        String digtaNumber = getDigtaNumber ( creatNumberVo.getDigitCapacity (),i );
        return employeeNumberPrefix + dateModel + employeeNumberInfix + employeeNumberSuffix + digtaNumber;
    }

    private String getDateModel(String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if ("YY".equals(rule)) {
            return year.substring(2, 3);
        }
        if ("YYYY".equals(rule)) {
            return year;
        }
        if ("YYMM".equals(rule)) {
            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }
            return year.substring(2, 3) + month;
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
        return null;
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
        return null;
    }
}
