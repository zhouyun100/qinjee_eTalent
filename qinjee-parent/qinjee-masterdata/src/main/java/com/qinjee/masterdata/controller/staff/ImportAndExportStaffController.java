package com.qinjee.masterdata.controller.staff;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.vo.custom.CheckCustomTableVO;
import com.qinjee.masterdata.model.vo.staff.ExportRequest;
import com.qinjee.masterdata.model.vo.staff.export.ExportFile;
import com.qinjee.masterdata.service.staff.IStaffImportAndExportService;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/port")
@Api(tags = "【导入导出】文件导入导出接口")
public class ImportAndExportStaffController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ImportAndExportStaffController.class);
    @Autowired
    private IStaffImportAndExportService staffImportAndExportService;

    /**
     * 文件类型校验以及生成list
     */
    @RequestMapping(value = "/importFileAndCheckFile", method = RequestMethod.POST)
    @ApiOperation(value = "文件类型校验以及生成list", notes = "hkt")
    public ResponseResult< List< Map< Integer,String>>> importFileAndCheckFile(MultipartFile multipartFile,String funcCode) {
        Boolean b = checkParam(multipartFile,getUserSession ());
        if(b) {
            try {
                List < Map < Integer, String > > list = staffImportAndExportService.importFileAndCheckFile ( multipartFile,funcCode,getUserSession ());
                return new ResponseResult <> (list, CommonCode.SUCCESS);
            } catch (IOException e) {
                return new ResponseResult <> (null, CommonCode. FILE_PARSING_EXCEPTION);
            } catch (Exception e) {
                return new ResponseResult <> (null, CommonCode.BUSINESS_EXCEPTION);
            }

        }
        return new ResponseResult <> (null, CommonCode.FILE_EMPTY);
    }

    /**
     * 校验所传的字段
     */
    @RequestMapping(value = "/checkField", method = RequestMethod.POST)
    @ApiOperation(value = "校验所传的字段", notes = "hkt")
//    @ApiImplicitParam(name = "path", value = "文件路径", paramType = "query", required = true)

    public ResponseResult<List<CheckCustomTableVO>> checkFile(@RequestBody  List< Map< Integer,String>> list,String funcCode) {
        Boolean b = checkParam(list,funcCode);
        if(b) {
            try {
                 List<CheckCustomTableVO> list1=staffImportAndExportService.checkFile ( list,funcCode );
                return new ResponseResult <> (list1, CommonCode.SUCCESS);
            } catch (Exception e) {
                return new ResponseResult <> (null, CommonCode. BUSINESS_EXCEPTION);
            }
        }
        return new ResponseResult <> (null, CommonCode.INVALID_PARAM);
    }
    /**
     * 准备导入文件
     */
    @RequestMapping(value = "/readyForImport", method = RequestMethod.POST)
    @ApiOperation(value = "准备导入文件", notes = "hkt")

    public ResponseResult<List< CheckCustomTableVO >> readyForImport(List< Map< Integer,String>> list, UserSession userSession,String title) {
        Boolean b = checkParam(list,userSession,title);
        if(b) {
            try {
                 staffImportAndExportService.readyForImport ( list,userSession,title );
                return new ResponseResult <> (null, CommonCode.SUCCESS);
            } catch (Exception e) {
                return new ResponseResult <> (null, CommonCode. BUSINESS_EXCEPTION);
            }
        }
        return new ResponseResult <> (null, CommonCode.INVALID_PARAM);
    }
    /**
     * 取消导入文件
     */
    @RequestMapping(value = "/cancelForImport", method = RequestMethod.POST)
    @ApiOperation(value = "取消导入文件", notes = "hkt")

    public ResponseResult readyForImport(String title) {
        Boolean b = checkParam(title,getUserSession ());
        if(b) {
            try {
                staffImportAndExportService.cancelForImport (title,getUserSession () );
                return new ResponseResult <> (null, CommonCode.SUCCESS);
            } catch (Exception e) {
                return new ResponseResult <> (null, CommonCode. REDIS_KEY_EXCEPTION);
            }
        }
        return new ResponseResult <> (null, CommonCode.INVALID_PARAM);
    }
    /**
     * 导入文件
     */
    @RequestMapping(value = "/importFile", method = RequestMethod.GET)
    @ApiOperation(value = "导入文件", notes = "hkt")

    public ResponseResult ImportFile(String title,  String funcCode) {
        Boolean b = checkParam(title,getUserSession (),funcCode);
        if(b) {
            try {
                staffImportAndExportService.importFile (title,getUserSession (),funcCode );
                return new ResponseResult <> (null, CommonCode.SUCCESS);
            } catch (Exception e) {
                return new ResponseResult <> (null, CommonCode. REDIS_KEY_EXCEPTION);
            }
        }
        return new ResponseResult <> (null, CommonCode.INVALID_PARAM);
    }

    /**
     * 模板导入黑名单
     */
    @RequestMapping(value = "/importBlaFile", method = RequestMethod.POST)
    @ApiOperation(value = "模板导入黑名单", notes = "hkt")
