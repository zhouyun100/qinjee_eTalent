/**
 * 文件名：UserLogin
 * 工程名称：eTalent
 * <p>
 * qinjee
 * 创建日期：2019/9/10
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.controller.auth;

import com.alibaba.fastjson.JSON;
import com.qinjee.consts.ResponseConsts;
import com.qinjee.masterdata.controller.BaseController;
import com.qinjee.masterdata.model.entity.SmsConfig;
import com.qinjee.masterdata.model.vo.auth.MenuVO;
import com.qinjee.masterdata.model.vo.auth.UserInfoVO;
import com.qinjee.masterdata.service.auth.UserLoginService;
import com.qinjee.masterdata.service.sms.SmsConfigService;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.ResponseResult;
import com.qinjee.utils.KeyUtils;
import com.qinjee.utils.RegexpUtils;
import com.qinjee.utils.SendMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 周赟
 * @date 2019/9/10
 */
@Api(tags = "【登录注册】用户登录接口")
@RestController
@RequestMapping("/userLogin")
public class UserLoginController extends BaseController{

    private static Logger logger = LogManager.getLogger(UserLoginController.class);

    /**
     * 短信验证码有效分钟数
     */
    private static final int SMS_CODE_VALID_MINUTE = 5;

    private ResponseResult responseResult;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SmsConfigService smsConfigService;

    @ApiOperation(value="手机号验证码登录", notes="根据手机号、验证码来登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/loginByPhoneAndCode",method = RequestMethod.GET)
    public ResponseResult<UserInfoVO> loginByMobileAndCode(HttpServletResponse response, String phone, String code) {

        try {

            if(phone.isEmpty() || code.isEmpty() || !RegexpUtils.checkPhone(phone)){
                responseResult = ResponseResult.FAIL();
                responseResult.setMessage("手机号或验证码错误，请重新输入!");
                return responseResult;
            }

            String redisPhoneLoginCode = redisClusterService.get("LOGIN_" + phone);
            if(redisPhoneLoginCode.isEmpty() || redisPhoneLoginCode.equals(code)){
                responseResult = ResponseResult.FAIL();
                responseResult.setMessage("手机号验证码无效!");
                return responseResult;
            }

            List<UserInfoVO> userInfoList = userLoginService.searchUserInfoByPhone(phone);

            if (userInfoList.isEmpty() || userInfoList.size() < 1) {

                responseResult = ResponseResult.FAIL();
                responseResult.setMessage("手机号用户信息为空!");
                return responseResult;
            }

            if(userInfoList.size() == 1){
                /**
                 * 有且仅有一家有效租户信息
                 */
                UserInfoVO userInfo = userInfoList.get(0);
                String loginKey = ResponseConsts.SESSION_KEY + "_" + userInfo.getUserId();
                /**
                 * 设置redis登录缓存时间，30分钟过期，与前端保持一致
                 */
                redisClusterService.setex(loginKey, ResponseConsts.SESSION_INVALID_SECCOND, JSON.toJSONString(userInfo));
                Cookie cookie = new Cookie(ResponseConsts.SESSION_KEY, loginKey);
                cookie.setMaxAge(ResponseConsts.SESSION_INVALID_SECCOND);
                cookie.setPath("/");
                response.addCookie(cookie);

                logger.info("Login success！phone={};companyId={}", phone, userInfo.getCompanyId());
                responseResult = ResponseResult.SUCCESS();
                responseResult.setResult(userInfo);
            }else if(userInfoList.size() > 1){
                /**
                 * 当前登录用户如果有多家有效租户信息，则提示用户选择所需登录的租户
                 */
                responseResult = ResponseResult.LOGIN_MULTIPLE_COMPANY();
                responseResult.setMessage("请选择需要登录的租户平台");
                responseResult.setResult(userInfoList);

            }

        }catch(Exception e) {
            logger.info("Login exception! phone={};code={};exception={}", phone,code,e.toString());
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("根据手机号、短信验证码登录异常！");
        }
        return responseResult;
    }

    @ApiOperation(value="账号密码登录", notes="根据用户名/手机号/邮箱、密码来登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "用户名/手机号/邮箱", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/loginByAccountAndPassword",method = RequestMethod.GET)
    public ResponseResult<UserSession> loginByAccountAndPassword(HttpServletResponse response, String account, String password) {
        if(account.isEmpty() || password.isEmpty()){
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("账号或密码为空，请重新输入!");
            return responseResult;
        }

        try {
            List<UserInfoVO> userInfoList = userLoginService.searchUserInfoByAccountAndPassword(account,password);
            if (userInfoList.isEmpty() || userInfoList.size() < 1) {
                responseResult = ResponseResult.FAIL();
                responseResult.setMessage("账号用户信息为空!");
                return responseResult;
            }
            if(userInfoList.size() == 1){
                /**
                 * 有且仅有一家有效租户信息
                 */
                UserInfoVO userInfo = userInfoList.get(0);
                String loginKey = ResponseConsts.SESSION_KEY + "_" + userInfo.getUserId();
                /**
                 * 设置redis登录缓存时间，30分钟过期，与前端保持一致
                 */
                redisClusterService.setex(loginKey, ResponseConsts.SESSION_INVALID_SECCOND, JSON.toJSONString(userInfo));
                Cookie cookie = new Cookie(ResponseConsts.SESSION_KEY, loginKey);
                cookie.setMaxAge(ResponseConsts.SESSION_INVALID_SECCOND);
                cookie.setPath("/");
                response.addCookie(cookie);

                logger.info("Login success！phone={};companyId={}", account, userInfo.getCompanyId());
                responseResult = ResponseResult.SUCCESS();
                responseResult.setResult(userInfo);
            }else if(userInfoList.size() > 1){
                /**
                 * 当前登录用户如果有多家有效租户信息，则提示用户选择所需登录的租户
                 */
                responseResult = ResponseResult.LOGIN_MULTIPLE_COMPANY();
                responseResult.setMessage("请选择需要登录的租户平台");
                responseResult.setResult(userInfoList);

            }
        }catch(Exception e) {
            logger.info("Login exception! account={};password={};exception={}", account, password, e.toString());
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("根据账号、密码登录异常！");
        }
        return responseResult;
    }

