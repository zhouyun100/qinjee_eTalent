package com.qinjee.masterdata.model.vo.staff;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@ToString
public class StatusChangeVo {
    /**
     * id
     */
    private Integer id;
    /**
     * 变更状态
     */
    @NotNull
    private String changeState;
    /**
     * 延期入职时间
     *
     */
    private Date delayTime;

    /**
     * 放弃原因
     */
    @NotNull
    private String abandonReason;

    /**
     * 变更描述
     */
    private String changeRemark;

}
