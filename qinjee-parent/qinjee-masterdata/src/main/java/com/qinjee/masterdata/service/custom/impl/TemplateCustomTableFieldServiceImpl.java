/**
 * 文件名：TemplateCustomTableFieldServiceImpl
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/12/9
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.custom.impl;

import com.qinjee.masterdata.dao.custom.TemplateCustomTableFieldDao;
import com.qinjee.masterdata.model.entity.SysDict;
import com.qinjee.masterdata.model.entity.TemplateEntryRegistration;
import com.qinjee.masterdata.model.vo.staff.entryregistration.SaveTemplateVo;
import com.qinjee.masterdata.model.vo.custom.*;
import com.qinjee.masterdata.service.custom.CustomTableFieldService;
import com.qinjee.masterdata.service.custom.TemplateCustomTableFieldService;
import com.qinjee.masterdata.service.staff.EntryRegistrationService;
import com.qinjee.masterdata.service.sys.SysDictService;
import com.qinjee.model.request.UserSession;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 周赟
 * @date 2019/12/9
 */
@Service
public class TemplateCustomTableFieldServiceImpl implements TemplateCustomTableFieldService {

    @Autowired
    private TemplateCustomTableFieldDao templateCustomTableFieldDao;

    @Autowired
    private CustomTableFieldService customTableFieldService;

    @Autowired
    private EntryRegistrationService entryRegistrationService;

    @Autowired
    private SysDictService sysDictService;

    @Override
    public List < TemplateCustomTableVO > searchTableListByCompanyIdAndTemplateId(Integer companyId, Integer templateId) {
        List < TemplateCustomTableVO > templateCustomTableList = templateCustomTableFieldDao.searchTableListByCompanyIdAndTemplateId ( companyId, templateId );
        if (CollectionUtils.isNotEmpty ( templateCustomTableList )) {
            List < TemplateCustomTableFieldVO > templateCustomTableFieldList = templateCustomTableFieldDao.searchTableFieldListByTemplateId ( templateId );
            for (TemplateCustomTableVO templateCustomTableVO : templateCustomTableList) {
                List < TemplateCustomTableFieldVO > templateCustomTableFieldVOS = new ArrayList <> ();
                for (TemplateCustomTableFieldVO templateCustomTableFieldVO : templateCustomTableFieldList) {
                    if (templateCustomTableVO.getTableId ().equals ( templateCustomTableFieldVO.getTableId () )) {
                        templateCustomTableFieldVOS.add ( templateCustomTableFieldVO );
                    }
                }
                templateCustomTableVO.setFieldList ( templateCustomTableFieldVOS );
            }
        }
        return templateCustomTableList;
    }


    @Override
    public List < TemplateCustomTableVO > searchTableFieldListByTemplateId(Integer templateId) {
        List < TemplateCustomTableVO > templateCustomTableList = templateCustomTableFieldDao.searchTableListByTemplateId ( templateId );
        if (CollectionUtils.isNotEmpty ( templateCustomTableList )) {
            List < TemplateCustomTableFieldVO > templateCustomTableFieldList = templateCustomTableFieldDao.searchTableFieldListByTemplateId ( templateId );
            for (TemplateCustomTableVO templateCustomTable : templateCustomTableList) {
                List < TemplateCustomTableFieldVO > fieldList = templateCustomTableFieldList.stream ().filter ( fieldVO -> {
                    if (fieldVO.getTableId ().equals ( templateCustomTable.getTableId () )) {
                        return true;
                    } else {
                        return false;
                    }
                } ).collect ( Collectors.toList () );
                templateCustomTable.setFieldList ( fieldList );
            }
        }
        return templateCustomTableList;
    }

