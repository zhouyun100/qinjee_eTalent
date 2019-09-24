package com.qinjee.masterdata.controller.organization;

import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.UserArchive;
import com.qinjee.masterdata.model.vo.organization.PageQueryVo;
import com.qinjee.masterdata.model.vo.organization.UserArchiveVo;
import com.qinjee.masterdata.service.organation.UserArchiveService;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 高雄
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年09月12日 10:44:00
 */
@Api(tags = "【机构管理】岗位用户信息接口")
@RequestMapping("/userArchive")
@RestController
public class UserArchiveController extends BaseController {

    @Autowired
    private UserArchiveService userArchiveService;

    @GetMapping("/getUserArchiveList")
    @ApiOperation(value = "根据条件分页查询用户信息", notes = "高雄")
    public ResponseResult<PageResult<UserArchive>> getUserArchiveList(PageQueryVo pageQueryVo){
        return userArchiveService.getUserArchiveList(pageQueryVo, getUserSession());
    }

    @PostMapping("/addUserArchive")
    @ApiOperation(value = "新增用户档案信息", notes = "高雄")
    public ResponseResult<Integer> addUserArchive(UserArchiveVo userArchiveVo){
        return userArchiveService.addUserArchive(userArchiveVo, getUserSession());
    }




















}
