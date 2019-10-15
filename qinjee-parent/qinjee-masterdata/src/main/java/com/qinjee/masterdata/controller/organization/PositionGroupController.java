package com.qinjee.masterdata.controller.organization;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.PositionGroup;
import com.qinjee.masterdata.model.vo.organization.PositionGroupVo;
import com.qinjee.masterdata.service.organation.PositionGroupService;
import com.qinjee.model.request.PageVo;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 高雄
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年09月11日 09:29:00
 */
@RestController
@RequestMapping("/positionGroup")
@Api(tags = "【机构管理】职位族接口")
public class  PositionGroupController extends BaseController {

    @Autowired
    private PositionGroupService positionGroupService;

    @ApiOperation(value = "树形展示职位", notes = "高雄")
    @GetMapping("/getAllPositionGroupTree")
    public ResponseResult<List<PositionGroup>> getAllPositionGroupTree(){
        return positionGroupService.getAllPositionGroupTree(getUserSession());
    }

    @ApiOperation(value = "获取所有的职位族", notes = "高雄")
    @GetMapping("/getAllPositionGroup")
    public ResponseResult<PageResult<PositionGroup>> getAllPositionGroup(PageVo pageVo){
        UserSession userSession = getUserSession();
        return positionGroupService.getAllPositionGroup(userSession, pageVo);
    }

    @ApiOperation(value = "新增职位族", notes = "高雄")
    @PostMapping("/addPositionGroup")
    public ResponseResult addPositionGroup(@RequestBody @ApiParam(value = "职位族名称", example = "研发族", required = true) String positionGroupName){
        return positionGroupService.addPositionGroup(getUserSession(), positionGroupName);
    }

    @ApiOperation(value = "编辑职位族", notes = "高雄")
    @PostMapping("/editPositionGroup")
    public ResponseResult editPositionGroup(@RequestBody PositionGroupVo positionGroupVo){
        return positionGroupService.editPositionGroup(getUserSession(), positionGroupVo);
    }

    @ApiOperation(value = "删除职位族", notes = "高雄")
    @PostMapping("/deletePositionGroup")
    @ApiImplicitParam(name="positionGroupIds", value = "职位族id", paramType = "query", dataType = "int", allowMultiple = true, required = true)
    public ResponseResult deletePositionGroup(@RequestParam("positionGroupId")  List<Integer> positionGroupIds){
        positionGroupService.deletePositionGroup(positionGroupIds);
        return new ResponseResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "prePositionGroupId", value = "上个职位族id", paramType = "query", dataType = "int",  required = true, example = "3"),
            @ApiImplicitParam(name = "midPositionGroupId", value = "需要排序的职位族id", paramType = "query", dataType = "int", required = true, example = "1"),
            @ApiImplicitParam(name = "nextPositionGroupId", value = "下一个职位族id", paramType = "query", dataType = "int", required = true, example = "2"),
    })
    @ApiOperation(value = "职位族排序", notes = "高雄")
    @GetMapping("/sortPositionGroup")
    public ResponseResult  sortPositionGroup (Integer prePositionGroupId,
                                              Integer midPositionGroupId,
                                              Integer nextPositionGroupId){
        return positionGroupService.sortPositionGroup(prePositionGroupId,midPositionGroupId,nextPositionGroupId);
    }

    @ApiImplicitParam(name = "positionGroupIds", value = "选择的职位族id,不传默认导出所有d", paramType = "query", dataType = "int", allowMultiple = true)
    @ApiOperation(value = "导出excel", notes = "高雄")
    @GetMapping("/downloadExcel")
    public ResponseResult downloadExcel(@RequestParam("positionGroupIds") List<Integer> positionGroupIds){
        //TODO
        return null;
    }
}