    @Override
    public List < TemplateCustomTableFieldVO > searchFieldListByTableIdAndTemplateId(Integer tableId, Integer templateId) {
        List < TemplateCustomTableFieldVO > templateCustomTableFieldList = templateCustomTableFieldDao.searchFieldListByTableIdAndTemplateId ( tableId, templateId );
        return templateCustomTableFieldList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveTemplateTableField(Integer templateId, List < TemplateCustomTableVO > templateCustomTableList, Integer operatorId) {
        int resultCount = 0;

        List < TemplateCustomTableVO > customTableList = templateCustomTableFieldDao.searchTableListByTemplateId ( templateId );
        List < TemplateCustomTableVO > addTemplateTableList = new ArrayList <> ();

        /**
         * 表单与DB数据对比，删除DB中多余的模板自定义表
         */
        if (CollectionUtils.isNotEmpty ( customTableList ) && CollectionUtils.isNotEmpty ( templateCustomTableList )) {
            List < TemplateCustomTableVO > delTemplateTableList = new ArrayList <> ();
            for (TemplateCustomTableVO customTableDB : customTableList) {

                for (TemplateCustomTableVO customTable : templateCustomTableList) {
                    if (customTable.getTableId ().equals ( customTableDB.getTableId () )) {
                        delTemplateTableList.add ( customTableDB );
                        addTemplateTableList.add ( customTable );
                    }

                }
            }
            //删除DB中多余的表和字段
            customTableList.removeAll ( delTemplateTableList );
            if (CollectionUtils.isNotEmpty ( customTableList )) {
                resultCount += templateCustomTableFieldDao.deleteTemplateTable ( templateId, customTableList, operatorId );
                resultCount += templateCustomTableFieldDao.deleteTemplateTableField ( templateId, customTableList, operatorId );
            }
        }

        /**
         * 新增DB中不存在的表和字段
         */
        templateCustomTableList.removeAll ( addTemplateTableList );
        if (CollectionUtils.isNotEmpty ( templateCustomTableList )) {
            resultCount += templateCustomTableFieldDao.addTemplateTable ( templateId, templateCustomTableList, operatorId );
            for (TemplateCustomTableVO customTable : templateCustomTableList) {
                resultCount += templateCustomTableFieldDao.addTemplateTableField ( templateId, customTable.getFieldList (), operatorId );
            }
        }

        /**
         * 更新表单中的自定义表及字段信息至DB
         */
        for (TemplateCustomTableVO templateCustomTableVO : templateCustomTableList) {
            //修改自定义表
            resultCount += templateCustomTableFieldDao.updateTemplateTable ( templateId, templateCustomTableVO, operatorId );

            //删除DB中原有模板表的所有字段(单表全量字段操作)
            resultCount += templateCustomTableFieldDao.deleteTemplateTableFieldByTemplateIdAndTableId ( templateId, templateCustomTableVO.getTableId (), operatorId );

            //重新全量添加表单中的模板表字段信息(单表全量字段操作)
            resultCount += templateCustomTableFieldDao.addTemplateTableField ( templateId, templateCustomTableVO.getFieldList (), operatorId );
        }
        return resultCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List < EntryRegistrationTableVO > searchCustomTableListByTemplateId(Integer templateId) {
        List < EntryRegistrationTableVO > entryRegistrationTableList = templateCustomTableFieldDao.searchEntryRegistrationTableListByTemplateId ( templateId );
        for (EntryRegistrationTableVO tableVO : entryRegistrationTableList) {
            List < CustomFieldVO > customFieldList = templateCustomTableFieldDao.searchCustomFieldListByTemplateIdAndTableId ( tableVO.getTableId (),templateId );
            for (CustomFieldVO customFieldVO : customFieldList) {
                if("code".equals(customFieldVO.getTextType())){
                    List<SysDict> dictList = sysDictService.searchSysDictListByDictType(customFieldVO.getCode());
                    //设置字典类
                    customFieldVO.setDictList(dictList);
                }
            }
            tableVO.setCustomFieldVOList ( customFieldList );
        }
        return entryRegistrationTableList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List < EntryRegistrationTableVO > handlerCustomTableGroupFieldList(List < EntryRegistrationTableVO > entryRegistrationTableList, Map < Integer, String > mapValue) {
        if (CollectionUtils.isNotEmpty ( entryRegistrationTableList )) {
            for (EntryRegistrationTableVO entryRegistrationTableVO : entryRegistrationTableList) {
                List < CustomFieldVO > customFieldVOS = customTableFieldService.selectFieldListByTableId ( entryRegistrationTableVO.getTableId () );
                //处理自定义表字段数据回填
                customTableFieldService.handlerCustomTableGroupFieldList ( customFieldVOS, mapValue );
                entryRegistrationTableVO.setCustomFieldVOList ( customFieldVOS );
            }
        }
        return entryRegistrationTableList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setCustomTableForTemplate(List < TemplateCustomTableVO > list, UserSession userSession) {
        //先将模板下的表全部清除
        Integer templateId = 0;
        for (TemplateCustomTableVO templateCustomTableVO : list) {
            templateId = templateCustomTableVO.getTemplateId ();
        }
        List < TemplateCustomTableVO > voList = searchTableFieldListByTemplateId ( templateId );
        //删除DB中原有模板表的所有字段(单表全量字段操作)
        templateCustomTableFieldDao.deleteTemplateTable ( templateId, voList, userSession.getArchiveId () );
        //新增表数据
        templateCustomTableFieldDao.addTemplateTable ( templateId, list, userSession.getArchiveId () );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplate(UserSession userSession, SaveTemplateVo saveTemplateVo) {
        Integer archiveId=userSession.getArchiveId ();
        TemplateEntryRegistration templateEntryRegistration = saveTemplateVo.getTemplateEntryRegistration ();
        //更新外层
        templateEntryRegistration.setOperatorId (archiveId);
        templateEntryRegistration.setIsEnable ( 1 );
        templateEntryRegistration.setCompanyId ( userSession.getCompanyId () );
        entryRegistrationService.modifyTemplateEntryRegistration ( templateEntryRegistration );
        if(CollectionUtils.isNotEmpty ( saveTemplateVo.getTemplateCustomTableList () )) {
            //删除模板中的表
            templateCustomTableFieldDao.deleteTemplateTableByKt ( saveTemplateVo.getTemplateId (), archiveId );
            //删除模板中的字段
            templateCustomTableFieldDao.deleteTemplateTableFieldByKt ( saveTemplateVo.getTemplateId (), archiveId );
            //新增表
            templateCustomTableFieldDao.addTemplateTable ( saveTemplateVo.getTemplateId (), saveTemplateVo.getTemplateCustomTableList (), archiveId );
            //新增表字段
            for (TemplateCustomTableVO templateCustomTableVO : saveTemplateVo.getTemplateCustomTableList ()) {
                if(CollectionUtils.isNotEmpty ( templateCustomTableVO.getFieldList () )) {
                    templateCustomTableFieldDao.addTemplateTableField ( saveTemplateVo.getTemplateId (),
                            templateCustomTableVO.getFieldList (), archiveId );
                }
            }
        }
    }

}
