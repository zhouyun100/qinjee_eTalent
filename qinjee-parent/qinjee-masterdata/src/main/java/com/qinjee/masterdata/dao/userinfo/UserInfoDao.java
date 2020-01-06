package com.qinjee.masterdata.dao.userinfo;

import com.qinjee.masterdata.model.vo.auth.UserInfoVO;
import org.springframework.stereotype.Repository;

/**
 * @program: eTalent
 * @description:
 * @author: penghs
 * @create: 2020-01-06 09:58
 **/
@Repository
public interface UserInfoDao {
    int editUserInfo(UserInfoVO userInfo);
}