    @ApiOperation(value="用户ID和企业ID登录", notes="用户ID和企业ID获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "int"),
            @ApiImplicitParam(name = "companyId", value = "密码", required = true, dataType = "int")
    })
    @RequestMapping(value = "/searchUserInfoByUserIdAndCompanyId",method = RequestMethod.GET)
    public ResponseResult<UserSession> searchUserInfoByUserIdAndCompanyId(HttpServletResponse response, Integer userId,Integer companyId) {

        if(null == userId || null == companyId){
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("用户信息或企业信息为空!");
            return responseResult;
        }

        try {
            UserInfoVO userInfo = userLoginService.searchUserInfoByUserIdAndCompanyId(userId,companyId);
            if (null == userInfo || 0 == userInfo.getUserId()) {
                responseResult = ResponseResult.FAIL();
                responseResult.setMessage("企业用户信息为空!");
                return responseResult;
            }
            String loginKey = ResponseConsts.SESSION_KEY + "_" + userInfo.getUserId();
            /**
             * 设置redis登录缓存时间，30分钟过期，与前端保持一致
             */
            redisClusterService.setex(loginKey, ResponseConsts.SESSION_INVALID_SECCOND, JSON.toJSONString(userInfo));
            Cookie cookie = new Cookie(ResponseConsts.SESSION_KEY, loginKey);
            cookie.setMaxAge(ResponseConsts.SESSION_INVALID_SECCOND);
            cookie.setPath("/");
            response.addCookie(cookie);

            logger.info("Login success！userId={};companyId={}", userId, companyId);
            responseResult = ResponseResult.SUCCESS();
            responseResult.setResult(userInfo);

        }catch(Exception e) {
            logger.info("Login exception! userId={};companyId={};exception={}", userId, companyId, e.toString());
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("根据账号、密码登录异常！");
        }
        return responseResult;

    }


    @ApiOperation(value="发送短信验证码", notes="根据手机号发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/sendCodeByPhone",method = RequestMethod.GET)
    public ResponseResult sendCodeByMobile(String phone) {

        if(phone.isEmpty() || !RegexpUtils.checkPhone(phone)){
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("手机号错误，请重新输入!");
            return responseResult;
        }

        try{
            /**
             * 生成6位随机数字验证码
             */
            String smsCode = KeyUtils.getNonceCodeNumber(6);
            String[] phoneNumbers = {phone};
            String[] param = {smsCode,String.valueOf(SMS_CODE_VALID_MINUTE)};
            redisClusterService.setex("LOGIN_" + phone,SMS_CODE_VALID_MINUTE * 60, smsCode);

            SmsConfig smsConfig = smsConfigService.selectLoginCodeSmsConfig();
            SendMessage.sendMessageMany(smsConfig.getAppId(),smsConfig.getAppKey(),smsConfig.getTemplateId(),smsConfig.getSmsSign(),phoneNumbers,param);

            responseResult = ResponseResult.SUCCESS();
            responseResult.setMessage("手机号短信验证码发送完毕，有效期5分钟!");
        }catch (Exception e){
            logger.info("sendCodeByMobile exception! phone={},exception={}", phone, e.toString());
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("手机号短信验证码发送异常！");
        }
        return responseResult;
    }

    @ApiOperation(value="修改密码", notes="修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/updatePasswordByCurrentAccount",method = RequestMethod.GET)
    public ResponseResult<UserSession> updatePasswordByCurrentAccount(String oldPassword, String newPassword) {
        try{
            userSession = getUserSession();
            userLoginService.updateUserPasswordByUserIdAndPassword(userSession.getUserId(), oldPassword, newPassword);
            responseResult = ResponseResult.SUCCESS();
        }catch (Exception e){
            responseResult = ResponseResult.FAIL();
            responseResult.setMessage("修改密码操作异常！");
        }
        return responseResult;
    }


    @ApiOperation(value="加载当前登录用户菜单树", notes="加载当前登录用户菜单树")
    @RequestMapping(value = "/loadMenuTreeByCurrentLoginUser",method = RequestMethod.GET)
    public ResponseResult<MenuVO> loadMenuTreeByCurrentLoginUser() {
        userSession = getUserSession();
        logger.info("loadMenuTreeByCurrentLoginUser！archiveId={} ", userSession.getArchiveId());

        responseResult = ResponseResult.SUCCESS();
        return responseResult;
    }


    @ApiOperation(value="退出", notes="退出")
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public ResponseResult logout(HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (ResponseConsts.SESSION_KEY.equals(cookies[i].getName())) {
                    String loginKey = cookies[i].getValue();
                    if (loginKey != null) {
                        if(redisClusterService.exists(loginKey)) {
                            redisClusterService.del(loginKey);
                        }
                        cookies[i].setValue(null);
                        // 立即销毁cookie
                        cookies[i].setMaxAge(0);
                        cookies[i].setPath("/");
                        response.addCookie(cookies[i]);
                    }
                }
            }
        }
        responseResult = ResponseResult.SUCCESS();
        return responseResult;
    }

}
