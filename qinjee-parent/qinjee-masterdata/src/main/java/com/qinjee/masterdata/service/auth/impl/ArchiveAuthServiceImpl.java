/**
 * 文件名：ArchiveAuthServiceImpl
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/18
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.auth.impl;

import com.qinjee.masterdata.dao.auth.ArchiveAuthDao;
import com.qinjee.masterdata.model.entity.UserOrgAuth;
import com.qinjee.masterdata.model.entity.UserRole;
import com.qinjee.masterdata.model.vo.auth.*;
import com.qinjee.masterdata.service.auth.ArchiveAuthService;
import com.qinjee.masterdata.service.auth.RoleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 周赟
 * @date 2019/9/18
 */
@Service
public class ArchiveAuthServiceImpl implements ArchiveAuthService {

    @Autowired
    private ArchiveAuthDao archiveAuthDao;

    @Autowired
    private RoleAuthService roleAuthService;


    @Override
    public List<RoleGroupVO> searchRoleTree(Integer companyId,Integer archiveId) {
        List<RoleGroupVO> roleGroupList = roleAuthService.searchRoleTree(companyId,archiveId);
        return roleGroupList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addArchiveRole(Integer roleId, List<Integer> archiveIdList,Integer operatorId) {

        if(CollectionUtils.isNotEmpty(archiveIdList)){

            UserRole userRole;
            List<ArchiveInfoVO> archiveList = archiveAuthDao.searchArchiveListByRoleId(roleId);

            if(CollectionUtils.isNotEmpty(archiveList)){
                for(ArchiveInfoVO archive : archiveList){
                    archiveIdList = archiveIdList.stream().filter(archiveId -> {
                        if(archiveId.equals(archive.getArchiveId())){
                            return false;
                        }else{
                            return true;
                        }
                    }).collect(Collectors.toList());
                }
            }

            for(Integer archiveId : archiveIdList){
                userRole = new UserRole();
                userRole.setArchiveId(archiveId);
                userRole.setRoleId(roleId);
                userRole.setOperatorId(operatorId);
                archiveAuthDao.addArchiveRole(userRole);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delArchiveRole(Integer roleId, List<Integer> archiveIdList,Integer operatorId) {
        if(CollectionUtils.isNotEmpty(archiveIdList)){
            UserRole userRole;
            UserOrgAuth userOrgAuth;
            for(Integer archiveId : archiveIdList){
                userRole = new UserRole();
                userRole.setArchiveId(archiveId);
                userRole.setRoleId(roleId);
                userRole.setOperatorId(operatorId);
                archiveAuthDao.delArchiveRole(userRole);

                userOrgAuth = new UserOrgAuth();
                userOrgAuth.setArchiveId(archiveId);
                userOrgAuth.setRoleId(roleId);
                archiveAuthDao.delArchiveOrgAuth(userOrgAuth);
            }
        }
    }

    @Override
    public void delUserRoleRelationByArchiveId(Integer operatorId, Integer archiveId) {
        archiveAuthDao.delUserRoleRelationByUserId(operatorId,archiveId);
    }

    @Override
    public List<OrganizationArchiveVO> getOrganizationArchiveTreeByArchiveId(Integer companyId, Integer archiveId) {

        List<OrganizationArchiveVO> organizationList = archiveAuthDao.searchOrganizationListByArchiveId(archiveId,new Date());
        List<ArchiveInfoVO> archiveInfoList = archiveAuthDao.searchArchiveListByCompanyId(companyId);
        //获取第一级机构
        List<OrganizationArchiveVO> organizationArchiveList = organizationList.stream().filter(organization -> {
            if (organization.getOrgParentId() != null && organization.getOrgParentId() == 0) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        //移除一级机构
        organizationList.removeAll(organizationArchiveList);

        //递归处理机构,使其以树形结构展示
        handlerOrganizationArchiveToTree(archiveInfoList,organizationList, organizationArchiveList);

        return organizationArchiveList;
    }

    private void handlerOrganizationArchiveToTree(List<ArchiveInfoVO> archiveInfoList, List<OrganizationArchiveVO> organizationList, List<OrganizationArchiveVO> organizationArchiveList) {
        for (OrganizationArchiveVO orgArchive : organizationArchiveList) {

            //筛选子集机构
            List<OrganizationArchiveVO> childOrgList = organizationList.stream().filter(organization -> {
                if (organization.getOrgParentId().equals(orgArchive.getOrgId())) {
                    return true;
                }else{
                    return false;
                }
            }).collect(Collectors.toList());

            //筛选子集人员
            List<ArchiveInfoVO> childArchiveList = archiveInfoList.stream().filter(archiveInfoVO -> {
                if(archiveInfoVO.getOrgId() != null && archiveInfoVO.getOrgId().equals(orgArchive.getOrgId())){
                    return true;
                }else{
                    return false;
                }
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(childArchiveList)){
                orgArchive.setChildArchiveList(childArchiveList);
                archiveInfoList.removeAll(childArchiveList);
            }

            //判断是否还有子级机构
            if (CollectionUtils.isNotEmpty(childOrgList)) {
                orgArchive.setChildOrgList(childOrgList);
                organizationList.removeAll(childOrgList);
                handlerOrganizationArchiveToTree(archiveInfoList,organizationList, childOrgList);
            }
        }
    }

    @Override
    public List<OrganizationVO> searchOrgAuthTree(Integer operatorId, Integer roleId, Integer archiveId) {
        if(roleId == null || archiveId == null){
            return null;
        }

        List<OrganizationVO> firstLevelMenuList;
        RequestRoleVO requestRole = new RequestRoleVO();
        requestRole.setRoleId(roleId);
        requestRole.setArchiveId(archiveId);
        requestRole.setCurrentDateTime(new Date());
        requestRole.setOperatorId(operatorId);
        List<OrganizationVO> organizationList = archiveAuthDao.searchOrgAuthTree(requestRole);

        /**
         * 如果机构列表为空则直接返回null
         */
        if(CollectionUtils.isNotEmpty(organizationList)){
            /**
             * 提取当前机构树的一级节点
             */
            firstLevelMenuList = organizationList.stream().filter(organization -> {
                if(organization.getOrgParentId() == 0){
                    return true;
                }else{
                    return false;
                }
            }).collect(Collectors.toList());

            /**
             * 处理所有机构列表以树形结构展示
             */
            roleAuthService.handlerOrgToTree(organizationList, firstLevelMenuList);
        }else{
            return null;
        }
        return firstLevelMenuList;
    }

    @Override
    public int updateArchiveOrgAuth(Integer roleId, Integer archiveId, List<Integer> orgIdList, Integer operatorId) {
        int resultNumber = 0;
        List<Integer> tempOrgIdList = new ArrayList<>();
        List<OrganizationVO> tempOrganizationList = new ArrayList<>();

        UserOrgAuth userOrgAuth = new UserOrgAuth();
        userOrgAuth.setOperatorId(operatorId);
        userOrgAuth.setArchiveId(archiveId);

        List<OrganizationVO> organizationList = archiveAuthDao.searchArchiveOrgListByRoleId(roleId, archiveId);

        if(CollectionUtils.isNotEmpty(orgIdList)){
            if(CollectionUtils.isNotEmpty(organizationList)){
                for(Integer orgId : orgIdList){
                    organizationList.stream().filter(organization ->{
                        if(orgId.equals(organization.getOrgId())){
                            tempOrgIdList.add(orgId);
                            tempOrganizationList.add(organization);
                            return true;
                        }else{
                            return false;
                        }
                    }).collect(Collectors.toList());
                }
            }
            orgIdList.removeAll(tempOrgIdList);
            for(Integer orgId : orgIdList){
                userOrgAuth.setOrgId(orgId);
                userOrgAuth.setRoleId(roleId);
                resultNumber += archiveAuthDao.addArchiveOrgAuth(userOrgAuth);
            }
        }

        if(CollectionUtils.isNotEmpty(organizationList)){
            organizationList.removeAll(tempOrganizationList);
            for(OrganizationVO organization : organizationList){
                userOrgAuth.setOrgId(organization.getOrgId());
                userOrgAuth.setRoleId(roleId);
                resultNumber += archiveAuthDao.delArchiveOrgAuth(userOrgAuth);
            }
        }
        return resultNumber;
    }

    @Override
    public List<MenuVO> searchMenuButtonList(Integer archiveId, Integer menuId) {
        List<MenuVO> menuList = archiveAuthDao.searchMenuButtonList(archiveId,menuId,new Date());
        return menuList;
    }
}
