package com.qinjee.masterdata.controller.organization;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.UserArchive;
import com.qinjee.masterdata.model.vo.organization.page.UserArchivePageVo;
import com.qinjee.masterdata.model.vo.organization.UserArchiveVo;
import com.qinjee.masterdata.service.organation.UserArchiveService;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 高雄
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年09月12日 10:44:00
 */
@Api(tags = "【机构管理】员工档案信息接口")
@RequestMapping("/userArchive")
@RestController
public class UserArchiveController extends BaseController {

  @Autowired
  private UserArchiveService userArchiveService;

  @PostMapping("/getUserArchiveList")
  @ApiOperation(value = "根据条件分页查询员工信息", notes = "高雄")
  public ResponseResult<PageResult<UserArchive>> getUserArchiveList(@RequestBody UserArchivePageVo pageQueryVo) {

    return userArchiveService.getUserArchiveList(pageQueryVo, getUserSession());
  }

  @PostMapping("/addUserArchive")
  @ApiOperation(value = "新增员工档案信息", notes = "高雄")
  public ResponseResult<Integer> addUserArchive(UserArchiveVo userArchiveVo) {
    return userArchiveService.addUserArchive(userArchiveVo, getUserSession());
  }

  @GetMapping("/deleteUserArchive")
  @ApiOperation(value = "删除员工档案信息", notes = "高雄")
  public ResponseResult deleteUserArchive(@ApiParam(value = "员工档案Id", required = true, allowMultiple = true) List<Integer> archiveIds) {
    return userArchiveService.deleteUserArchive(archiveIds);
  }


}
