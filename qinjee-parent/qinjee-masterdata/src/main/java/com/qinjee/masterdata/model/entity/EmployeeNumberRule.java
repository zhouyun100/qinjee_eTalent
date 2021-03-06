package com.qinjee.masterdata.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 工号规则表
 * @author
 */
@Data
@JsonInclude
@ApiModel(description = "工号规则表实体类")

public class EmployeeNumberRule implements Serializable {
    /**
     * 工号规则ID
     */
    @ApiModelProperty("工号规则ID")
    private Integer enRuleId;

    /**
     * 工号前缀
     */
    @ApiModelProperty("工号前缀")
    private String employeeNumberPrefix;

    /**
     * 日期规则
     */
    @ApiModelProperty("日期规则")
    private String dateRule;

    /**
     * 工号中缀
     */
    @ApiModelProperty("工号中缀")
    private String employeeNumberInfix;

    /**
     * 流水号位数
     */
    @ApiModelProperty("流水号位数")
    private Short digitCapacity;

    /**
     * 工号后缀
     */
    @ApiModelProperty("工号后缀")
    private String employeeNumberSuffix;

    /**
     * 企业ID
     */
    @ApiModelProperty("企业ID")
    private Integer companyId;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private Integer operatorId;

    /**
     * 创建时间
     */
   // //@DateTimeFormat(pattern = "yyyy-MM-dd" )
    ////@JSONField(format = "yyyy-MM-dd ")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
   // //@DateTimeFormat(pattern = "yyyy-MM-dd" )
    ////@JSONField(format = "yyyy-MM-dd ")
    @ApiModelProperty("修改时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Short isDelete;
}
