package com.qinjee.masterdata.dao.staffdao.userarchivedao;

import com.qinjee.masterdata.model.entity.UserArchive;
import com.qinjee.masterdata.model.vo.organization.query.PageQuery;
import com.qinjee.masterdata.model.vo.staff.UserArchiveVo;
import com.qinjee.masterdata.model.vo.staff.export.ExportArcVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Repository
public interface UserArchiveDao {

    int deleteByPrimaryKey(Integer archiveId);

    int insert(UserArchive record);

    int insertSelective(UserArchive record);

    UserArchive selectByPrimaryKey(Integer archiveId);

    int updateByPrimaryKeySelective(UserArchive record);

    int updateByPrimaryKey(UserArchive record);

    Integer selectMaxId();

    List<String> selectMail(List<Integer> list);

    List<UserArchive> selectNotInList(@Param("readyIdList") List<Integer> readyIdList);

   Integer deleteArchiveByIdList(@Param("archiveid") List<Integer> archiveid);



    Integer resumeDeleteArchiveById(@Param("archiveid") Integer archiveid);

    /**
     * 根据机构id查询人员档案
     *
     * @param orgId
     * @return
     */
    List<UserArchive> getUserArchiveListByOrgId(Integer orgId);

    /**
     * 查询机构id下员工信息列表
     *
     * @param pageQueryVo
     * @param sortFieldStr
     * @return
     */
    List<UserArchive> getUserArchiveList(@Param("pageQueryVo") PageQuery pageQueryVo, @Param("sortFieldStr") String sortFieldStr);

    Map<String,String> selectNameAndNumber(@Param("id") Integer id);


    Integer selectArchiveIdByNumber(String employeeNumber);


    List<Integer> selectByOrgId(Integer orgId);

    Integer selectArcNumberIn(Integer id);

    List<UserArchive> selectByPrimaryKeyList(@Param("achiveList") List<Integer> achiveList);

    List<Integer> selectStaffNoStandingBook(@Param("archiveType") String archiveType, @Param("id") Integer id);

    Integer selectArchiveIdByTel(@Param("phoneNumber") String phoneNumber);

    Integer selectId(@Param("idType") String idType, @Param("idNumber") String idNumber);

    List<Integer> selectIdByComId(@Param("companyId") Integer companyId);

    List<Integer> selectStaff(@Param("sql") String sql);

     @MapKey("archive_id")
     Map<Integer,Map<String,Object>> getUserArchiveListCustom(@Param("baseSql") String baseSql, @Param("order") String order);


    List<Integer> selectArchiveIdByOrgId(@Param("companyId") Integer companyId);

    List<ExportArcVo> selectDownLoadVoList(@Param("archiveIdList") List<Integer> archiveIdList);

    String selectEmployNumber(@Param("businessId") Integer businessId);

    Date selectDateByStatus(@Param("string") String string);

    List< UserArchiveVo > selectByOrgAndAuth(@Param("orgId") Integer orgId, @Param("archiveId") Integer archiveId, @Param("companyId") Integer companyId);

    Integer countUserArchiveByOrgId(@Param("orgId") Integer orgId);

    Integer selectIdByNumberAndEmploy(@Param("number") String number, @Param("employ") String employ);

    void insertBatch(@Param("userArchives") List< UserArchive> userArchives);


    Integer countUserArchiveByPostId(Integer postId);

    void deleteArchiveById(Integer businessId);

    List< UserArchive> selectBeforeFilter(@Param("archiveType") String archiveType, @Param("orgId") Integer orgId, @Param("type") String type);

    List< UserArchive> selectUserArchiveByName(@Param("name") String name);
}
