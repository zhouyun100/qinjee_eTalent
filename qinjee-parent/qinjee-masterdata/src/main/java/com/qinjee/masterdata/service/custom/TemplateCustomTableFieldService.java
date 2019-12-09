/**
 * 文件名：TemplateCustomTableFieldService
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/12/9
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.custom;

import com.qinjee.masterdata.model.vo.custom.EntryRegistrationTableVO;
import com.qinjee.masterdata.model.vo.custom.TemplateCustomTableFieldVO;
import com.qinjee.masterdata.model.vo.custom.TemplateCustomTableVO;

import java.util.List;
import java.util.Map;

/**
 * 预入职登记自定义表字段设置Service
 * @author 周赟
 * @date 2019/12/9
 */
public interface TemplateCustomTableFieldService {

    /**
     * 根据企业ID和模板ID查询自定义表列表
     * @param companyId 企业ID
     * @param templateId 模板ID
     * @param isAll 是否显示全部表(1:显示全部自定义表,0:显示模板对应的自定义表)
     * @return
     */
    List<TemplateCustomTableVO> searchTableListByCompanyIdAndTemplateId(Integer companyId, Integer templateId, Integer isAll);

    /**
     * 根据模板ID查询所有表字段信息
     * @param templateId
     * @return
     */
    List<TemplateCustomTableVO> searchTableFieldListByTemplateId(Integer templateId);

    /**
     * 根据表ID和模板ID查询对应表字段配置信息
     * @param tableId
     * @param templateId
     * @return
     */
    List<TemplateCustomTableFieldVO> searchFieldListByTableIdAndTemplateId(Integer tableId, Integer templateId);

    /**
     * 保存自定义表字段配置
     * @param templateId
     * @param templateCustomTableList
     * @param operatorId
     * @return
     */
    int saveTemplateTableField(Integer templateId, List<TemplateCustomTableVO> templateCustomTableList, Integer operatorId);

    /**
     * 根据模板ID和档案查询自定义表及字段信息
     * templateId:模板ID
     * archiveId:档案ID
     * @param templateId
     * @param archiveId
     * @return
     */
    List<EntryRegistrationTableVO> searchCustomTableListByTemplateIdAndArchiveId(Integer templateId, Integer archiveId);

    /**
     * 处理自定义表字段数据回填
     * @param entryRegistrationTableList 自定义表字段列表
     * @param mapValue 表单各字段数据值列表
     * @return
     */
    List<EntryRegistrationTableVO> handlerCustomTableGroupFieldList(List<EntryRegistrationTableVO> entryRegistrationTableList, Map<Integer,String> mapValue);
}
