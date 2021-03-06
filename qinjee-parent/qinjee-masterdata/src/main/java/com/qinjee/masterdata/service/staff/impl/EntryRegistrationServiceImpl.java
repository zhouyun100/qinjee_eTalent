/**
 * 文件名：EntryRegistrationServiceImpl
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/12/10
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.staff.impl;

import com.qinjee.masterdata.dao.staffdao.entryregistration.EntryRegistrationDao;
import com.qinjee.masterdata.dao.staffdao.entryregistration.TemplateAttachmentGroupDao;
import com.qinjee.masterdata.model.entity.TemplateAttachmentGroup;
import com.qinjee.masterdata.model.entity.TemplateEntryRegistration;
import com.qinjee.masterdata.model.vo.staff.entryregistration.TemplateAttachmentGroupVO;
import com.qinjee.masterdata.model.vo.staff.entryregistration.TemplateEntryRegistrationVO;
import com.qinjee.masterdata.service.staff.EntryRegistrationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 周赟
 * @date 2019/12/10
 */
@Service
public class EntryRegistrationServiceImpl implements EntryRegistrationService {

    @Autowired
    private EntryRegistrationDao entryRegistrationDao;

    @Autowired
    private TemplateAttachmentGroupDao templateAttachmentGroupDao;

    @Override
    public List<TemplateEntryRegistrationVO> searchTemplateEntryRegistrationList(Integer companyId) {
        List<TemplateEntryRegistrationVO> templateEntryRegistrationList = entryRegistrationDao.searchTemplateEntryRegistrationList(companyId);
        return templateEntryRegistrationList;
    }

    @Override
    public int addTemplateEntryRegistration(TemplateEntryRegistration templateEntryRegistration) {
        int resultCount = 0;
        resultCount += entryRegistrationDao.addTemplateEntryRegistration(templateEntryRegistration);
        return resultCount;
    }

    @Override
    public int deleteTemplateEntryRegistration(Integer templateId, Integer operatorId) {
        int resultCount = 0;
        resultCount += entryRegistrationDao.deleteTemplateEntryRegistration(templateId, operatorId);
        return resultCount;
    }

    @Override
    public int modifyTemplateEntryRegistration(TemplateEntryRegistration templateEntryRegistration) {
        int resultCount = 0;
        resultCount += entryRegistrationDao.modifyTemplateEntryRegistration(templateEntryRegistration);
        return resultCount;
    }

    @Override
    public TemplateEntryRegistration getTemplateEntryRegistrationByTemplateId(Integer templateId) {
        TemplateEntryRegistration templateEntryRegistration = entryRegistrationDao.getTemplateEntryRegistrationByTemplateId(templateId);
        return templateEntryRegistration;
    }

    @Override
    public List<TemplateAttachmentGroupVO> searchTemplateAttachmentListByTemplateId(Integer templateId, Integer isAll) {
        List<TemplateAttachmentGroupVO> templateAttachmentGroupList = templateAttachmentGroupDao.searchTemplateAttachmentListByTemplateId(templateId,isAll);
        return templateAttachmentGroupList;
    }

    @Override
    public TemplateAttachmentGroupVO getTemplateAttachmentListByTagId(Integer tagId) {
        TemplateAttachmentGroupVO templateAttachmentGroup = templateAttachmentGroupDao.getTemplateAttachmentListByTagId(tagId);
        return templateAttachmentGroup;
    }

    @Override
    public int modifyTemplateAttachmentGroup(TemplateAttachmentGroup templateAttachmentGroup) {
        int resultCount = 0;
        resultCount += templateAttachmentGroupDao.modifyTemplateAttachmentGroup(templateAttachmentGroup);
        return resultCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTemplateAttachmentGroup(Integer templateId, List<TemplateAttachmentGroup> list, Integer operatorId) {
        int resultCount = 0;
        //查询模板中的附件信息
        List<TemplateAttachmentGroupVO> templateAttachmentGroupVOList = templateAttachmentGroupDao.searchTemplateAttachmentListByTemplateId(templateId,1);
        //筛选出已经存在的附件信息
        List<TemplateAttachmentGroup> groupTempList = new ArrayList<>();
        List<TemplateAttachmentGroupVO> groupVOTempList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list) && CollectionUtils.isNotEmpty(templateAttachmentGroupVOList)){
            for(TemplateAttachmentGroupVO groupVO : templateAttachmentGroupVOList){
                for(TemplateAttachmentGroup group : list){
                    if(group.getGroupId().equals(groupVO.getGroupId())){
                        groupTempList.add(group);
                        groupVOTempList.add(groupVO);
                    }
                }
            }
        }

        //删除重复后，剩下需要新增的附件
        list.removeAll(groupTempList);
        if(CollectionUtils.isNotEmpty(list)){
            resultCount += templateAttachmentGroupDao.addTemplateAttachmentGroupBatch(list, operatorId);
        }
        //删除重复后，剩下需要删除的附件
        templateAttachmentGroupVOList.removeAll(groupVOTempList);
        if(CollectionUtils.isNotEmpty(templateAttachmentGroupVOList)){
            templateAttachmentGroupDao.delTemplateAttachmentGroupList(templateAttachmentGroupVOList, operatorId);
        }
        return resultCount;
    }

    @Override
    public int delTemplateAttachmentGroup(Integer tagId, Integer operatorId) {
        int resultCount = 0;
        resultCount += templateAttachmentGroupDao.delTemplateAttachmentGroup(tagId, operatorId);
        return resultCount;
    }

    @Override
    public int sortTemplateAttachmentGroup(List<TemplateAttachmentGroup> templateAttachmentGroupList, Integer operatorId) {
        int resultCount = 0;
        resultCount += templateAttachmentGroupDao.sortTemplateAttachmentGroup(templateAttachmentGroupList, operatorId);
        return resultCount;
    }

    @Override
    public void deleteTemplateAttachmentGroupById(Integer templateId, Integer archiveId) {
        templateAttachmentGroupDao.deleteTemplateAttachmentGroupById(templateId,archiveId);
    }


}
