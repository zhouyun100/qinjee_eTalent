package com.qinjee.masterdata.controller.numberandparam;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.ContractParam;
import com.qinjee.masterdata.model.entity.EmployeeNumberRule;
import com.qinjee.masterdata.model.vo.staff.ContractParamVo;
import com.qinjee.masterdata.model.vo.staff.EmployeeNumberRuleVo;
import com.qinjee.masterdata.service.employeenumberrule.IEmployeeNumberRuleService;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/number")
@Api(tags = "【人员管理】工号合同编号操作")
public class NumberController extends BaseController {
    @Autowired
    private IEmployeeNumberRuleService employeeNumberRuleService;
    /**
     * 新增合同参数表
     */
    @RequestMapping(value = "/insertContractParam", method = RequestMethod.POST)
    @ApiOperation(value = "新增合同参数表", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult insertContractParam(@Valid ContractParamVo contractParamVo) {
        Boolean b = checkParam(contractParamVo, getUserSession());
        if (b) {
                employeeNumberRuleService.addContractParam(contractParamVo,getUserSession());
                return ResponseResult.SUCCESS();
        }
        return failResponseResult("合同参数表参数错误或者session错误");
    }
    /**
     * 新增工号生成表
     */
    @RequestMapping(value = "/insertEmployNumber", method = RequestMethod.POST)
    @ApiOperation(value = "新增工号生成表", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult insertEmployNumber(@Valid EmployeeNumberRuleVo employeeNumberRuleVo) {
        Boolean b = checkParam(employeeNumberRuleVo, getUserSession());
        if (b) {
                employeeNumberRuleService.addEmployeeNumberRule(employeeNumberRuleVo,getUserSession());
                return ResponseResult.SUCCESS();
        }
        return failResponseResult("工号表错误或者session错误");
    }
    /**
     * 生成合同编号
     */
    @RequestMapping(value = "/creatConNumber", method = RequestMethod.POST)
    @ApiOperation(value = "生成合同编号", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult<String> creatConNumber() throws Exception {
        Boolean b = checkParam( getUserSession());
        if (b) {
                String number = employeeNumberRuleService.createConNumber(getUserSession().getCompanyId ());
                return new ResponseResult<>(number, CommonCode.SUCCESS);
        }
        return failResponseResult("生成合同编号错误或者session错误");
    }
    /**
     * 生成工号
     */
    @RequestMapping(value = "/creatNumberPre", method = RequestMethod.POST)
    @ApiOperation(value = "生成工号", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult<String> creatNumberPre( ) throws Exception {
        Boolean b = checkParam(getUserSession ());
        if (b) {
                String number = employeeNumberRuleService.createEmpNumber (getUserSession ().getCompanyId ());
                return new ResponseResult<>(number, CommonCode.SUCCESS);
        }
        return failResponseResult("生成工号错误或者session错误");
    }
    /**
     * 根据企业id展示生成合同编号规则
     */
    @RequestMapping(value = "/showCreateConRule", method = RequestMethod.POST)
    @ApiOperation(value = "根据企业id展示生成合同编号规则", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult< List < ContractParam > > showCreateConRule() {
        Boolean b = checkParam( getUserSession());
        if (b) {
                List < ContractParam > contractParams = employeeNumberRuleService.showCreateConRule ( getUserSession () );
                    return new ResponseResult <> ( contractParams, CommonCode.SUCCESS );
        }
        return failResponseResult("session错误");
    }
    /**
     * 根据企业id展示生成工号规则
     */
    @RequestMapping(value = "/showCreateEmpRule", method = RequestMethod.POST)
    @ApiOperation(value = "根据企业id展示生成工号规则", notes = "hkt")
//    @ApiImplicitParam(name = "customArchiveTable", value = "自定义表", paramType = "form" ,required = true)
    public ResponseResult< List < EmployeeNumberRule > > showCreateEmpRule() {
        Boolean b = checkParam( getUserSession());
        if (b) {
                List < EmployeeNumberRule > contractParams = employeeNumberRuleService.showCreateEmpRule ( getUserSession () );
                    return new ResponseResult <> ( contractParams, CommonCode.SUCCESS );
        }
        return failResponseResult("session错误");
    }
    private Boolean checkParam(Object... params) {
        for (Object param : params) {
            if (null == param || "".equals(param)) {
                return false;
            }
        }
        return true;
    }

    private ResponseResult failResponseResult(String message){
        ResponseResult fail = ResponseResult.FAIL();
        fail.setMessage(message);
        return fail;
    }
}
