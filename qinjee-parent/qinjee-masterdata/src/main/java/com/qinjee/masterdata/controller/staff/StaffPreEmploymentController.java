package com.qinjee.masterdata.controller.staff;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.PreEmployment;
import com.qinjee.masterdata.model.vo.staff.StatusChangeVo;
import com.qinjee.masterdata.service.staff.IStaffPreEmploymentService;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/staffpre")
@Api(tags = "【人员管理】预入职相关接口")
public class StaffPreEmploymentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(StaffPreEmploymentController.class);
    @Autowired
    private IStaffPreEmploymentService staffPreEmploymentService;
    /**
     * 新增预入职
     * 预入职表与档案表用物理表进行对应，此时需要物理表的存在，新增两个自定义表，新增n个自定义字段，物理字段名与物理属性名分别是物理表与属性的对应
     * 在项目初始化应该建好此表
     */
    @RequestMapping(value = "/insertPreEmployment ", method = RequestMethod.GET)
    @ApiOperation(value = "新增预入职", notes = "hkt")
    @ApiImplicitParam(name = "PreEmployment", value = "PreEmployment", paramType = "form", required = true)
    public ResponseResult insertPreEmployment(@Valid PreEmployment preEmployment ){
        Boolean b = checkParam(preEmployment);
        if(b){
            try {
                staffPreEmploymentService.insertPreEmployment(preEmployment);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("新增预入职失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 删除预入职
     */
    @RequestMapping(value = "/deletePreEmployment ", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构查看预入职", notes = "hkt")
    @ApiImplicitParam(name = "list", value = "预入职id集合", paramType = "query", required = true)
    public ResponseResult deletePreEmployment(List<Integer> list ){
        Boolean b = checkParam(list);
        if(b){
            try {
                staffPreEmploymentService.deletePreEmployment(list);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("删除预入职失败");
            }
        }
        return  failResponseResult("参数错误");
    }
    /**
     * 修改预入职信息(值的信息)
     */
    @RequestMapping(value = "/updatePreEmployment ", method = RequestMethod.GET)
    @ApiOperation(value = "修改预入职信息(值的信息)", notes = "hkt")
    @ApiImplicitParam(name = "PreEmployment", value = "PreEmployment", paramType = "form",  required = true)
    public ResponseResult updatePreEmployment(@Valid PreEmployment preEmployment){
        Boolean b = checkParam(preEmployment);
        if(b){
            try {
                staffPreEmploymentService.updatePreEmployment(preEmployment);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("修改预入职信息失败");
            }
        }
        return  failResponseResult("参数错误");
    }
    /**
     * 修改预入职信息(显示字段的信息)
     */
    @RequestMapping(value = "/updatePreEmploymentField ", method = RequestMethod.GET)
    @ApiOperation(value = "修改预入职信息(显示字段的信息)", notes = "hkt")
    @ApiImplicitParam(name = "map", value = "字段id与对应的字段名", paramType = "form",  required = true)
    public ResponseResult updatePreEmploymentField(Map<Integer,String> map){
        Boolean b = checkParam(map);
        if(b){
            try {
                staffPreEmploymentService.updatePreEmploymentField(map);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("修改预入职信息失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 查看预入职信息(显示字段的信息)
     */
    @RequestMapping(value = "/selectPreEmploymentField ", method = RequestMethod.GET)
    @ApiOperation(value = "查看预入职信息(显示字段的信息)，返回值中map是物理表属性，value是字段名", notes = "hkt")
    public ResponseResult<Map<String,String>> selectPreEmploymentField(){
        Boolean b = checkParam(getUserSession());
        if(b){
            try {
                staffPreEmploymentService.selectPreEmploymentField(getUserSession());
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("查看预入职信息失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 根据机构查看预入职
     */
    @RequestMapping(value = "/selectPreEmployment ", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构查看预入职(物理表)", notes = "hkt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机构id", paramType = "query", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", paramType = "query", required = true),
            @ApiImplicitParam(name = "PageSize", value = "页大小", paramType = "query", required = true),

    })
    public ResponseResult<PageResult<PreEmployment>> selectPreEmployment(Integer companyId,
                                                                         Integer currentPage,
                                                                         Integer pageSize){
        Boolean b = checkParam(companyId,currentPage,pageSize);
        if(b){
            try {
                PageResult<PreEmployment> pageResult =
                        staffPreEmploymentService.selectPreEmployment(companyId, currentPage, pageSize);
                if(pageResult!=null){
                    return ResponseResult.SUCCESS();
                }
                return failResponseResult("无入职人员或者此页无信息");
            } catch (Exception e) {
                return failResponseResult("根据机构查看预入职失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 发短信给预入职人员
     */
    @RequestMapping(value = "/sendMessage ", method = RequestMethod.GET)
    @ApiOperation(value = "短信发送入职登记表", notes = "hkt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list", value = "预入职登记表id组成的集合", paramType = "query", required = true),
            @ApiImplicitParam(name = "templateId", value = "短信模板", paramType = "query", required = true),
            @ApiImplicitParam(name = "params[]", value = "所传参数", paramType = "query", required = true),

    })
    public ResponseResult sendMessage(List<Integer> list, Integer templateId, String[] params) {
        Boolean b = checkParam(list,templateId,params);
        if(b){
            try {
                staffPreEmploymentService.sendMessage(list,templateId,params);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("发短信给预入职人员失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 发邮件给预入职人员
     */
    @RequestMapping(value = "/sendMail ", method = RequestMethod.GET)
    @ApiOperation(value = "邮箱发送入职登记表", notes = "hkt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list", value = "预入职收信人id集合", paramType = "query", required = true),
            @ApiImplicitParam(name = "list", value = "抄送人档案id集合", paramType = "query", required = true),
            @ApiImplicitParam(name = "String", value = "发送邮件主题", paramType = "query", required = true),
            @ApiImplicitParam(name = "String", value = "发送邮件内容", paramType = "query", required = true),
            @ApiImplicitParam(name = "String[]", value = "发送邮件附件路径的数组", paramType = "query", required = true),
    })
    public ResponseResult sendMail(List<Integer> prelist,List<Integer> conList,
                                   String content,String subject,
                                   String[] filepath) {
        Boolean b = checkParam(prelist,conList,content,subject,filepath);
        if(b){
            try {
                staffPreEmploymentService.sendManyMail(prelist,conList,content,subject,filepath) ;
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("发邮件给预入职人员失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 校验手机号码
     */
    @RequestMapping(value = "/checkPhone ", method = RequestMethod.GET)
    @ApiOperation(value = "校验手机号码", notes = "hkt")
    @ApiImplicitParam(name = "String", value = "手机号", paramType = "query", required = true)
    public ResponseResult checkPhone(String phoneNumber) {
        Boolean b = checkParam(phoneNumber);
        if(b){
            try {
                if(staffPreEmploymentService.checkPhone(phoneNumber)){
                    return new ResponseResult("手机号验证通过");
                }
                return failResponseResult("手机号验证不通过");
            } catch (Exception e) {
                return failResponseResult("手机号验证出现问题");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 校验邮箱
     */
    @RequestMapping(value = "/checkMail ", method = RequestMethod.GET)
    @ApiOperation(value = "校验邮箱", notes = "hkt")
    @ApiImplicitParam(name = "String", value = "邮箱", paramType = "query", required = true)
    public ResponseResult checkMail(String mail) {
        Boolean b = checkParam(mail);
        if(b){
            try {
                if(staffPreEmploymentService.checkMail(mail)){
                    return new ResponseResult("邮箱验证通过");
                }
                return failResponseResult("邮箱验证不通过");
            } catch (Exception e) {
                return failResponseResult("邮箱验证出现问题");
            }
        }
        return  failResponseResult("参数错误");

    }



    /**
     * 生成入职登记二维码,返回是二维码图片链接
     */
    @RequestMapping(value = "/creatQrcodeForPre", method = RequestMethod.GET)
    @ApiOperation(value = "生成入职登记二维码", notes = "hkt")
    @ApiImplicitParam(name = "url", value = "链接地址", paramType = "query", required = true)
    public ResponseResult creatQrcodeForPre(String url) {

        ResponseResult<String> stringResponseResult = new ResponseResult<>("qrcode path", CommonCode.SUCCESS);
        return stringResponseResult;
    }

    /**
     * 延期入职以及放弃入职(延期入职时间还需要添加时间)
     * 延期入职需要更新预入职信息的入职时间，同时更新预入职更改表
     * 确认入职需要更新预入职表中的入职状态，需要更新预入职更改表，需要将数据同步到档案表
     * 鉴于更改表与预入职表中字段有出入，无法完全获取其中的字段值，故需要传更改表的数据过来
     */

    @RequestMapping(value = "/updatePreEmploymentChange", method = RequestMethod.POST)
    @ApiOperation(value = "延期入职以及放弃入职", notes = "hkt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "PreEmploymentId", value = "预入职表id", paramType = "query", required = true),
            @ApiImplicitParam(name = "StatusChangeVo", value = "预入职变更表vo类", paramType = "form", required = true),
    })
    public ResponseResult updatePreEmploymentChange(Integer preEmploymentId,
                                                    StatusChangeVo statusChangeVo,
                                                    String reason) {
        Boolean b = checkParam(preEmploymentId,statusChangeVo,reason,getUserSession());
        if(b){
            try {
                staffPreEmploymentService.insertStatusChange(getUserSession(),preEmploymentId, statusChangeVo,reason);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                return failResponseResult("延期入职以及放弃入职失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 检验参数
     * @param params
     * @return
     */
    public Boolean checkParam(Object... params) {
        for (Object param : params) {
            if (null == param || "".equals(param)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 错误返回值
     * @param message
     * @return
     */
    public ResponseResult failResponseResult(String message){
        ResponseResult fail = ResponseResult.FAIL();
        fail.setMessage(message);
        logger.error(message);
        return fail;
    }

}