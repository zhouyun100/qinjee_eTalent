package com.qinjee.masterdata.model.vo.organization.bo;

import com.qinjee.masterdata.model.vo.staff.FieldValueForSearch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 彭洪思
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020年02月28日 16:39:00
 */
@ApiModel(description = "岗位分页实体Vo类")
@Data
public class PostPageBO implements Serializable {
  private Integer currentPage;
  private Integer pageSize;
  @ApiModelProperty(value = "机构ID", example = "1")
  private Integer orgId;
  @ApiModelProperty(value = "岗位ID", example = "1")
  private Integer postId;
  @ApiModelProperty(value = "父级岗位ID", example = "1")
  private Integer parentPostId;

  @ApiModelProperty("是否删除 0未删除、1已删除")
  private Short isDelete;
  private List<FieldValueForSearch> tableHeadParamList;

  @ApiModelProperty("是否含有封存 0不含有、1含有")
  private Short isEnable;

}
