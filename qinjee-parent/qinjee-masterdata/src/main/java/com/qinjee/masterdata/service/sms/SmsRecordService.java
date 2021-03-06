/**
 * 文件名：SmsRecordService
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/16
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.sms;

import com.qinjee.model.request.UserSession;

import java.util.List;

/**
 * @author 周赟
 * @date 2019/9/18
 */
public interface SmsRecordService {

    /**
     * 发送预入职登记短信
     */
    void sendMessageSms(List <Integer> list, Integer templateId, UserSession userSession) throws Exception;

    /**
     * 发送短信登录验证码
     * @param phone
     */
    void sendSmsLoginCode(String phone);

    /**
     * 发送企业注册手机号验证码
     * @param phone
     */
    void sendSmsRegistCode(String phone);

    /**
     * 发送微信绑定手机号验证码
     * @param phone
     */
    void sendSmsWechatBindCode(String phone);

    /**
     * 发送忘记密码手机号验证码
     * @param phone
     */
    void sendSmsForgetPasswordCode(String phone);

    /**
     * 发送预入职登录短信验证码
     * @param phone
     */
    void sendSmsPreLoginCode(String phone);

    /**
     * 验证预入职登录短信验证码
     * @param phone
     * @param code
     * @return
     */
    boolean checkPreLoginCodeByPhoneAndCode(String phone, String code);

}
