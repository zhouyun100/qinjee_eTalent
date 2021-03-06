package com.qinjee.masterdata.model.vo.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude
public class AttchmentRecordVo implements Serializable {
    private Integer attachmentId;
    private String userName;
    private Integer orgId;
    private String employeeNumber;
    private String businessUnitName;
    private String orgName;
    private String attatchmentUrl;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件名称
     */
    private String fileName;
    private String creatTime;
}
