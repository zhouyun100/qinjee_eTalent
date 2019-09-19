package com.qinjee.masterdata.service.staff;

import com.qinjee.masterdata.model.entity.PreEmploymentChange;
import com.qinjee.model.response.ResponseResult;

import java.util.List;

/**
 * @author Administrator
 */
public interface IStaffPreEmploymentService {
    /**
     * 发送短信
     * @param list
     * @param templateId
     * @param params
     * @return
     */
    ResponseResult sendMessage(List<Integer> list, Integer templateId, String[] params);

    /**
     * 发送邮件
     * @param prelist
     * @param conList
     * @param content
     * @param subject
     * @param filepath
     * @return
     */
    ResponseResult sendManyMail(List<Integer> prelist, List<Integer> conList, String content,String subject, String[] filepath);

    /**
     * 验证手机号
     * @param phoneNumber
     * @return
     */
    ResponseResult checkPhone(String phoneNumber);

    /**
     * 验证邮箱
     * @param mail
     * @return
     */
    ResponseResult checkMail(String mail);

    /**
     * 更新预入职变更表
     * @param preEmploymentChange
     * @return
     */
    ResponseResult updatePreEmploymentChange(PreEmploymentChange preEmploymentChange);
}
