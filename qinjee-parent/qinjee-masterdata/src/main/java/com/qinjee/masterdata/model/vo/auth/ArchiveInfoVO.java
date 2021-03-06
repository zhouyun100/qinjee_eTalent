/**
 * 文件名：ArchiveInfoVO
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/19
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.model.vo.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 周赟
 * @version 1.0.0
 * @date  2019年09月19日
 */
@ApiModel(description = "档案信息类")
@Data
@NoArgsConstructor
@JsonInclude
public class ArchiveInfoVO implements Serializable {

    /**
     * 档案ID
     */
    @ApiModelProperty("档案ID")
    private Integer archiveId;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String userName;

    /**
     * 工号
     */
    @ApiModelProperty("工号")
    private String employeeNumber;

    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private String gender;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Integer orgId;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String businessUnitName;

    /**
     * 部门
     */
    @ApiModelProperty("部门")
    private String deptName;

    /**
     * 部门全称
     */
    @ApiModelProperty("部门全称")
    private String deptFullName;

    /**
     * 岗位
     */
    @ApiModelProperty("岗位")
    private String postName;


}
