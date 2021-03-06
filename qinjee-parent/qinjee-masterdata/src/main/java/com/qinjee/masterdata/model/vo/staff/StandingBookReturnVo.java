package com.qinjee.masterdata.model.vo.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@JsonInclude
@Data
public class StandingBookReturnVo implements Serializable {
    /**
     * 台账id
     */
    private Integer stangdingBookId;
    /**
     * 任职类型
     */
    private List<String> archiveType;
    /**
     * 企业id
     */
    private List <Integer> orgIdList;
    /**
     * 兼职类型
     */
    private List<String> type;
    /**
     * 页大小
     */
    private Integer pageSize;
    /**
     * 当前页
     */
    private Integer currentPage;
    /**
     * 总条数
     */
    private Integer total;
    /**
     * 查询方案id
     */
    private Integer querySchemaId;
}
