/**
 * 文件名：EntryRegistrationService
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/12/10
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.staff;

import com.qinjee.masterdata.model.entity.TemplateAttachmentGroup;
import com.qinjee.masterdata.model.entity.TemplateEntryRegistration;
import com.qinjee.masterdata.model.vo.staff.entryregistration.TemplateAttachmentGroupVO;
import com.qinjee.masterdata.model.vo.staff.entryregistration.TemplateEntryRegistrationVO;

import java.util.List;

/**
 * 入职登记Service
 * @author 周赟
 * @date 2019/12/10
 */
public interface EntryRegistrationService {

    /**
     * 根据企业ID查询入职登记模板列表
     * @param companyId
     * @return
     */
    List<TemplateEntryRegistrationVO> searchTemplateEntryRegistrationList(Integer companyId);

    /**
     * 新增入职登记模板
     * @param templateEntryRegistration
     * @return
     */
    int addTemplateEntryRegistration(TemplateEntryRegistration templateEntryRegistration);

    /**
     * 删除入职登记模板
     * @param templateId
     * @return
     */
    int deleteTemplateEntryRegistration(Integer templateId, Integer operatorId);

    /**
     * 修改入职登记模板
     * @param templateEntryRegistration
     * @return
     */
    int modifyTemplateEntryRegistration(TemplateEntryRegistration templateEntryRegistration);

    /**
     * 根据模板ID获取模板详情
     * @param templateId
     * @return
     */
    TemplateEntryRegistration getTemplateEntryRegistrationByTemplateId(Integer templateId);


    /**
     * 根据模板ID查询模板附件配置列表
     * @param templateId 模板ID
     * @param isAll 是否显示全部(0：是[包含系统默认且未配置的信息]，1：否[仅显示模板已配置的附件信息])
     * @return
     */
    List<TemplateAttachmentGroupVO> searchTemplateAttachmentListByTemplateId(Integer templateId,Integer isAll);

    /**
     * 根据模板附件ID查询模板附件详情
     * @param tagId
     * @return
     */
    TemplateAttachmentGroupVO getTemplateAttachmentListByTagId(Integer tagId);

    /**
     * 修改模板附件信息
     * @param templateAttachmentGroup
     * @return
     */
    int modifyTemplateAttachmentGroup(TemplateAttachmentGroup templateAttachmentGroup);

    /**
     * 新增模板附件信息
     * @param templateId
     * @param list
     * @param operatorId
     * @return
     */
    int addTemplateAttachmentGroup(Integer templateId, List<TemplateAttachmentGroup> list, Integer operatorId);

    /**
     * 删除模板附件信息
     * @param tagId
     * @param operatorId
     * @return
     */
    int delTemplateAttachmentGroup(Integer tagId, Integer operatorId);

    /**
     * 模板附件排序
     * @param templateAttachmentGroupList
     * @return
     */
    int sortTemplateAttachmentGroup(List<TemplateAttachmentGroup> templateAttachmentGroupList, Integer operatorId);
    /**
     * 根据模板id删除附件记录
     */
    void deleteTemplateAttachmentGroupById(Integer templateId, Integer archiveId);

}
