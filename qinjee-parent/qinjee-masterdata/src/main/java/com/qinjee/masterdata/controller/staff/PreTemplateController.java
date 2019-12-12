package com.qinjee.masterdata.controller.staff;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.TemplateAttachmentGroup;
import com.qinjee.masterdata.model.entity.TemplateEntryRegistration;
import com.qinjee.masterdata.model.vo.custom.EntryRegistrationTableVO;
import com.qinjee.masterdata.model.vo.custom.TemplateCustomTableFieldVO;
import com.qinjee.masterdata.model.vo.custom.TemplateCustomTableVO;
import com.qinjee.masterdata.model.vo.staff.entryregistration.TemplateAttachmentGroupVO;
import com.qinjee.masterdata.service.custom.CustomTableFieldService;
import com.qinjee.masterdata.service.custom.TemplateCustomTableFieldService;
import com.qinjee.masterdata.service.staff.EntryRegistrationService;
import com.qinjee.masterdata.service.staff.IPreTemplateService;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staffTemp")
@Api(tags = "【人员管理】预入职模板")
public class PreTemplateController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger ( PreTemplateController.class );
    @Autowired
    private IPreTemplateService preTemplateService;
    @Autowired
    private EntryRegistrationService entryRegistrationService;
    @Autowired
    private TemplateCustomTableFieldService templateCustomTableFieldService;
    @Autowired
    private CustomTableFieldService customTableFieldService;

    /**
     * 根据企业ID查询入职登记模板列表
     */
    @RequestMapping(value = "/searchTemplateEntryRegistrationList", method = RequestMethod.GET)
    @ApiOperation(value = "根据企业ID查询入职登记模板列表", notes = "hkt")
    public ResponseResult < List < TemplateEntryRegistration > > searchTemplateEntryRegistrationList(Integer companyId) {
        Boolean b = checkParam ( companyId );
        if (b) {
            try {
                List < TemplateEntryRegistration > templateEntryRegistrations =
                        entryRegistrationService.searchTemplateEntryRegistrationList ( companyId );
                if (CollectionUtils.isEmpty ( templateEntryRegistrations )) {
                    return failResponseResult ( "查询失败" );
                } else {
                    return new ResponseResult <> ( templateEntryRegistrations, CommonCode.SUCCESS );
                }
            } catch (Exception e) {
                return failResponseResult ( "查询异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

//    /**
//     * 生成logo背景图路径
//     * @param multipartFile
//     * @return
//     */
//    public String getLogoPath(MultipartFile multipartFile){
//        Boolean b = checkParam ( multipartFile,getUserSession () );
//        if (b) {
//            try {
//                String path="http://193.112.188.180/file/CompanyLogo/"+
//            } catch (Exception e) {
//                return failResponseResult ( "查询异常" );
//            }
//
//        }
//        return failResponseResult ( "参数错误或者session错误" );
//    }

    /**
     * 新增入职登记模板
     */
    @RequestMapping(value = "/addTemplateEntryRegistration", method = RequestMethod.POST)
    @ApiOperation(value = "新增入职登记模板", notes = "hkt")
    public ResponseResult addTemplateEntryRegistration(@RequestBody TemplateEntryRegistration templateEntryRegistration) {
        Boolean b = checkParam ( templateEntryRegistration );
        if (b) {
            try {
                entryRegistrationService.addTemplateEntryRegistration ( templateEntryRegistration );
                return new ResponseResult ( null, CommonCode.SUCCESS );
            } catch (Exception e) {
                return failResponseResult ( "查询异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 删除入职登记模板
     *
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/deleteTemplateEntryRegistration", method = RequestMethod.GET)
    @ApiOperation(value = "新增入职登记模板", notes = "hkt")
    public ResponseResult deleteTemplateEntryRegistration(Integer templateId) {
        Boolean b = checkParam ( templateId, getUserSession () );
        if (b) {
            try {
                entryRegistrationService.deleteTemplateEntryRegistration ( templateId, getUserSession ().getArchiveId () );
                return new ResponseResult ( null, CommonCode.SUCCESS );
            } catch (Exception e) {
                return failResponseResult ( "删除异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 修改入职登记模板
     *
     * @param templateEntryRegistration
     * @return
     */
    @RequestMapping(value = "/modifyTemplateEntryRegistration", method = RequestMethod.POST)
    @ApiOperation(value = "新增入职登记模板", notes = "hkt")
    public ResponseResult modifyTemplateEntryRegistration(@RequestBody TemplateEntryRegistration
                                                                  templateEntryRegistration) {
        Boolean b = checkParam ( templateEntryRegistration );
        if (b) {
            try {
                entryRegistrationService.modifyTemplateEntryRegistration ( templateEntryRegistration );
                return new ResponseResult ( null, CommonCode.SUCCESS );
            } catch (Exception e) {
                return failResponseResult ( "修改异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 根据模板ID获取模板详情
     *
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/getTemplateEntryRegistrationByTemplateId", method = RequestMethod.POST)
    @ApiOperation(value = "根据模板ID获取模板详情", notes = "hkt")
    public ResponseResult < TemplateEntryRegistration > getTemplateEntryRegistrationByTemplateId(Integer templateId) {
        Boolean b = checkParam ( templateId );
        if (b) {
            try {

                TemplateEntryRegistration templateEntryRegistrationByTemplateId =
                        entryRegistrationService.getTemplateEntryRegistrationByTemplateId ( templateId );
                return new ResponseResult <> ( templateEntryRegistrationByTemplateId, CommonCode.SUCCESS );
            } catch (Exception e) {
                return failResponseResult ( "展示异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 根据模板ID查询模板附件配置列表
     *
     * @param templateId 模板ID
     * @param isAll      是否显示全部(0：是[包含系统默认且未配置的信息]，1：否[仅显示模板已配置的附件信息])
     * @return
     */
    @RequestMapping(value = "/searchTemplateAttachmentListByTemplateId", method = RequestMethod.GET)
    @ApiOperation(value = "根据模板ID查询模板附件配置列表", notes = "hkt")
    public ResponseResult < List < TemplateAttachmentGroupVO > > searchTemplateAttachmentListByTemplateId(Integer templateId, Integer isAll) {
        Boolean b = checkParam ( templateId );
        if (b) {
            try {

                List < TemplateAttachmentGroupVO > templateAttachmentGroupVOS =
                        entryRegistrationService.searchTemplateAttachmentListByTemplateId ( templateId, isAll );
                if (CollectionUtils.isEmpty ( templateAttachmentGroupVOS )) {
                    return new ResponseResult <> ( null, CommonCode.FAIL_VALUE_NULL );

                } else {
                    return new ResponseResult <> ( templateAttachmentGroupVOS, CommonCode.SUCCESS );
                }
            } catch (Exception e) {
                return failResponseResult ( "展示异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 根据模板附件ID查询模板附件详情
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/getTemplateAttachmentListByTagId", method = RequestMethod.GET)
    @ApiOperation(value = "根据模板ID查询模板附件配置列表", notes = "hkt")
    public ResponseResult<TemplateAttachmentGroupVO>  getTemplateAttachmentListByTagId(Integer tagId) {
        Boolean b = checkParam ( tagId );
        if (b) {
            try {
                TemplateAttachmentGroupVO templateAttachmentListByTagId = entryRegistrationService.getTemplateAttachmentListByTagId ( tagId );
                return new ResponseResult <> ( templateAttachmentListByTagId, CommonCode.FAIL_VALUE_NULL );
            } catch (Exception e) {
                return failResponseResult ( "展示异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 修改模板附件信息
     *
     * @param templateAttachmentGroup
     * @return
     */
    @RequestMapping(value = "/modifyTemplateAttachmentGroup", method = RequestMethod.GET)
    @ApiOperation(value = "修改模板附件信息", notes = "hkt")
    public ResponseResult modifyTemplateAttachmentGroup(TemplateAttachmentGroup templateAttachmentGroup) {
        Boolean b = checkParam ( templateAttachmentGroup );
        if (b) {
            try {
                entryRegistrationService.modifyTemplateAttachmentGroup ( templateAttachmentGroup );
                return new ResponseResult <> ( null, CommonCode.SUCCESS );

            } catch (Exception e) {
                return failResponseResult ( "修改异常" );
            }

        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 新增模板附件信息
     * @param templateAttachmentGroup
     * @return
     */
    @RequestMapping(value = "/addTemplateAttachmentGroup", method = RequestMethod.GET)
    @ApiOperation(value = "新增模板附件信息", notes = "hkt")
    public ResponseResult addTemplateAttachmentGroup(TemplateAttachmentGroup templateAttachmentGroup) {
        Boolean b = checkParam ( templateAttachmentGroup );
        if (b) {
            try {
                entryRegistrationService.addTemplateAttachmentGroup ( templateAttachmentGroup );
                return new ResponseResult <> ( null, CommonCode.SUCCESS );

            } catch (Exception e) {
                return failResponseResult ( "新增异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }


    /**
     * 删除模板附件信息
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/delTemplateAttachmentGroup", method = RequestMethod.GET)
    @ApiOperation(value = "删除模板附件信息", notes = "hkt")
    public ResponseResult delTemplateAttachmentGroup(Integer tagId) {
        Boolean b = checkParam ( tagId,getUserSession () );
        if (b) {
            try {
                entryRegistrationService.delTemplateAttachmentGroup ( tagId,getUserSession ().getArchiveId () );
                return new ResponseResult <> ( null, CommonCode.SUCCESS );

            } catch (Exception e) {
                return failResponseResult ( "删除异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 模板附件排序
     * @param templateAttachmentGroupList
     * @return
     */
    @RequestMapping(value = "/delTemplateAttachmentGroup", method = RequestMethod.POST)
    @ApiOperation(value = "模板附件排序", notes = "hkt")
    public ResponseResult sortTemplateAttachmentGroup(List<TemplateAttachmentGroup> templateAttachmentGroupList) {
        Boolean b = checkParam ( templateAttachmentGroupList,getUserSession () );
        if (b) {
            try {
                entryRegistrationService.sortTemplateAttachmentGroup ( templateAttachmentGroupList, getUserSession ().getArchiveId () );
                return new ResponseResult <> ( null, CommonCode.SUCCESS );

            } catch (Exception e) {
                return failResponseResult ( "排序异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 根据企业ID和模板ID查询自定义表列表
     * 根据模板id或者全量查询企业下的自定义表，新增展示自定义表名时可复用此接口全量查询
     * @param templateId 模板ID
     * @param isAll 是否显示全部表(1:显示全部自定义表,0:显示模板对应的自定义表)
     * @return
     */
    @RequestMapping(value = "/searchTableListByCompanyIdAndTemplateId", method = RequestMethod.POST)
    @ApiOperation(value = "根据企业ID和模板ID查询自定义表列表", notes = "hkt")
    public ResponseResult <List < TemplateCustomTableVO >> searchTableListByCompanyIdAndTemplateId(Integer templateId,Integer isAll) {
        Boolean b = checkParam (templateId,isAll,getUserSession () );
        if (b) {
            try {
                List < TemplateCustomTableVO > templateCustomTableVOS =
                        templateCustomTableFieldService.searchTableListByCompanyIdAndTemplateId ( getUserSession ().getCompanyId (), templateId, isAll );
                if(!CollectionUtils.isEmpty ( templateCustomTableVOS )){
                    return new ResponseResult <> ( templateCustomTableVOS,CommonCode.SUCCESS );
                }else {
                    return new ResponseResult <> ( null, CommonCode.FAIL_VALUE_NULL );
                }
            } catch (Exception e) {
                return failResponseResult ( "获取值异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 根据模板ID查询所有表字段信息
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/searchTableFieldListByTemplateId", method = RequestMethod.POST)
    @ApiOperation(value = "根据企业ID和模板ID查询自定义表列表", notes = "hkt")
    public ResponseResult <List < TemplateCustomTableVO >> searchTableFieldListByTemplateId(Integer templateId) {
        Boolean b = checkParam (templateId );
        if (b) {
            try {
                List < TemplateCustomTableVO > templateCustomTableVOS =
                        templateCustomTableFieldService.searchTableFieldListByTemplateId (templateId );
                if(!CollectionUtils.isEmpty ( templateCustomTableVOS )){
                    return new ResponseResult <> ( templateCustomTableVOS,CommonCode.SUCCESS );
                }else {
                    return new ResponseResult <> ( null, CommonCode.FAIL_VALUE_NULL );
                }
            } catch (Exception e) {
                return failResponseResult ( "获取值异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 根据表ID和模板ID查询对应表字段配置信息
     * @param tableId
     * @param templateId
     * @return
     */
    @RequestMapping(value = "/searchFieldListByTableIdAndTemplateId", method = RequestMethod.POST)
    @ApiOperation(value = "根据表ID和模板ID查询对应表字段配置信息", notes = "hkt")
    public ResponseResult <List < TemplateCustomTableFieldVO >> searchTableFieldListByTemplateId(Integer tableId,Integer templateId) {
        Boolean b = checkParam (templateId,tableId );
        if (b) {
            try {

                List < TemplateCustomTableFieldVO > templateCustomTableFieldVOS
                        = templateCustomTableFieldService.searchFieldListByTableIdAndTemplateId ( tableId, templateId );
                if(!CollectionUtils.isEmpty ( templateCustomTableFieldVOS )){
                    return new ResponseResult <> ( templateCustomTableFieldVOS,CommonCode.SUCCESS );
                }else {
                    return new ResponseResult <> ( null, CommonCode.FAIL_VALUE_NULL );
                }
            } catch (Exception e) {
                return failResponseResult ( "获取值异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    /**
     * 保存自定义表字段配置
     * @param templateId
     * @param templateCustomTableList
     * @return
     */
    @RequestMapping(value = "/saveTemplateTableField", method = RequestMethod.POST)
    @ApiOperation(value = "根据表ID和模板ID查询对应表字段配置信息", notes = "hkt")
    public ResponseResult <List < TemplateCustomTableFieldVO >> saveTemplateTableField(Integer templateId,
                                                                                                 List<TemplateCustomTableVO> templateCustomTableList) {
        Boolean b = checkParam (templateId,templateCustomTableList,getUserSession () );
        if (b) {
            try {
                templateCustomTableFieldService.saveTemplateTableField ( templateId,templateCustomTableList,userSession.getArchiveId () );
                    return new ResponseResult <> ( null, CommonCode.BUSINESS_EXCEPTION );
            } catch (Exception e) {
                return failResponseResult ( "更改异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 根据模板ID和预入职查询自定义表及字段信息
     * templateId：模板ID
     * preId：预入职ID
     * @param templateId 模板ID
     * @param preId 预入职ID
     * @return
     */
    @RequestMapping(value = "/searchCustomTableListByTemplateIdAndArchiveId", method = RequestMethod.POST)
    @ApiOperation(value = "根据表ID和模板ID查询对应表字段配置信息", notes = "hkt")
    public ResponseResult <List < EntryRegistrationTableVO >> searchCustomTableListByTemplateIdAndArchiveId(Integer templateId,Integer preId) {
        Boolean b = checkParam (templateId,preId );
        if (b) {
            try {
                List < EntryRegistrationTableVO > entryRegistrationTableVOS =
                        templateCustomTableFieldService.searchCustomTableListByTemplateIdAndArchiveId ( templateId, preId );
                if(!CollectionUtils.isEmpty ( entryRegistrationTableVOS )){
                    return new ResponseResult <> ( entryRegistrationTableVOS, CommonCode.SUCCESS);
                }else{
                    return new ResponseResult <> ( entryRegistrationTableVOS, CommonCode.FAIL_VALUE_NULL );
                }
            } catch (Exception e) {
                return failResponseResult ( "更改异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }
    /**
     * 处理自定义表字段数据回填
     * 目前一家企业只有一个预入职模板
     * @param preId
     * @return
     */
    @RequestMapping(value = "/handlerCustomTableGroupFieldList", method = RequestMethod.POST)
    @ApiOperation(value = "根据表ID和模板ID查询对应表字段配置信息", notes = "hkt")
    public ResponseResult <List < EntryRegistrationTableVO >> handlerCustomTableGroupFieldList(Integer preId) {
        Boolean b = checkParam (getUserSession (),preId );
        if (b) {
            try {
                List < EntryRegistrationTableVO > list =
                        preTemplateService.handlerCustomTableGroupFieldList ( getUserSession ().getCompanyId (), preId );
                if(CollectionUtils.isEmpty ( list )){
                    return new ResponseResult <> ( list, CommonCode.SUCCESS);
                }else{
                    return new ResponseResult <> ( null, CommonCode.FAIL_VALUE_NULL );
                }
            } catch (Exception e) {
                return failResponseResult ( "更改异常" );
            }
        }
        return failResponseResult ( "参数错误或者session错误" );
    }

    private Boolean checkParam(Object... params) {
        for (Object param : params) {
            if (null == param || "".equals ( param )) {
                return false;
            }
        }
        return true;
    }

    private ResponseResult failResponseResult(String message) {
        ResponseResult fail = ResponseResult.FAIL ();
        fail.setMessage ( message );
        logger.error ( message );
        return fail;
    }
}