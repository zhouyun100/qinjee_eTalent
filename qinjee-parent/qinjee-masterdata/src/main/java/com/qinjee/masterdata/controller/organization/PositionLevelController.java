package com.qinjee.masterdata.controller.organization;

import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.PositionLevel;
import com.qinjee.masterdata.model.vo.organization.PositionLevelVo;
import com.qinjee.masterdata.service.organation.PositionLevelService;
import com.qinjee.model.request.PageVo;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 彭洪思
 * @version 1.0.1
 * @Description TODO
 * @createTime 2020年03月01日 10:13:00
 */
@RestController
@RequestMapping("/positionLevel")
@Api(tags = "【机构管理】职级接口")
public class PositionLevelController extends BaseController {

    @Autowired
    private PositionLevelService positionLevelService;

    private Boolean checkParam(Object... params) {
        for (Object param : params) {
            if (param instanceof UserSession) {
                if (null == param || "".equals(param)) {
                    ExceptionCast.cast(CommonCode.INVALID_SESSION);
                    return false;
                }
            }
            if (null == param || "".equals(param)) {
                return false;
            }
        }
        return true;
    }

    @GetMapping("/get{id}")
    @ApiOperation(value = "根据id查询职级", notes = "彭洪思")
    public ResponseResult<PositionLevelVo> get(@PathVariable("id") Integer id) {
        if (!checkParam( id)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        PositionLevelVo positionLevelVo = positionLevelService.getPositionLevelById(id);
        return new ResponseResult<>(positionLevelVo);
    }
    @GetMapping("/list")
    @ApiOperation(value = "分页查询职级列表", notes = "彭洪思")
    public ResponseResult<PageResult<PositionLevelVo>> list( PageVo pageVo) {
        if (!checkParam(getUserSession(), pageVo)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        PageResult<PositionLevelVo> positionLevelList = positionLevelService.listPositionLevel(pageVo, getUserSession());
        return new ResponseResult<>(positionLevelList);
    }
    @GetMapping("/listByPositionGradeId/{id}")
    @ApiOperation(value = "根据职等id查询职级列表", notes = "彭洪思")
    public ResponseResult<List<PositionLevelVo>> listByPositionGradeId(@PathVariable("id") Integer id) {
        if (!checkParam( id)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        List<PositionLevelVo> positionLevelList = positionLevelService.listByPositionGradeId(id);
        return new ResponseResult<>(positionLevelList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增职级", notes = "彭洪思")
    public ResponseResult add(@RequestBody PositionLevel positionLevel) {
        if (!checkParam(getUserSession(), positionLevel)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        int i = positionLevelService.addPositionLevel(positionLevel, getUserSession());
        if (i > 0) {
            return new ResponseResult(i);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑职级", notes = "彭洪思")
    public ResponseResult edit(@RequestBody PositionLevel positionLevel) {
        if (!checkParam(getUserSession(), positionLevel)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        int i = positionLevelService.editPositionLevel(getUserSession(), positionLevel);
        if (i > 0) {
            return new ResponseResult(i);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除职级", notes = "彭洪思")
    public ResponseResult delete(@RequestBody List<Integer> positionLevelIds) {
        if (!checkParam(getUserSession(), positionLevelIds)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        int i = positionLevelService.batchDeletePositionLevel(positionLevelIds, getUserSession());
        if (i > 0) {
            return new ResponseResult(i);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

    @PostMapping("/sort")
    @ApiOperation(value = "职级排序", notes = "彭洪思")
    public ResponseResult sort(@RequestBody List<Integer> positionLevelIds) {
        if (!checkParam(getUserSession(), positionLevelIds)) {
            return new ResponseResult<>(null, CommonCode.INVALID_PARAM);
        }
        int i = positionLevelService.sortPositionLevel(positionLevelIds, getUserSession());
        return new ResponseResult(i);
    }

}
