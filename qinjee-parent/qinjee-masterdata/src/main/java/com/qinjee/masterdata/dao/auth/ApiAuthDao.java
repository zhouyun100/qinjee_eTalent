/**
 * 文件名：ApiAuthDao
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/11/25
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.dao.auth;

import com.qinjee.masterdata.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 周赟
 * @date 2019/11/25
 */
@Repository
public interface ApiAuthDao {

    /**
     * 查询父级机构自动授权子集机构的角色列表
     * @param orgId
     * @return
     */
    List<Role> searchParentOrgIdAutoAuthChildOrgRoleList(Integer orgId);

    /**
     * 查询自动授权子集机构角色列表
     * @param orgId
     * @return
     */
    List<Role> searchAutoAuthChildOrgRoleList(Integer orgId);


    /**
     * 根据机构列表查询子集机构
     * @param orgIdList
     * @return
     */
    List<Integer> searchChildOrganizationList(List<Integer> orgIdList);

    /**
     * 机构新增角色
     * @param orgId 机构ID
     * @param roleId 角色ID
     * @param operatorId 操作人档案ID
     * @return
     */
    int insertRoleOrg(Integer orgId,Integer roleId, Integer operatorId);

    /**
     * 新增机构档案角色关系
     * @param orgId 机构ID
     * @param archiveId 档案ID
     * @param roleId 角色ID
     * @param operatorId 操作人
     * @return
     */
    int insertUserOrg(Integer orgId, Integer archiveId, Integer roleId, Integer operatorId);


    /**
     * 删除角色机构
     * @param orgIdList 机构ID列表
     * @param roleId 角色ID
     * @param operatorId 操作人档案ID
     * @return
     */
    int deleteRoleOrgAuth(List<Integer> orgIdList,Integer roleId, Integer operatorId);

    /**
     * 删除不在职员工角色权限(离职、退休)
     * @param archiveId 档案ID
     * @param operatorId 操作人档案ID
     * @return
     */
    int deleteArchiveRoleAuth(Integer archiveId, Integer operatorId);

    /**
     * 删除不在职员工机构权限(离职、退休)
     * @param archiveId 档案ID
     * @param orgId 机构ID
     * @param operatorId 操作人档案ID
     * @return
     */
    int deleteArchiveOrgAuth(Integer archiveId, Integer orgId, Integer operatorId);
}