//    @ApiImplicitParam(name = "path", value = "文件路径", paramType = "query", required = true)

    public ResponseResult importBlaFile(MultipartFile multipartFile,String funcCode) {
        Boolean b = checkParam(multipartFile,getUserSession(),funcCode);
        if(b){
            try {
                staffImportAndExportService.importBlaFile(multipartFile,getUserSession(),funcCode);
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导入失败");
            }
        }
        return  failResponseResult("文件内容错误");
    }
    /**
     * 模板导入合同
     */
    @RequestMapping(value = "/importConFile", method = RequestMethod.POST)
    @ApiOperation(value = "模板导入合同", notes = "hkt")
//    @ApiImplicitParam(name = "path", value = "文件路径", paramType = "query", required = true)

    public ResponseResult importConFile(MultipartFile multipartFile,String funcCode) {
        Boolean b = checkParam(multipartFile,funcCode,getUserSession());
        if(b){
            try {
                staffImportAndExportService.importConFile(multipartFile,funcCode,getUserSession());
                return ResponseResult.SUCCESS();
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导入失败");
            }
        }
        return  failResponseResult("文件内容错误");
    }


    /**
     * 模板导出档案
     */
    @RequestMapping(value = "/exportArcFile", method = RequestMethod.POST)
    @ApiOperation(value = "导出档案模板", notes = "hkt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", value = "文档下载路径", paramType = "query", required = true),
//            @ApiImplicitParam(name = "title", value = "excel标题", paramType = "query", required = true),
//            @ApiImplicitParam(name = "QuerySchemeId", value = "查询方案id", paramType = "query", required = true),
//            @ApiImplicitParam(name = "list", value = "人员id集合", paramType = "query", required = true),
//    })
    //导出的文件应该是以.xls结尾
    public ResponseResult exportArcFile(@RequestBody @Valid ExportFile exportFile, HttpServletResponse response) {
        Boolean b = checkParam(exportFile,response,getUserSession ());
        if(b){
            try {
                staffImportAndExportService.exportArcFile(exportFile,response,getUserSession ());
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导出失败");
            }
        }
        return  failResponseResult("参数错误");
    }


    /**
     * 模板导出预入职
     */
    @RequestMapping(value = "/exportPreFile", method = RequestMethod.POST)
    @ApiOperation(value = "导出预入职模板", notes = "hkt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", value = "文档下载路径", paramType = "query", required = true),
//            @ApiImplicitParam(name = "title", value = "excel标题", paramType = "query", required = true),
//            @ApiImplicitParam(name = "list", value = "预入职id集合", paramType = "query", required = true),
//    })

    public ResponseResult exportPreFile(@RequestBody ExportRequest exportRequest,HttpServletResponse response) {
        Boolean b = checkParam(exportRequest,response,getUserSession ());
        if(b){
            try {
                staffImportAndExportService.exportPreFile(exportRequest,response,getUserSession () );
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导出失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 模板导出黑名单
     */
    @RequestMapping(value = "/exportBlackFile", method = RequestMethod.POST)
    @ApiOperation(value = "导出黑名单模板", notes = "hkt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", value = "文档下载路径", paramType = "query", required = true),
//            @ApiImplicitParam(name = "title", value = "excel标题", paramType = "query", required = true),
//            @ApiImplicitParam(name = "list", value = "预入职id集合", paramType = "query", required = true),
//    })

    public ResponseResult exportBlackFile(@RequestBody ExportRequest exportRequest,HttpServletResponse response) {
        Boolean b = checkParam(exportRequest,response,getUserSession ());
        if(b){
            try {
                staffImportAndExportService.exportBlackFile(exportRequest,response,getUserSession());
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导出失败");
            }
        }
        return  failResponseResult("参数错误");
    }
    /**
     * 模板导出合同表
     */
    @RequestMapping(value = "/exportContractList", method = RequestMethod.POST)
    @ApiOperation(value = "模板导出合同表", notes = "hkt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", value = "文档下载路径", paramType = "query", required = true),
//            @ApiImplicitParam(name = "title", value = "excel标题", paramType = "query", required = true),
//            @ApiImplicitParam(name = "list", value = "预入职id集合", paramType = "query", required = true),
//    })

    public ResponseResult exportContractList(@RequestBody ExportRequest exportRequest, HttpServletResponse response) {
        Boolean b = checkParam(exportRequest,response,getUserSession ());
        if(b){
            try {
                staffImportAndExportService.exportContractList(exportRequest,response,getUserSession());
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return failResponseResult("导出失败");
            }
        }
        return  failResponseResult("参数错误");
    }

    /**
     * 模板导出业务类
     */
    @RequestMapping(value = "/exportBusiness", method = RequestMethod.POST)
    @ApiOperation(value = "模板导出业务类", notes = "hkt")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", value = "文档下载路径", paramType = "query", required = true),
//            @ApiImplicitParam(name = "title", value = "excel标题", paramType = "query", required = true),
//            @ApiImplicitParam(name = "list", value = "预入职id集合", paramType = "query", required = true),
//    })

    public ResponseResult exportBusiness(@RequestBody ExportRequest exportRequest,HttpServletResponse response,String funcCode) {
        Boolean b = checkParam(exportRequest,response,getUserSession());
        if(b){
            try {
                staffImportAndExportService.exportBusiness(exportRequest,response,getUserSession(),funcCode);
                return null;
            } catch (Exception e) {
                e.printStackTrace ();
                return failResponseResult("导出失败");
            }
        }
        return  failResponseResult("参数错误");
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
        logger.error(message);
        return fail;
    }
}
