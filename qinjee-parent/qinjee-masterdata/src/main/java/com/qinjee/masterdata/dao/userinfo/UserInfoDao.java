package com.qinjee.masterdata.dao.userinfo;

import com.qinjee.masterdata.model.entity.CompanyInfo;
import com.qinjee.masterdata.model.entity.UserInfo;
import com.qinjee.masterdata.model.vo.auth.UserInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @program: eTalent
 * @description:
 * @author: penghs
 * @create: 2020-01-06 09:58
 **/
@Repository
public interface UserInfoDao {
    int editUserInfo(UserInfoVO userInfo);

    UserInfo getUserByPhoneAndCompanyId(@Param("phone") String phone, @Param("companyId") Integer companyId);
    UserInfo getUserByPhone(String phone);

    int clearUserCompany(@Param("userId") Integer userId, @Param("companyId") Integer companyId, @Param("date") Date date);

    void update(UserInfo userInfoDo);

    /**
     * 根据用户ID查询企业列表
     * @param userId
     * @return
     */
    List<CompanyInfo> selectCompanyListByUserId(Integer userId);

    /**
     * 设置用户默认登录企业
     * @param userId
     * @param companyId
     * @return
     */
    int setUserCompanyDefaultLogin(@Param("userId") Integer userId, @Param("companyId") Integer companyId);

    /**
     * 设置用户该企业外为非默认登录
     * @param userId
     * @param companyId
     * @return
     */
    int setUserCompanyNoneDefaultLogin(@Param("userId") Integer userId, @Param("companyId") Integer companyId);
}
