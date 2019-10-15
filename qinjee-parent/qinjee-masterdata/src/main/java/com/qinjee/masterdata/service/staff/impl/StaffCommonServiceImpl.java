package com.qinjee.masterdata.service.staff.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.qinjee.masterdata.dao.*;
import com.qinjee.masterdata.dao.organation.OrganizationDao;
import com.qinjee.masterdata.dao.staffdao.commondao.*;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.PreEmploymentDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.QuerySchemeFieldDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.UserArchiveDao;
import com.qinjee.masterdata.model.entity.*;
import com.qinjee.masterdata.model.vo.staff.ForWardPutFile;
import com.qinjee.masterdata.service.staff.IStaffCommonService;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.PageResult;
import com.qinjee.utils.ExcelUtil;
import com.qinjee.utils.UpAndDownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Administrator
 */
@Service
public class StaffCommonServiceImpl implements IStaffCommonService {
    private static final Logger logger = LoggerFactory.getLogger(StaffCommonServiceImpl.class);
    private static final String ARCHIVE = "档案";
    private static final String ARCHIVETABLE = "档案表";
    private static final String PREEMP = "预入职";
    private static final String PREEMPTABLE = "预入职表";
    private static final String IDTYPE = "证件类型";
    private static final String IDNUMBER = "证件号码";
    private static final String USERCATEGORY = "人员分类";
    private static final String PHONE = "手机";

    @Autowired
    private CustomArchiveTableDao customArchiveTableDao;
    @Autowired
    private CustomArchiveGroupDao customArchiveGroupDao;
    @Autowired
    private CustomArchiveFieldDao customArchiveFieldDao;
    @Autowired
    private CustomArchiveTableDataDao customArchiveTableDataDao;
    @Autowired
    private CustomArchiveFieldCheckDao customArchiveFieldCheckDao;
    @Autowired
    private OrganizationDao organizationDao;
    @Autowired
    private CompanyCodeDao companyCodeDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private UserArchiveDao userArchiveDao;
    @Autowired
    private PreEmploymentDao preEmploymentDao;
    @Autowired
    private QuerySchemeFieldDao querySchemeFieldDao;

