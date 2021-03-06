package com.qinjee.masterdata.service.staff;

import com.qinjee.masterdata.model.entity.*;
import com.qinjee.masterdata.model.vo.organization.OrganizationVO;
import com.qinjee.masterdata.model.vo.staff.*;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.PageResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface IStaffCommonService {

    /**
     * <String,String>  key是fieldCode拼接textType，value是值
     * @param id 业务id
     * @param tableId
     * @return
     */
     List<Map<String, String>> getCustomDataByTableIdAndEmploymentId(Integer id, Integer tableId) throws IllegalAccessException;
    /**
     * 新增自定义表
     * @param customArchiveTable
     * @return
     */
   void  insertCustomArichiveTable(CustomArchiveTable customArchiveTable,UserSession userSession);

    /**
     * 逻辑删除自定义表
     * @param list
     * @return
     */
     void deleteCustomArchiveTable(List<Integer> list) throws Exception;

    /**
     * 自定义表修改
     * @param customArchiveTable
     * @return
     */
   void updateCustomArchiveTable(CustomArchiveTable customArchiveTable);


    /**
     * 分页展示自定义表
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageResult<CustomArchiveTable> selectCustomArchiveTable(Integer currentPage, Integer pageSize,UserSession userSession);

    /**
     * 新增自定义组
     * @param customArchiveGroup
     * @return
     */

    void insertCustomArchiveGroup(CustomArchiveGroup customArchiveGroup,UserSession userSession);

    /**
     * 删除自定义组
     * @param list
     * @return
     */
    void deleteCustomArchiveGroup(List<Integer> list) throws Exception;

    /**
     * 自定义组修改
     * @param customArchiveGroup
     * @return
     */
    void updateCustomArchiveGroup(CustomArchiveGroup customArchiveGroup);

    /**
     *分页展示自定义组中的表
     * @param currentPage
     * @param pageSize
     * @param customArchiveGroupId
     * @return
     */
  PageResult<CustomArchiveField>selectArchiveFieldFromGroup(Integer currentPage, Integer pageSize, Integer customArchiveGroupId);

    /**
     * 新增自定义字段
     * @param customArchiveField
     * @return
     */
  void insertCustomArchiveField(CustomArchiveField customArchiveField,UserSession userSession);

    /**
     * 逻辑删除自定义字段
     * @param list
     * @return
     */
     void deleteCustomArchiveField(List<Integer> list) throws Exception;

    /**
     * 修改自定义字段类型
     * @param customArchiveField
     * @returnvoid
     */
    void updateCustomArchiveField(CustomArchiveField customArchiveField);

    /**
     *分页展示指定自定义表下的自定义字段
     * @param currentPage
     * @param pageSize
     * @param customArchiveTableId
     * @return
     */
    PageResult<CustomArchiveField> selectCustomArchiveField(Integer currentPage, Integer pageSize, Integer customArchiveTableId);
    /**
     * 通过字段id找到自定义字段信息
     * @param customArchiveFieldId
     * @return
     */
    CustomArchiveField selectCustomArchiveFieldById(Integer customArchiveFieldId);
    /**
     * 将数据插入自定义数据表
     * @param bigDataVo
     * @return
     */
    void insertCustomArchiveTableData(BigDataVo bigDataVo, UserSession userSession);

    /**
     * 修改自定义字段表中的数据
     * @param  customArchiveTableDataVo
     * @return
     */
    void updateCustomArchiveTableData(CustomArchiveTableDataVo customArchiveTableDataVo);

    /**
     * 展示自定义表数据内容,返回自定义表数据
     * @param currentPage
     * @param pageSize
     * @param customArchiveTableId
     * @return
     */
   PageResult<CustomArchiveTableData> selectCustomArchiveTableData(Integer currentPage, Integer pageSize, Integer customArchiveTableId);
    /**
     * 据档案显示对应权限下的单位
     * @param userSession
     * @return
     */
    Integer getCompanyId(UserSession userSession);
    /**
     * 根据档案id显示对应权限下的子集部门
     * @return
     */
    List<OrganzitionVo> getOrgIdByCompanyId( UserSession userSession);

    /**
     * 显示部门下的岗位
     * @param orgId
     * @return
     */
    String getPostByOrgId (Integer orgId,UserSession userSession);

    /**
     * 展示自定义字段的值
     * @param customArchiveFieldId
     * @return
     */
    List<String> selectFieldValueById(Integer customArchiveFieldId);

    List<Integer> saveFieldAndValue (UserSession userSession, InsertDataVo insertDataVo) throws Exception;

    /**
     *
     * @param tableId
     * @param businessId
     * @return
     * @throws IllegalAccessException
     */
    List<Map< Integer, String>> selectValue(Integer tableId, Integer businessId) throws IllegalAccessException;

    void deletePreValue(Integer id,UserSession userSession);

    void deleteArcValue(Integer businessId,UserSession userSession);

    /**
     *
     * @param orgId
     * @param userSession
     * @return
     */
    Map< String, String> getNameForOrganzition(Integer orgId, UserSession userSession,Integer postId);

    void deleteCustomArchiveTableData(List< Integer> list);

    OrganizationVO getOrgById(Integer orgId, UserSession userSession);

    Post getPostById(Integer postId,UserSession userSession);

    void deleteCustomTableData(Integer id);

    List< Post> getPostListByOrgId(Integer orgId,Integer companyId);

    void updateCustomArchiveTableDatas(MoblieCustom moblieCustom);
}
