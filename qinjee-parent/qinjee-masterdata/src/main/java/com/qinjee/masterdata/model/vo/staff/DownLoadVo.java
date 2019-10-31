package com.qinjee.masterdata.model.vo.staff;

import com.qinjee.utils.QueryColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
@Data
public class DownLoadVo implements Serializable {
    /**
     * 档案ID
     */
    @ApiModelProperty("档案ID")
    private Integer archiveId;

    /**
     * 工号
     */
    @NotNull
    @ApiModelProperty("工号")
    private String employeeNumber;

    /**
     * 企业ID
     */
    @NotNull
    @ApiModelProperty("企业ID")
    private Integer companyId;


    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    private String businessUnitName;


    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String orgName;

    /**
     * 岗位名称
     */
    @ApiModelProperty("岗位名称")
    private String postName;

    /**
     * 姓名
     */
    @NotNull
    @QueryColumn("tua.user_name")
    @ApiModelProperty("姓名")
    private String userName;

    /**
     * 试用到期时间
     */
    @ApiModelProperty("试用到期时间")
    private Date probationDueDate;


    /**
     * 联系电话
     */
    @NotNull
    @ApiModelProperty("联系电话")
    private String tel;

    /**
     * 上级领导姓名
     */
    @ApiModelProperty("上级领导姓名")
    private Integer supervisorUserName;

    /**
     * 任职类型
     */
    private String employmentType;
    private static final long serialVersionUID = 1L;

}