    @Override
    public void insertCustomArichiveTable(CustomArchiveTable customArchiveTable) {
        customArchiveTableDao.insertSelective(customArchiveTable);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCustomArchiveTable(List<Integer> list) throws Exception {
        Integer max = customArchiveTableDao.selectMaxPrimaryKey();
        for (Integer integer : list) {
            if(max<integer) {
                throw new Exception("id有误");
            }
        }
        customArchiveTableDao.deleteCustomTable(list);
    }

    @Override
    public void updateCustomArchiveTable(CustomArchiveTable customArchiveTable) {

        customArchiveTableDao.updateByPrimaryKey(customArchiveTable);

    }

    @Override
    public PageResult<CustomArchiveTable> selectCustomArchiveTable(Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<CustomArchiveTable> customArchiveTables = customArchiveTableDao.selectByPage();
        return new PageResult<>(customArchiveTables);
    }

    @Override
    public void insertCustomArchiveGroup(CustomArchiveGroup customArchiveGroup) {
        customArchiveGroupDao.insertSelective(customArchiveGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomArchiveGroup(List<Integer> list) throws Exception {
        Integer max = customArchiveGroupDao.selectMaxPrimaryKey();
        for (Integer integer : list) {
            if(max<integer) {
                throw new Exception("id有误");
            }
        }
        customArchiveGroupDao.deleteCustomGroup(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomArchiveGroup(CustomArchiveGroup customArchiveGroup) {
        customArchiveGroupDao.updateByPrimaryKey(customArchiveGroup);
    }

    @Override
    public PageResult<CustomArchiveTable> selectCustomTableFromGroup(Integer currentPage, Integer
            pageSize, Integer customArchiveGroupId) {
        PageHelper.startPage(currentPage, pageSize);
        //获得自定义组中自定义表id的集合
        List<Integer> integerList = customArchiveGroupDao.selectTableId(customArchiveGroupId);

        List<CustomArchiveTable> list = customArchiveTableDao.selectByPrimaryKeyList(integerList);
        return new PageResult<>(list);
    }

    @Override
    public void insertCustomArchiveField(CustomArchiveField customArchiveField) {
        customArchiveFieldDao.insertSelective(customArchiveField);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomArchiveField(List<Integer> list) throws Exception {
        Integer max = customArchiveFieldDao.selectMaxPrimaryKey();
        for (Integer integer : list) {
            if(max<integer) {
                throw new Exception("id有误");
            }
        }
        customArchiveFieldDao.deleteCustomField(list);
    }

    @Override
    public void updateCustomArchiveField(CustomArchiveField customArchiveField) {
        customArchiveFieldDao.updateByPrimaryKey(customArchiveField);
    }

    @Override
    public PageResult<CustomArchiveField> selectCustomArchiveField(Integer currentPage, Integer pageSize,
                                                                   Integer customArchiveTableId) {

        PageHelper.startPage(currentPage, pageSize);
        //根据自定义表找自定义字段id
        List<Integer> integerList = customArchiveFieldDao.selectFieldId(customArchiveTableId);
        List<CustomArchiveField> list = customArchiveFieldDao.selectByPrimaryKeyList(integerList);
        return new PageResult<>(list);
    }

    @Override
    public CustomArchiveField selectCustomArchiveFieldById(Integer customArchiveFieldId) {
        return customArchiveFieldDao.selectByPrimaryKey(customArchiveFieldId);
    }


    @Override
    public List<Integer> getCompanyId(UserSession userSession) {
        Integer archiveId = userSession.getArchiveId();
        return organizationDao.getCompanyIdByArchiveId(archiveId);

    }

    @Override
    public List<Integer> getOrgIdByCompanyId(Integer orgId) {

        return organizationDao.getOrgIdByCompanyId(orgId);
    }

    @Override
    public List<Integer> getPostByOrgId(Integer orgId) {

        return postDao.getPostByOrgId(orgId);
    }

    @Override
    public List<String> selectTableFromOrg(UserSession userSession) {
        //根据部门id筛选自己的表id
        List<Integer> list1 = customArchiveTableDao.selectIdByComId(userSession.getCompanyId());
        return customArchiveTableDao.selectNameById(list1);
    }

    @Override
    public List<String> staffCommonService(Integer customArchiveFieldId) {
        //找到企业代码id
        Integer id = customArchiveFieldDao.selectCodeId(customArchiveFieldId);
        //找到自定义字段的值
        return companyCodeDao.selectValue(id);
    }


    @Override
    public void insertCustomArchiveTableData(CustomArchiveTableData customArchiveTableData, UserSession userSession) {
        Integer archiveId = userSession.getArchiveId();
        //将前端传过来的大字段进行解析
        StringBuffer bigData = null;
        JSONArray json = (JSONArray) JSONArray.toJSON(customArchiveTableData.getBigData());
        JSONObject jsono = JSONObject.parseObject(json.toString());
        List<String> strings = new ArrayList<>(jsono.keySet());
        for (String string : strings) {
            bigData.append("@@").append(string).append("@@:").append(jsono.get(string)).append(";");
        }
        //去除最后一个分号
        int i = bigData.toString().lastIndexOf(",");
        String substring = bigData.toString().substring(0, i);
        customArchiveTableData.setOperatorId(archiveId);
        customArchiveTableData.setBigData(substring);
        customArchiveTableDataDao.insert(customArchiveTableData);
    }

    @Override
    public void updateCustomArchiveTableData(CustomArchiveTableData customArchiveTableData) {
        customArchiveTableDataDao.updateByPrimaryKey(customArchiveTableData);
    }

    @Override
    public PageResult<CustomArchiveTableData> selectCustomArchiveTableData(Integer currentPage, Integer pageSize, Integer customArchiveTableId) {
        PageHelper.startPage(currentPage, pageSize);
        //通过自定义表id找到数据id集合
        List<Integer> integerList = customArchiveTableDataDao.selectCustomArchiveTableId(customArchiveTableId);

        List<CustomArchiveTableData> list = customArchiveTableDataDao.selectByPrimaryKeyList(integerList);

        return new PageResult<>(list);
    }


    @Override
    public List<String> checkField(Integer fieldId) {
        //通过字段名找到验证code
        return customArchiveFieldCheckDao.selectCheckName(fieldId);
    }

    @Override
    public void importFile(String path, UserSession userSession) throws IOException, NoSuchFieldException, IllegalAccessException {
        MultipartFile multipartFile = ExcelUtil.getMultipartFile(new File(path));
        Integer tableId = null;
        //key是字段名称，value是值
        Map<String, List<String>> stringListMap = ExcelUtil.readExcel(multipartFile);
        Map<Integer, List<String>> fieldMap = new HashMap<>();
        //需要进行入库操作
        //根据companyId，功能code为档案和预入职的表
        List<Integer> list = customArchiveTableDao.selectidbycomidandfunccode(userSession.getCompanyId());
        //根据自定义表找到自定义字段名存进map中
        for (Integer integer : list) {
            List<String> list1 = customArchiveFieldDao.selectFieldNameListByTableId(integer);
            fieldMap.put(integer, list1);
        }
        //匹配找到需要设值的表id
        for (Map.Entry<Integer, List<String>> integerListEntry : fieldMap.entrySet()) {
            List<String> strings = new ArrayList<>(stringListMap.keySet());
            //将需要设置进行业务id寻找的字段剔除，保证能找到tableId
            for (String s : strings) {
                if (PHONE.equals(s) || IDNUMBER.equals(s) || IDTYPE.equals(s)) {
                    strings.remove(s);
                }
            }
            if (integerListEntry.getValue().containsAll(strings)) {
                tableId = integerListEntry.getKey();
            } else {
                logger.error("字段有误");
            }
        }
        Integer integer = customArchiveTableDao.selectInside(tableId);
        if (integer > 0) {
            //  若表为内置表
            //根据表id确认是档案表还是预入职表
            String funcCode = customArchiveTableDao.selectFuncCode(tableId);
            if (ARCHIVE.equals(funcCode)) {
                //将值设入属性,判断是新增还是更新
                List<String> list1 = stringListMap.get(IDTYPE);
                List<String> list2 = stringListMap.get(IDNUMBER);
                for (int i = 0; i < list1.size(); i++) {
                    Integer id = userArchiveDao.selectId(list1.get(i), list2.get(i));
                    if ("".equals(id) || null == id) {
                        UserArchive userArchive = new UserArchive();
                        setValue(stringListMap, i, userArchive);
                        userArchiveDao.insertSelective(userArchive);
                    } else {
                        UserArchive userArchive = new UserArchive();
                        userArchive.setArchiveId(id);
                        setValue(stringListMap, i, userArchive);
                        userArchiveDao.updateByPrimaryKeySelective(userArchive);
                    }
                }
            }
            if (PREEMP.equals(funcCode)) {
                List<String> list1 = stringListMap.get(PHONE);
                for (int i = 0; i < list1.size(); i++) {
                    Integer id = preEmploymentDao.selectIdByNumber(list1.get(i));
                    if ( null == id) {
                        PreEmployment preEmployment = new PreEmployment();
                        setValue(stringListMap, i, preEmployment);
                        preEmploymentDao.insertSelective(preEmployment);
                    } else {
                        //将值设入属性
                        PreEmployment preEmployment = new PreEmployment();
                        preEmployment.setEmploymentId(id);
                        setValue(stringListMap, i, preEmployment);
                        preEmploymentDao.updateByPrimaryKey(preEmployment);
                    }
                }
            }
            logger.error("未找到对应的物理表,导入失败");
           /*
            //将值设入属性
            Set<Map.Entry<String, List<String>>> entries = stringListMap.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                Field declaredField = object.getClass().getDeclaredField(entry.getKey());
                declaredField.setAccessible(true);
                declaredField.set(object,entry.getValue());
            }
            //将值设置给物理表
            //物理属性名集合
            List<String> strings = customArchiveFieldDao.selectPhysicNameByList(new ArrayList<>(stringListMap.keySet()));
            //拼接sql并插入
            customArchiveTableDataDao.insertCustom(s,getFieldSql(strings),getValueSql(strings));*/
        } else {
            //若此表为自定义表，说明已经存在了基本表。此时需要需要通过传过来的手机号找到业务id确认存进哪张表中
            String funcCode = customArchiveTableDao.selectFuncCode(tableId);
            if (ARCHIVE.equals(funcCode)) {
                //根据证件类型，证件号找到id，作为业务id
                List<String> list1 = stringListMap.get(IDTYPE);
                List<String> list2 = stringListMap.get(IDNUMBER);
                for (int i = 0; i < list1.size(); i++) {
                    //业务id
                    Integer id = userArchiveDao.selectId(list1.get(i), list2.get(i));
                    setCustom(userSession, tableId, stringListMap, i, id);
                }
            }
            if (PREEMP.equals(funcCode)) {
                List<String> list1 = stringListMap.get(PHONE);
                for (int i = 0; i < list1.size(); i++) {
                    Integer id = preEmploymentDao.selectIdByNumber(list1.get(i));
                    setCustom(userSession, tableId, stringListMap, i, id);
                }
            }
            logger.error("未找到对应的自定义表,导入失败");
        }
    }

    private void setCustom(UserSession userSession, Integer tableId, Map<String, List<String>> stringListMap, int i, Integer id) {
        CustomArchiveTableData customArchiveTableData = new CustomArchiveTableData();
        customArchiveTableData.setBusinessId(id);
        customArchiveTableData.setTableId(tableId);
        customArchiveTableData.setOperatorId(userSession.getArchiveId());
        customArchiveTableData.setIsDelete(0);
        String bigData = getBigData(stringListMap, i);
        customArchiveTableData.setBigData(bigData);
        Integer integer1 = customArchiveTableDataDao.selectTableIdByBusinessIdAndTableId(id, tableId);
        if ( null == integer1) {
            customArchiveTableDataDao.insertSelective(customArchiveTableData);
        } else {
            customArchiveTableDataDao.updateByPrimaryKey(customArchiveTableData);
        }
    }

    private void setValue(Map<String, List<String>> stringListMap, int i, Object o) throws NoSuchFieldException, IllegalAccessException {
        Set<Map.Entry<String, List<String>>> entries = stringListMap.entrySet();
        List<Map.Entry<String, List<String>>> entries1 = new ArrayList<>(entries);
        for (Map.Entry<String, List<String>> stringListEntry : entries1) {
            Field declaredField = o.getClass().getDeclaredField(stringListEntry.getKey());
            declaredField.setAccessible(true);
            declaredField.set(o, stringListEntry.getValue().get(i));
        }

    }

    private String getBigData(Map<String, List<String>> stringListMap, int i) {
        StringBuffer bigData = null;
        Set<Map.Entry<String, List<String>>> entries = stringListMap.entrySet();
        List<Map.Entry<String, List<String>>> entries1 = new ArrayList<>(entries);
        for (Map.Entry<String, List<String>> stringListEntry : entries1) {
            String key = stringListEntry.getKey();
            String s = stringListEntry.getValue().get(i);
            bigData.append("@@").append(key).append("@@:").append(s).append(";");
        }
        return bigData.toString();
    }

    @Override
    public void exportPreFile(String path, String title, List<Integer> list, UserSession userSession) throws NoSuchFieldException, IllegalAccessException {
        //得到response对象
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        if (CollectionUtils.isEmpty(list)) {
            list = preEmploymentDao.selectIdByComId(userSession.getCompanyId());
        }
        //通过企业id，物理表名找到tableId
        Integer tableId = customArchiveTableDao.selectByComIdAndPhyName(userSession.getCompanyId(), PREEMPTABLE);
        //通过tableid找到字段名集合
        List<String> heads = customArchiveFieldDao.selectFieldNameListByTableId(tableId);
        //找到预入职表
        List<PreEmployment> preEmploymentList = preEmploymentDao.selectByPrimaryKeyList(list);
        //预入职物理字段名
        downloadFile(path,title,response,preEmploymentList,heads);

    }

    @Override
    public void exportArcFile(String path, String title, Integer querySchemeId, List<Integer> list, UserSession userSession) throws NoSuchFieldException, IllegalAccessException {
        //得到response对象
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        if (CollectionUtils.isEmpty(list)) {
            list = userArchiveDao.selectIdByComId(userSession.getCompanyId());
        }
        List<UserArchive> userArclist = userArchiveDao.selectByPrimaryKeyList(list);
        //如果没有查询方案
        if (null == querySchemeId || "".equals(querySchemeId)) {
            //通过企业id，物理表名找到tableId
            Integer tableId = customArchiveTableDao.selectByComIdAndPhyName(userSession.getCompanyId(), ARCHIVETABLE);
            //通过tableid找到字段名集合
            List<String> heads = customArchiveFieldDao.selectFieldNameListByTableId(tableId);
            //找到物理表名集合
            downloadFile(path, title, response, userArclist, heads);
        } else {
            //根据查询方案id找到应该展示的字段id
            List<Integer> fieldIdList = querySchemeFieldDao.selectFieldId(querySchemeId);
            //根据id查字段名
            List<String> heads = customArchiveFieldDao.selectFieldNameByList(fieldIdList);
            List<Map<String, String>> dates = null;
            List<Map<String, String>> mapList = null;
            Map<String, String> stringMap = new HashMap<>();
            List<String> physicList = customArchiveFieldDao.selectPhysicNameByList(heads);
            //根据字段名判断是否是物理表里的字段
            for (String head : heads) {
                //如果是
                if (isSystem(head)) {
                    dates = getDates(heads, physicList, userArclist);
                }
                //如果不是
                //通过字段名找到表id
                Integer id = customArchiveFieldDao.selectTableIdByFieldName(head);
                //将传过来的id作为业务id寻找出数据大字段
                for (Integer integer : list) {
                    String bigData = customArchiveTableDataDao.selectBigDataBybusinessIdAndTableId(id, integer);
                    //大字段进行解析
                    String s = bigData.split("@@" + head + "@@:")[1].split(";@@")[0];
                    stringMap.put(head, s);
                }
                mapList.add(stringMap);
            }
            dates.addAll(mapList);
            //得到字段名与类型的集合
            Map<String, String> map = getStringStringMap(heads);
            ExcelUtil.download(path, response, title, heads, dates, map);
        }
    }

    private void downloadFile(String path, String title, HttpServletResponse response, List objectList, List<String> heads) throws NoSuchFieldException, IllegalAccessException {
        List<String> physicList = customArchiveFieldDao.selectPhysicNameByList(heads);
        List<Map<String, String>> dates = getDates(heads, physicList, objectList);
        //map中key是字段名，value是对应的类型
        Map<String, String> map = getStringStringMap(heads);
        ExcelUtil.download(path, response, title, heads, dates, map);
    }


    private List<Map<String, String>> getDates(List<String> heads, List<String> physicList, List objectList) throws NoSuchFieldException, IllegalAccessException {
        //map中key是字段名，value是对应的值
        List<Map<String, String>> dates = new ArrayList<>();
        //通过字段名找到物理字段名
        Map<String, String> dateMap = new HashMap<>();
        for (int i = 0; i < objectList.size(); i++) {
            for (int j = 0; j < physicList.size(); j++) {
                Field declaredField = objectList.get(i).getClass().getDeclaredField(physicList.get(j));
                declaredField.setAccessible(true);
                Object o = declaredField.get(declaredField);
                dateMap.put(heads.get(j), String.valueOf(o));
                dates.add(dateMap);
            }
        }
        return dates;
    }

    private Boolean isSystem(String fieldName) {
        Short isSystem = customArchiveFieldDao.isSystemField(fieldName);
        if (isSystem > 0) {
            return true;
        }
        return false;
    }

    private Map<String, String> getStringStringMap(List<String> heads) {
        Map<String, String> map = new HashMap<>();
        List<String> stringList = customArchiveFieldDao.selectTypeByNameList(heads);
        for (int i = 0; i < stringList.size(); i++) {
            map.put(heads.get(i), stringList.get(i));
        }
        return map;
    }

    @Override
    public void putFile(String path) {
        //TODO 文件对象键的定义
        String key = "";
        UpAndDownUtil.putFile(path, key);
    }

    @Override
    public ForWardPutFile UploadFileByForWard() {
        String s = UpAndDownUtil.TransToForward();
        ForWardPutFile forWardPutFile = new ForWardPutFile();
        forWardPutFile.setString(s);
        //TODO 对象键的定义需要制定规则，利用规则生成
        forWardPutFile.setKey("");
        return forWardPutFile;
    }
//    public String getFieldSql(List<String> list){
//        String fieldSql=null;
//        if(CollectionUtils.isEmpty(list)){
//            return null;
//        }else {
//            for (String s : list) {
//                fieldSql+=s+",";
//            }
//            String[] split = fieldSql.split(",", -1);
//            return split[0];
//        }
//    }
//    public String getValueSql(List<String> list){
//        String valueSql=null;
//        if(CollectionUtils.isEmpty(list)){
//            return null;
//        }else {
//            for (String s : list) {
//                valueSql+="object."+s+",";
//            }
//            String[] split = valueSql.split(",", -1);
//            return split[0];
//        }
//    }
}




