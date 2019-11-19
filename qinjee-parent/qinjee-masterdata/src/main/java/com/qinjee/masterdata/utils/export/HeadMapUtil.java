package com.qinjee.masterdata.utils.export;


import java.util.*;

public class HeadMapUtil {
    /**
     * 基本档案
     * @param keyList
     * @return
     */
    public static List<String> getHeadForArc(List<String> keyList) {
        String[] strings={"姓名","单位","部门","联系电话","任职类型","工号","档案id","直接上级","岗位","试用期到期时间","入职日期"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 预入职
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByPre(List<String> keyList) {
        String[] strings = {"姓名", "性别", "手机号", "个人邮箱", "证件类型", "证件号码", "年龄","参加工作时间","最高学历","毕业院校",
                            "毕业专业","最近工作单位","计划入职日期","试用期","应聘岗位","入职部门编码","入职部门名称",
                            "入职岗位编码","入职岗位名称","备注"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 工作经历
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByWorkLine(List<String> keyList) {
        String[] strings = {"姓名", "证件号码", "工号", "起始时间", "终止日期", "所在单位", "所在岗位","证明人姓名","变动原因"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 黑名单
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByBlackList(List<String> keyList) {
        String[] strings = {"姓名", "性别", "证件号码", "手机号", "所属单位", "部门", "岗位","拉黑原因"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 家庭成员
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByFamily(List<String> keyList) {
        String[] strings = {"姓名", "证件号码", "工号", "成员姓名", "与本人关系", "成员出生日期", "成员电话","成员工作单位","成员职位"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 教育经历子集
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByEduLine(List<String> keyList) {
        String[] strings = {"姓名", "证件号码", "工号", "入学时间", "毕业时间", "毕业院校", "学历","专业","学习形式","学位","是否最高学历"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 人事异动子集
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByStaffLine(List<String> keyList) {
        String[] strings = {"姓名", "工号", "变动前人员分类", "变动前部门编码", "变动前部门", "变动后部门编码", "变动后部门","变动前岗位编码",
                "变动前岗位","变动后岗位编码", "变动后岗位","变动前职位","变动后职位","变动类型","变动类型","变动原因","变动时间"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 合同
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByCon(List<String> keyList){
        String[] strings={"姓名","证件号码","工号","合同类别","合同编号","合同期限类型","合同签订日期","合同起始日期",
                "合同终止日期","合同期限(月)","合同主体","签订次数","备注"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 职称子集
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByStaffSymbol(List<String> keyList){
        String[] strings={"姓名","证件号码","工号","职称资格","职称资格等级","职称证编号","资格批准单位","获得资格途径"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

    /**
     * 职业资格子集
     * @param keyList
     * @return
     */
    public static List<String> getHeadsByStaffAuth(List<String> keyList){
        String[] strings={"姓名","证件号码","工号","资格名称","证书号","职业工种","发证单位","发证日期"};
        keyList.addAll(Arrays.asList(strings));
        return keyList;
    }

}
