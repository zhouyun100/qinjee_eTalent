/**
 * 文件名：AuthHandoverService
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/20
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.auth;

import com.qinjee.masterdata.model.vo.auth.MenuVO;
import com.qinjee.masterdata.model.vo.auth.OrganizationVO;
import com.qinjee.masterdata.model.vo.auth.RoleGroupVO;
import com.qinjee.masterdata.model.vo.auth.UserRoleVO;

import java.util.Date;
import java.util.List;

/**
 * 权限移交
 * @author 周赟
 * @date 2019/9/20
 */
public interface AuthHandoverService {

    /**
     * 查询角色功能权限树
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
     * 角色回收
     * @param idList
     * @param operatorId
     * @return
     */
    int roleRecoveryByArchiveId(List<Integer> idList, Integer operatorId);


    /**
     * 角色移交
     * @param acceptArchiveId
     * @param idList
     * @param operatorId
     * @return
     */
    int roleHandoverByArchiveId(Integer acceptArchiveId, List<Integer> idList, Integer operatorId);


    /**
     * 角色托管
     * @param trusteeshipArchiveId
     * @param acceptArchiveId
     * @param trusteeshipBeginTime
     * @param trusteeshipEndTime
     * @param idList
     * @param operatorId
     * @return
     */
    int roleTrusteeshipByArchiveId(Integer trusteeshipArchiveId, Integer acceptArchiveId, Date trusteeshipBeginTime, Date trusteeshipEndTime, List<Integer> idList, Integer operatorId);


    /**
     * 根据档案ID查询角色列表
     * @param archiveId
     * @param companyId
     * @return
     */
    List<UserRoleVO> searchRoleListByArchiveId(Integer archiveId, Integer companyId);
}
