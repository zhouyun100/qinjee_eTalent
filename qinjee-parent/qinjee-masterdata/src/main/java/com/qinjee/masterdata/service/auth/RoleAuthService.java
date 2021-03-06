/**
 * 文件名：RoleAuthService
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/23
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.auth;


import com.qinjee.masterdata.model.entity.Role;
import com.qinjee.masterdata.model.vo.auth.*;

import java.util.List;

/**
 * 角色授权
 * @author 周赟
 * @date 2019/9/23
 */
public interface RoleAuthService {

    /**
     * 根据企业ID和档案ID查询角色树
     * @param companyId
     * @param archiveId
     * @return
     */
    List<RoleGroupVO> searchRoleTree(Integer companyId,Integer archiveId);

    /**
     * 根据企业ID、档案ID、角色ID查询角色树
     * @param companyId
     * @param archiveId
     * @param roleId
     * @return
     */
    List<RoleGroupVO> searchRoleTreeByRoleId(Integer companyId,Integer archiveId,Integer roleId);

    /**
     * 根据角色ID查询角色功能权限树
     * @param operatorId
     * @param roleId
     * @param companyId
     * @return
     */
    List<MenuVO> searchRoleAuthTree(Integer operatorId, Integer roleId, Integer companyId);



    /**
     * 查询角色机构权限树
     * @param operatorId
     * @param roleId
     * @param archiveId
     * @return
     */
    List<OrganizationVO> searchOrgAuthTree(Integer operatorId, Integer roleId, Integer archiveId);

    /**
     * 新增角色组
     * @param roleGroupName
     * @param companyId
     * @param operatorId
     * @return
     */
    int addRoleGroup(String roleGroupName, Integer companyId, Integer operatorId);

    /**
     * 新增角色
     * @param roleGroupId
     * @param roleName
     * @param companyId
     * @param operatorId
     * @return
     */
    int addRole(Integer roleGroupId, String roleName, Integer companyId, Integer operatorId);

    /**
     * 修改角色
     * @param roleId
     * @param roleGroupId
     * @param roleName
     * @param operatorId
     * @return
     */
    int updateRole(Integer roleId, Integer roleGroupId, String roleName, Integer operatorId);

    /**
     * 根据角色ID查询角色详情
     * @param roleId
     * @return
     */
    Role searchRoleDetailByRoleId(Integer roleId);

    /**
     * 根据角色ID修改角色是否自动授权新增子集机构权限
     * @param roleId
     * @param isAutoAuthChildOrg
     * @param operatorId
     * @return
     */
    int updateRoleAutoAuthChildOrgByRoleId(Integer roleId, Integer isAutoAuthChildOrg, Integer operatorId);

    /**
     * 修改角色组
     * @param roleGroupId
     * @param roleGroupName
     * @param operatorId
     * @return
     */
    int updateRoleGroup(Integer roleGroupId, String roleGroupName, Integer operatorId);

    /**
     * 根据角色ID删除角色
     * @param roleId
     * @param operatorId
     * @return
     */
    int delRole(Integer roleId, Integer operatorId);

    /**
     * 根据角色ID删除角色组
     * @param roleGroupId
     * @param operatorId
     * @return
     */
    int delRoleGroup(Integer roleGroupId, Integer operatorId);

    /**
     * 修改角色功能权限
     * @param roleId
     * @param menuIdList
     * @param operatorId
     * @return
     */
    int updateRoleMenuAuth(Integer roleId, List<Integer> menuIdList, Integer operatorId);

    /**
     * 修改角色机构权限
     * @param roleId
     * @param orgIdList
     * @param operatorId
     * @return
     */
    int updateRoleOrgAuth(Integer roleId, List<Integer> orgIdList, Integer operatorId);

    /**
     * 角色授权角色
     * @param roleId
     * @param roleIdList
     * @param operatorId
     * @return
     */
    int roleAuthByRoleId(Integer roleId, List<Integer> roleIdList, Integer operatorId);

    /**
     * 根据角色ID查询角色自定义表列表
     * @param companyId
     * @return
     */
    List<CustomArchiveTableFieldVO> searchCustomArchiveTableList(Integer companyId);

    /**
     * 根据角色ID和自定义表ID查询自定义字段列表
     * @param roleId
     * @param tableId
     * @return
     */
    List<CustomArchiveTableFieldVO> searchCustomArchiveTableFieldListByTableId(Integer roleId, Integer tableId);

    /**
     * 根据角色ID查询角色自定义表字段列表
     * @param roleId
     * @return
     */
    List<CustomArchiveTableFieldVO> searchCustomArchiveTableFieldListByRoleId(Integer roleId);

    /**
     * 修改角色自定义人员表字段权限
     * @param requestCustomTableFieldList
     * @param operatorId
     * @return
     */
    int updateRoleCustomArchiveTableFieldAuth(List<CustomArchiveTableFieldVO> requestCustomTableFieldList, Integer operatorId);

    /**
     * 处理所有角色列表以树形结构展示
     * @param allRoleGroupList
     * @param firstLevelRoleList
     */
    void handlerRoleToTree(List<RoleGroupVO> allRoleGroupList, List<RoleGroupVO> firstLevelRoleList);

    /**
     * 处理所有菜单列表以树形结构展示
     * @param allMenuList
     * @param firstLevelMenuList
     */
    void handlerMenuToTree(List<MenuVO> allMenuList, List<MenuVO> firstLevelMenuList);

    /**
     * 处理所有机构列表以树形结构展示
     * @param allOrgList
     * @param firstLevelOrgList
     */
    void handlerOrgToTree(List<OrganizationVO> allOrgList, List<OrganizationVO> firstLevelOrgList);

    /**
     * 保存数据级权限定义
     * @param roleDataLevelAuthVO
     * @return
     */
    int saveRoleDataLevelAuth(RoleDataLevelAuthVO roleDataLevelAuthVO);

}
