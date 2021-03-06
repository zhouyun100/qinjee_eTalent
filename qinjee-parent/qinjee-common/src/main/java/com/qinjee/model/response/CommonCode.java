package com.qinjee.model.response;

import lombok.ToString;

/**
 * 公共的响应代码枚举类
 * @date 2019/10-16
 */
@ToString
public enum CommonCode implements ResultCode{
    INVALID_PARAM(false,10003,"非法参数或者长时间未登录请重新登陆！"),
    SUCCESS(true,10000,"操作成功！"),
    FAIL(false,10002,"操作失败！"),
    CODE_GENER_FAIL(false,10007,"机构/岗位编码生成失败（可能由于存在字符）！"),
    UNAUTHENTICATED(false,10001,"此操作需要登陆系统！"),
    UNAUTHORISE(false,10004,"权限不足，无权操作！"),
    SERVER_ERROR(false,99999,"抱歉，系统繁忙，请稍后重试！"),
    INVALID_SESSION(false,10101,"SESSION失效！"),
    BUSINESS_EXCEPTION(false,10100,"业务异常！"),
    REQUEST_EXCEPTION(false,10200,"请求异常，请联系开发人员！"),
    NET_EXCEPTION(false,10201,"网络异常，服务不可用，请稍后刷新重试！"),
    LOGIN_MULTIPLE_COMPANY(false, 10301, "多租户验证，请选择需要登录的租户平台！"),
    ORGANIZATION_OUT_OF_RANGE(false,10302,"本级机构长度超出范围,添加失败！"),
    NAME_ALREADY_USED(false,10303,"名称已存在,操作失败！"),
    EXIST_USER(false,10305, "该机构/岗位下存在人员信息，不允许删除!"),
    FILE_PARSING_EXCEPTION(false,10306, "导入文件解析异常,导入失败!"),
    FILE_FORMAT_ERROR(false,10307, "导入文件格式错误,导入失败!"),
    POST_DOES_NOT_EXIST(false,10308, "此岗位不存在,导入失败!"),
    TIME_FORMAT_ERROR(false,10310, "时间格式错误!"),
    FILE_EXPORT_FAILED(false,10311, "文件导出失败!"),
    FAIL_VALUE_NULL(false,10313, "此员工已经存在!"),
    FILE_EMPTY(false,10314, "文件为空!"),
    REDIS_KEY_EXCEPTION(false,10315, "key不存在或者以过期!"),
    TARGET_NOT_EXIST(false,10316, "目标对象不存在!"),
    ORIGIN_NOT_EXIST(false,10317, "源对象不存在!"),
    NOT_SAME_LEVEL_EXCEPTION(false,10318,"不支持非同级下操作！"),
    CODE_USED(false,10319,"编码已被别处使用！"),
    POST_NOT_EXSIT_EXCEPTION(false,10320,"不存在相关岗位！"),
    TRANSFER_REPET_OPERATION(false,10321,"已在目标机构中存在，请勿重复划转！"),
    SET_DEADLINE_EXCEPTION(false,10316, "一种类型只能设置一种临近天数!"),
    SEND_MAIL_FAIL(false,10317, "发送邮件失败!"),
    File_NUMBER_WRONG(false,10318, "文件存储超过数量上限!"),
    CAN_NOT_SEND_PREREGIST(false,10319, "不允许发送入职登记!"),
    DATE_SO_LONG(false,10320, "二维码过期!"),
    POSITION_USED_NY_POST(false,10321, "职位已被岗位引用，不许删除!"),
    WECHAT_NO_BIND(true,10322, "微信未绑定用户账号"),
    WECHAT_ACCESS_TOKEN(false,40029, "invalid code"),
    CHECK_FALSE(false,10323, "验证失败"),
    PHONE_ALREADY_EXIST(false,10324, "手机号已存在"),
    PARAM_IS_NULL(false,10325, "参数表为空不能进行生成工号"),
    STAFF_IS_EXIST(false,10326, "此人员已经在档案信息中存在"),
    STANDINGBOOK_IS_EMPTY(false,10327, "台账无内容"),
    ORG_HAVE_POST(false,10328, "机构下存在岗位，是否继续删除？"),
    EMPLOYEENUMBER_IS_EXIST(false,10328, "工号已被占用"),
    ARCHIVEID_IS_TOOLONG(false,10329, "请增加规则容量"),
    CANNOT_TWO_NULL(false,10330, "证件号与手机号不能同时为空"),
    IS_EXIST_BLACKLIST(false,10331, "该人员已经存在与黑名单中"),
    IDNUMBER_ALREADY_EXIST(false,10332, "证件号已存在"),
    SQL_MAY_MISTAKE(false,10334, "请检查台账括号或者连接符是否有误"),
    PLAN_IS_NULL(false,10335, "查询方案为空"),
    PLAN_IS_MISTAKE(false,10336, "查询方案无字段内容"),
    FILE_IMPORT_FAILED(false,10333, "文件加载失败，请检查内容是否符合指定格式"),
    DO_NOT_DEL_CURRENT_USER(false,10541, "不允许删除当前登录用户"),
    EXCEL_HEAD_WRONG(false,10542, "所传文件信息不符要求"),
    MSG_IS_MISTAKE(false,10543, "信息发送失败"),
    EMAIL_IS_MISTAKE(false,10544, "邮件发送失败"),
    GET_CACHE_MISTAKE(false,10545, "获取缓存失败，请联系厂商"),
    GRADE_USE_IN_LEVEL(false,10546, "职等被职级引用，不允许删除"),
    LEVEL_USE_IN_POST(false,10547, "职级被岗位引用，不允许删除"),
    PARAM_IS_WRONG(false,10548, "参数格式错误"),
    DATE_IS_WRONG(false,10549, "合同时间设置错误"),
    NO_POWER_CONTRACT(false,10549, "不存在有效合同"),
    FAIL_TO_DELETE(false,10550, "删除文件失败"),
    UPDATE_PARENT_POST_ERR(false,10551, "上级岗位不能设置为当前岗位及当前岗位的下级岗位"),
    UNIT_PARENT_NOT_BE_DEPT(false,10552, "单位的上级机构不能是部门"),
    GROUP_PARENT_NOT_BE_UNIT_OR_DEPT(false,10553, "集团的上级机构不能是单位或部门"),
    NAME_ALREADY_USED_AT_SAME_LEVEL(false,10554, "名称在同级下已存在"),
    TOPORG_CAN_NOT_DEL(false,10555, "顶级机构不能删除"),
    CREATER_CAN_NOT_DEL(false,10556, "企业注册人不能删除"),
    ORG_OR_POST_IS_NULL(false,10554, "确认入职时部门岗位不能为空，请先完善信息"),
    ;


    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 操作代码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    CommonCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return this.success;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
