package com.qinjee.masterdata.service.organation.impl;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.CompanyInfoDao;
import com.qinjee.masterdata.dao.UserCompanyDao;
import com.qinjee.masterdata.dao.organation.OrganizationDao;
import com.qinjee.masterdata.dao.organation.PositionLevelDao;
import com.qinjee.masterdata.dao.organation.PostDao;
import com.qinjee.masterdata.dao.staffdao.contractdao.ContractParamDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.BlacklistDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.UserArchiveDao;
import com.qinjee.masterdata.dao.sys.SysDictDao;
import com.qinjee.masterdata.dao.userinfo.UserInfoDao;
import com.qinjee.masterdata.dao.userinfo.UserLoginDao;
import com.qinjee.masterdata.model.entity.*;
import com.qinjee.masterdata.model.vo.auth.UserInfoVO;
import com.qinjee.masterdata.model.vo.organization.OrganizationVO;
import com.qinjee.masterdata.model.vo.organization.PositionLevelVo;
import com.qinjee.masterdata.model.vo.organization.bo.UserArchiveImportBO;
import com.qinjee.masterdata.model.vo.organization.bo.UserArchivePageBO;
import com.qinjee.masterdata.model.vo.staff.UserArchiveVo;
import com.qinjee.masterdata.redis.RedisClusterService;
import com.qinjee.masterdata.service.auth.ArchiveAuthService;
import com.qinjee.masterdata.service.employeenumberrule.IEmployeeNumberRuleService;
import com.qinjee.masterdata.service.organation.AbstractOrganizationHelper;
import com.qinjee.masterdata.service.organation.OrganizationService;
import com.qinjee.masterdata.service.organation.UserArchiveService;
import com.qinjee.masterdata.service.userinfo.UserInfoService;
import com.qinjee.masterdata.utils.DealHeadParamUtil;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import com.qinjee.model.response.ResponseResult;
import com.qinjee.utils.MD5Utils;
import com.qinjee.utils.RegexpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserArchiveServiceImpl extends AbstractOrganizationHelper<UserArchiveImportBO, UserArchiveVo> implements UserArchiveService {
    private static Logger logger = LogManager.getLogger(UserArchiveServiceImpl.class);
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";
    @Autowired
    private UserArchiveDao userArchiveDao;
    @Autowired
    private RedisClusterService redisService;
    @Autowired
    private UserLoginDao userLoginDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserCompanyDao userCompanyDao;
    @Autowired
    private SysDictDao sysDictDao;

    @Autowired
    private OrganizationDao organizationDao;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PostDao postDao;
    @Autowired
    private BlacklistDao blacklistDao;
    @Autowired
    private CompanyInfoDao companyInfoDao;
    @Autowired
    private ArchiveAuthService archiveAuthService;
    @Autowired
    private PositionLevelDao positionLevelDao;
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private IEmployeeNumberRuleService employeeNumberRuleService;
    @Autowired
    private ContractParamDao contractParamDao;


    @Override
    public ResponseResult<PageResult<UserArchiveVo>> getUserArchiveList(UserArchivePageBO pageQueryVo, UserSession userSession) {


        //判断是否是顶级机构，如果是 则将没有机构id的用户也查出来
        OrganizationVO org = organizationDao.getOrganizationById(pageQueryVo.getOrgId());
        boolean isContains = false;
        if (org.getOrgParentId() == null || org.getOrgParentId().equals(0)) {
            isContains = true;
        }

        //0表示不查询封存机构下的用户
        List<Integer> orgIdList = organizationDao.getOrgIds(pageQueryVo.getOrgId(), userSession.getArchiveId(), Short.valueOf("0"), new Date());
        logger.info("查询机构下用户，机构id：" + orgIdList);
        if (pageQueryVo.getCurrentPage() != null && pageQueryVo.getPageSize() != null) {
            PageHelper.startPage(pageQueryVo.getCurrentPage(), pageQueryVo.getPageSize());
        }
        String whereSql = DealHeadParamUtil.getWhereSql(pageQueryVo.getTableHeadParamList(), "lastTable.");
        String orderSql = DealHeadParamUtil.getOrderSql(pageQueryVo.getTableHeadParamList(), "lastTable.");
        List<UserArchiveVo> userArchiveList = userArchiveDao.getUserArchiveList(orgIdList, isContains, whereSql, orderSql);
        PageInfo<UserArchiveVo> pageInfo = new PageInfo<>(userArchiveList);
        PageResult<UserArchiveVo> pageResult = new PageResult<>(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return new ResponseResult<>(pageResult);
    }


    /**
     * 删除时不要删除账号信息，只将用户当前企业下的档案信息，以及用户企业关系表的信息
     * 不能删除当前用户信息
     *
     * @param idsMap
     * @return
     */
    @Override
    public void deleteUserArchive(Map<Integer, Integer> idsMap, Integer currentArchiveId, Integer companyId) {
        //TODO 删人员在后续会通过走流程控制，目前只要有删除用户权限即可进行删除操作，可以先忽略这种情况
        //查询出超级超级管理员(企业创建人)，不能删除
        UserArchiveVo ua= userArchiveDao.getCompanyCreator(companyId);
        Set<Integer> userIds = idsMap.keySet();
        if(userIds.contains(ua.getUserId())){
            ExceptionCast.cast(CommonCode.CREATER_CAN_NOT_DEL);
        }
        //entry中key为userId，value为archiveId
        for (Map.Entry<Integer, Integer> entry : idsMap.entrySet()) {
            //TODO 权限判断 未完整
            if (currentArchiveId.equals(entry.getValue())) {//如果是当前登录用户
                ExceptionCast.cast(CommonCode.DO_NOT_DEL_CURRENT_USER);
            }
            //清除企业关联
            userInfoDao.clearUserCompany(entry.getKey(), companyId, new Date());
            /*//删除关联的角色关系
            archiveAuthService.delUserRoleRelationByArchiveId(currentArchiveId,entry.getValue());*/
            //删除档案
            UserArchive userArchive = new UserArchive();
            userArchive.setIsDelete((short) 1);
            userArchive.setArchiveId(entry.getValue());
            userArchive.setUpdateTime(new Date());
            userArchiveDao.updateByPrimaryKeySelective(userArchive);
        }
    }

    @Override
    public void editUserArchive(UserArchiveVo userArchiveVo, UserSession userSession) {

        logger.info("编辑用户信息：userArchiveVo：" + userArchiveVo);
        UserInfoVO userInfoVO = userLoginDao.searchUserCompanyByUserIdAndCompanyId(userArchiveVo.getUserId(), userSession.getCompanyId());
        UserInfo userByPhone = userInfoDao.getUserByPhone(userArchiveVo.getPhone());
        logger.info("根据手机号查到的用户：" + userByPhone);
        if (Objects.nonNull(userByPhone) && !userByPhone.getUserId().equals(userInfoVO.getUserId())) {

            ExceptionCast.cast(CommonCode.PHONE_ALREADY_EXIST);
        }
        //userInfoVO.setUserName(userArchiveVo.getUserName());
        userInfoVO.setPhone(userArchiveVo.getPhone());
        userInfoVO.setEmail(userArchiveVo.getEmail());

        userInfoDao.editUserInfo(userInfoVO);
        UserArchiveVo userArchiveVo1 = userArchiveDao.selectBasicById(userArchiveVo.getArchiveId());

        UserArchive userArchive = new UserArchive();
        BeanUtils.copyProperties(userArchiveVo, userArchiveVo1);
        BeanUtils.copyProperties(userArchiveVo1, userArchive);
        logger.info("编辑的userArchive：" + userArchive);
        userArchiveDao.updateByPrimaryKeySelective(userArchive);
    }

    @Override
    public ResponseResult uploadAndCheck(MultipartFile multfile, UserSession userSession) throws Exception {
        return doUploadAndCheck(multfile, UserArchiveImportBO.class, userSession);
    }

    @Override
    @Transactional
    /**
     * 1.添加账号（如果已存在则不更新）
     * 2.关联企业
     * 3.新增档案（用户在一家企业下只有一份档案）
     */
    public ResponseResult<Integer> addUserArchive(UserArchiveVo userArchiveVo, UserSession userSession) {
        //黑名单校验
        List<Blacklist> blacklistsMem = blacklistDao.selectByPage(userSession.getCompanyId());
        if (CollectionUtils.isNotEmpty(blacklistsMem)) {
            //”XX曾于XX年月日被XX公司因XX原因被列入黑名单，不允许入职/投递简历，请联系该公司处理!"
            //查询公司名称
            CompanyInfo companyInfo = companyInfoDao.selectByPrimaryKey(userSession.getCompanyId());
            //如果身份证在黑名单表中存在  或者（手机号+姓名）在身份证中存在
            List<Blacklist> blacklist = blacklistsMem.stream().filter(a -> (userArchiveVo.getPhone().equals(a.getPhone()) && userArchiveVo.getUserName().equals(a.getUserName())) || (null != userArchiveVo.getIdNumber() && userArchiveVo.getIdNumber().equals(a.getIdNumber()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(blacklist)) {
                CommonCode isExistBlacklist = CommonCode.IS_EXIST_BLACKLIST;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                String reason = "";
                if (null != blacklist.get(0).getBlockReason()) {
                    reason = blacklist.get(0).getBlockReason();
                }
                String msg = blacklist.get(0).getUserName() + "曾于" + sdf.format(blacklist.get(0).getBlockTime()) + "被" + companyInfo.getCompanyName() + "因[ " + reason + " ]原因列入黑名单，不允许入职/投递简历，请联系该公司处理!";
                isExistBlacklist.setMessage(msg);
                ExceptionCast.cast(isExistBlacklist);
            }
        }

        //根据手机号查找账号表，如果存在则不对账号表进行处理
        UserInfo userInfo = userInfoDao.getUserByPhone(userArchiveVo.getPhone());
        if (Objects.isNull(userInfo)) {
            userInfo = new UserInfo();
            userInfo.setPhone(userArchiveVo.getPhone());
            userInfo.setEmail(userArchiveVo.getEmail());
            userInfo.setCreateTime(new Date());
            //密码默认手机号后6位
            String p = StringUtils.substring(userArchiveVo.getPhone(), userArchiveVo.getPhone().length() - 6, userArchiveVo.getPhone().length());
            userInfo.setPassword(MD5Utils.getMd5(p));
            userLoginDao.addUserInfo(userInfo);

        } else {
            UserInfo user = userInfoDao.getUserByPhoneAndCompanyId(userArchiveVo.getPhone(), userSession.getCompanyId());
            if (Objects.nonNull(user)) {
                ExceptionCast.cast(CommonCode.PHONE_ALREADY_EXIST);
            }
            userInfoService.changeCompany(userInfo.getUserId(),userSession.getCompanyId());
        }

        //维护员工企业关系表
        UserCompany userCompany = new UserCompany();
        userCompany.setCreateTime(new Date());
        userCompany.setUserId(userInfo.getUserId());
        userCompany.setCompanyId(userSession.getCompanyId());
        userCompany.setIsEnable((short) 1);
        userCompanyDao.insertSelective(userCompany);

        UserArchive userArchive = new UserArchive();
        BeanUtils.copyProperties(userArchiveVo, userArchive);

        userArchive.setOperatorId(userSession.getArchiveId());
        userArchive.setCompanyId(userSession.getCompanyId());
        userArchive.setUserId(userInfo.getUserId());
        userArchive.setIsDelete((short) 0);
        userArchiveDao.insertSelective(userArchive);
        try {
            checkEmployeeNumber(userSession, userArchive);
            userArchiveDao.updateByPrimaryKeySelective(userArchive);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 记录异常 ExceptionCast.cast(CommonCode.FAIL);
        }
        return new ResponseResult(userArchive.getArchiveId());
    }


    @Override
    public void importToDatabase(String orgExcelRedisKey, UserSession userSession) {
        String data = redisService.get(orgExcelRedisKey.trim());
        //将其转为对象集合
        List<UserArchiveVo> excelArchivelist = JSONArray.parseArray(data, UserArchiveVo.class);
        if (CollectionUtils.isNotEmpty(excelArchivelist)) {
            List<SysDict> sysDictsMem = sysDictDao.searchSomeSysDictList();
            //直接遍历入库  账户信息表  账户企业关联表 用户档案表
            for (UserArchiveVo excelArchiveVo : excelArchivelist) {
                UserInfo user = userInfoDao.getUserByPhoneAndCompanyId(excelArchiveVo.getPhone(), userSession.getCompanyId());
                //存在账户并且已与当前企业关联
                if (Objects.nonNull(user)) {
                    //TODO 企业有没有权限更新平台用户的账户表
                } else {
                    user = userInfoDao.getUserByPhone(excelArchiveVo.getPhone());
                    if (Objects.nonNull(user)) {//存在账户，但没有关联当前企业
                        //绑定企业
                        UserCompany userCompany = new UserCompany();
                        userCompany.setCreateTime(new Date());
                        userCompany.setUserId(user.getUserId());
                        userCompany.setCompanyId(userSession.getCompanyId());
                        userCompany.setIsEnable((short) 1);
                        userCompanyDao.insertSelective(userCompany);
                        userInfoService.changeCompany(user.getUserId(),userSession.getCompanyId());
                    } else {
                        //不存在账号，新注册一个账号并绑定当前企业
                        user = new UserInfo();
                        user.setPhone(excelArchiveVo.getPhone());
                        user.setEmail(excelArchiveVo.getEmail());
                        user.setCreateTime(new Date());
                        //密码默认手机号后6位
                        String passwd = StringUtils.substring(excelArchiveVo.getPhone(), excelArchiveVo.getPhone().length() - 6, excelArchiveVo.getPhone().length());
                        user.setPassword(MD5Utils.getMd5(passwd));
                        userLoginDao.addUserInfo(user);
                        UserCompany userCompany = new UserCompany();
                        userCompany.setCreateTime(new Date());
                        userCompany.setUserId(user.getUserId());
                        userCompany.setCompanyId(userSession.getCompanyId());
                        userCompany.setIsEnable((short) 1);
                        userCompanyDao.insertSelective(userCompany);
                    }
                }
                UserArchiveVo oldArchiveVo = userArchiveDao.selectByUserId(user.getUserId(), userSession.getCompanyId());
                if (Objects.nonNull(oldArchiveVo)) {
                    UserArchive userArchive = buildUserArchive(excelArchiveVo, oldArchiveVo, sysDictsMem, userSession);
                    userArchive.setArchiveId(oldArchiveVo.getArchiveId());
                    userArchive.setUserId(user.getUserId());
                    userArchive.setUpdateTime(new Date());
                    userArchiveDao.updateByPrimaryKeySelective(userArchive);
                } else {
                    UserArchive userArchive = buildUserArchive(excelArchiveVo, oldArchiveVo, sysDictsMem, userSession);
                    userArchive.setCreateTime(new Date());
                    userArchive.setUserId(user.getUserId());
                    userArchiveDao.insertSelective(userArchive);
                }
            }//外层循环结束
        }
    }

    private UserArchive buildUserArchive(UserArchiveVo excelArchiveVo, UserArchiveVo oldArchiveVo, List<SysDict> sysDictsMem, UserSession userSession) {
        UserArchive userArchive = new UserArchive();
        if (Objects.nonNull(oldArchiveVo)) {
            BeanUtils.copyProperties(oldArchiveVo, userArchive);
        }
        BeanUtils.copyProperties(excelArchiveVo, userArchive);
        userArchive.setCompanyId(userSession.getCompanyId());
        userArchive.setOperatorId(userSession.getArchiveId());

        //设置部门id
        if (StringUtils.isNotBlank(excelArchiveVo.getOrgCode())) {
            OrganizationVO org = organizationDao.getOrganizationByOrgCodeAndCompanyId(excelArchiveVo.getOrgCode(), userSession.getCompanyId());
            userArchive.setOrgId(org.getOrgId());
            //设置单位id
            //根据部门id查找单位id
            Integer unitId = organizationService.getBusunessUnitIdByOrgId(org.getOrgId());
            userArchive.setBusinessUnitId(unitId);

        }
        //设置岗位id
        if (StringUtils.isNotBlank(excelArchiveVo.getPostCode())) {
            Post post = postDao.getPostsByPostCode(excelArchiveVo.getPostCode(), userSession.getCompanyId());
            userArchive.setPostId(post.getPostId());
        }

        //设置职级id
        if(StringUtils.isNotBlank(excelArchiveVo.getPositionLevelName())){
            PositionLevelVo positionLevel=positionLevelDao.getByPositionLevelName(excelArchiveVo.getPositionLevelName(),userSession.getCompanyId());
            userArchive.setPositionLevelId(positionLevel.getPositionLevelId());
        }

        //设置直接上级id
        if (StringUtils.isNotBlank(excelArchiveVo.getEmployeeNumber())) {
            Integer archiveId = userArchiveDao.selectArchiveIdByNumber(excelArchiveVo.getSupervisorEmployeeNumber(), userSession.getCompanyId());
            userArchive.setSupervisorId(archiveId);
        }

        //进行一些字典设置--------------------------
        //性别
        Optional<SysDict> sexDictOp = sysDictsMem.stream().filter(a -> "SEX_TYPE".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getGender()) && excelArchiveVo.getGender().equals(a.getDictValue())).findFirst();
        if (sexDictOp.isPresent()) {
            userArchive.setGender(sexDictOp.get().getDictCode());
        } else {
            userArchive.setGender(null);
        }
        //证件类型
        Optional<SysDict> idTypeDictOp = sysDictsMem.stream().filter(a -> "CARD_TYPE".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getIdType()) && excelArchiveVo.getIdType().equals(a.getDictValue())).findFirst();
        if (idTypeDictOp.isPresent()) {
            userArchive.setIdType(idTypeDictOp.get().getDictCode());
        } else {
            userArchive.setIdType(null);
        }

        //第一学历
        Optional<SysDict> firstDegreeDictOp = sysDictsMem.stream().filter(a -> "DEGREE".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getFirstDegree()) && excelArchiveVo.getFirstDegree().equals(a.getDictValue())).findFirst();
        if (firstDegreeDictOp.isPresent()) {
            userArchive.setFirstDegree(firstDegreeDictOp.get().getDictCode());
        } else {
            userArchive.setFirstDegree(null);
        }
        //最高学历
        Optional<SysDict> topDegreeDictOp = sysDictsMem.stream().filter(a -> "DEGREE".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getHighestDegree()) && excelArchiveVo.getHighestDegree().equals(a.getDictValue())).findFirst();
        if (topDegreeDictOp.isPresent()) {
            userArchive.setHighestDegree(topDegreeDictOp.get().getDictCode());
        } else {
            userArchive.setHighestDegree(null);
        }
        //民族
        Optional<SysDict> nationalityDictOp = sysDictsMem.stream().filter(a -> "NATIONALITY".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getNationality()) && excelArchiveVo.getNationality().equals(a.getDictValue())).findFirst();
        if (nationalityDictOp.isPresent()) {
            userArchive.setNationality(nationalityDictOp.get().getDictCode());
        } else {
            userArchive.setNationality(null);
        }
        //婚姻状况
        Optional<SysDict> maritalStatusDictOp = sysDictsMem.stream().filter(a -> "MARITAL_STATUS".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getMaritalStatus()) && excelArchiveVo.getMaritalStatus().equals(a.getDictValue())).findFirst();
        if (maritalStatusDictOp.isPresent()) {
            userArchive.setMaritalStatus(maritalStatusDictOp.get().getDictCode());
        } else {
            userArchive.setMaritalStatus(null);
        }
        //政治面貌
        Optional<SysDict> politicalAffiliationDictOp = sysDictsMem.stream().filter(a -> "POLITICAL_AFFILIATION".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getPoliticalStatus()) && excelArchiveVo.getPoliticalStatus().equals(a.getDictValue())).findFirst();
        if (politicalAffiliationDictOp.isPresent()) {
            userArchive.setPoliticalStatus(politicalAffiliationDictOp.get().getDictCode());
        } else {
            userArchive.setPoliticalStatus(null);
        }
        //职业资格
        Optional<SysDict> professionalCertificationDictOp = sysDictsMem.stream().filter(a -> "PROFESSIONAL_CERTIFICATION".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getProfessionalCertification()) && excelArchiveVo.getProfessionalCertification().equals(a.getDictValue())).findFirst();
        if (professionalCertificationDictOp.isPresent()) {
            userArchive.setProfessionalCertification(professionalCertificationDictOp.get().getDictCode());
        } else {
            userArchive.setProfessionalCertification(null);
        }

        //职称
        Optional<SysDict> professionalTitleDictOp = sysDictsMem.stream().filter(a -> "PROFESSIONAL_TITLE".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getProfessionalTitle()) && excelArchiveVo.getProfessionalTitle().equals(a.getDictValue())).findFirst();
        if (professionalTitleDictOp.isPresent()) {
            userArchive.setProfessionalTitle(professionalTitleDictOp.get().getDictCode());
        } else {
            userArchive.setProfessionalTitle(null);
        }

        //职称等级
        Optional<SysDict> professionalLevelDictOp = sysDictsMem.stream().filter(a -> "PROFESSIONAL_LEVEL".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getProfessionalLevel()) && excelArchiveVo.getProfessionalLevel().equals(a.getDictValue())).findFirst();
        if (professionalLevelDictOp.isPresent()) {
            userArchive.setProfessionalLevel(professionalLevelDictOp.get().getDictCode());
        } else {
            userArchive.setProfessionalLevel(null);
        }

        //人员分类
        Optional<SysDict> userCategoryDictOp = sysDictsMem.stream().filter(a -> "USER_CATEGORY".equals(a.getDictType()) && StringUtils.isNotBlank(excelArchiveVo.getUserCategory()) && excelArchiveVo.getUserCategory().equals(a.getDictValue())).findFirst();
        if (userCategoryDictOp.isPresent()) {
            userArchive.setUserCategory(userCategoryDictOp.get().getDictCode());

        } else {
            userArchive.setUserCategory(null);
        }
        return userArchive;
    }

    private void checkEmployeeNumber(UserSession userSession, UserArchive userArchive) {
        String empNumber = employeeNumberRuleService.createEmpNumber(userSession.getCompanyId());
        List<Integer> employnumberList = userArchiveDao.selectEmployNumberByCompanyId(userSession.getCompanyId(), userArchive.getEmployeeNumber());
        if (CollectionUtils.isEmpty(employnumberList) || (employnumberList.size() == 1 && employnumberList.get(0).equals(userArchive.getArchiveId()))) {
            userArchive.setEmployeeNumber(empNumber);
        } else {
            ExceptionCast.cast(CommonCode.EMPLOYEENUMBER_IS_EXIST);
        }
    }


    @Override
    protected List<UserArchiveVo> checkExcel(List<UserArchiveImportBO> boList, UserSession userSession) {

        List<UserArchiveVo> checkList = new ArrayList<>(boList.size());
        List<UserArchiveVo> archiveVoByCompanyIdMem = userArchiveDao.getByCompanyId(userSession.getCompanyId());
        List<SysDict> sysDictsMem = sysDictDao.searchSomeSysDictList();
        //查到用户有权的机构
        List<OrganizationVO> organizationVOListMem = organizationDao.listAllOrganizationByArchiveId(userSession.getArchiveId(), null, new Date());
        //根据有权的机构id查询岗位
        List<Integer> orgIds = organizationVOListMem.stream().map(OrganizationVO::getOrgId).collect(Collectors.toList());
        List<Post> postsMem = postDao.listPostsByOrgIds(orgIds, null, null);
        List<PositionLevelVo> positionLevelsMem = positionLevelDao.list(userSession.getCompanyId());
        List<Blacklist> blacklistsMem = blacklistDao.selectByPage(userSession.getCompanyId());
        for (UserArchiveImportBO bo : boList) {
            StringBuilder resultMsg = new StringBuilder(1024);
            //先校验必填项 手机号 姓名 证件号
            if (StringUtils.isBlank(bo.getUserName())) {
                bo.setCheckResult(false);
                resultMsg.append("用户姓名不能为空 | ");
            }
            if (StringUtils.isBlank(bo.getPhone())) {
                bo.setCheckResult(false);
                resultMsg.append("手机号不能为空 | ");
            }
            if (StringUtils.isBlank(bo.getIdNumber())) {
                bo.setCheckResult(false);
                resultMsg.append("证件号码不能为空 | ");
            }

            //黑名单校验
            ////////////////////begin update date:2020-01-17; author:zhouyun
            Boolean anyMatch = false;

            //先判断手机号和用户名是否为空，不为空则验证黑名单是否存在
            if (StringUtils.isNoneBlank(bo.getPhone()) && StringUtils.isNoneBlank(bo.getUserName())) {
                anyMatch = blacklistsMem.stream().anyMatch(a -> (bo.getPhone().equals(a.getPhone()) && bo.getUserName().equals(a.getUserName())));
            }

            //判断上一步手机号和用户名是否能查到黑名单，查不到再判断证件号码是否为空，不为空再使用证件号码验证黑名单是否存在
            if (!anyMatch) {
                if (StringUtils.isNoneBlank(bo.getIdNumber())) {
                    anyMatch = blacklistsMem.stream().anyMatch(a -> (null != bo.getIdNumber() && (bo.getIdNumber().equals(a.getIdNumber()))));
                }
            }

            //两步判断只要有一个为true即说明黑名单已存在
            if (anyMatch) {
                bo.setCheckResult(false);
                resultMsg.append("用户已存在于黑名单 | ");
            }
            ////////////////////end update date:2020-01-17; author:zhouyun

            //如果同时存在证件号和工号 //则判断是否相等
            if (StringUtils.isNotBlank(bo.getEmployeeNumber()) && StringUtils.isNotBlank(bo.getIdNumber())) {
                Optional<UserArchiveVo> first = archiveVoByCompanyIdMem.stream().filter(a -> StringUtils.equals(bo.getIdNumber(), a.getIdNumber())).findFirst();
                if (first.isPresent()) {
                    UserArchiveVo archiveVo = first.get();
                    if (Objects.nonNull(archiveVo) && Objects.nonNull(archiveVo.getEmployeeNumber()) && !archiveVo.getEmployeeNumber().equals(bo.getEmployeeNumber())) {
                        bo.setCheckResult(false);
                        resultMsg.append("证件号码与其对应的工号人员不一致 | ");
                    }
                }

            }
            //--------------下面的进行字典验证
            if (StringUtils.isNotBlank(bo.getGender())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "SEX_TYPE".equals(a.getDictType()) && bo.getGender().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);

                    resultMsg.append("性别中没有[" + bo.getGender() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getIdType())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "CARD_TYPE".equals(a.getDictType()) && bo.getIdType().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);

                    resultMsg.append("证件类型中没有[" + bo.getIdType() + "]的选项 | ");
                }
            }

            if (StringUtils.isNotBlank(bo.getNationality())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "NATIONALITY".equals(a.getDictType()) && bo.getNationality().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("民族中没有[" + bo.getNationality() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getHighestDegree())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "DEGREE".equals(a.getDictType()) && bo.getHighestDegree().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("最高学历中没有[" + bo.getHighestDegree() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getFirstDegree())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "DEGREE".equals(a.getDictType()) && bo.getFirstDegree().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("第一学历中没有[" + bo.getFirstDegree() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getMaritalStatus())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "MARITAL_STATUS".equals(a.getDictType()) && bo.getMaritalStatus().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("婚姻状况中没有[" + bo.getMaritalStatus() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getPoliticalStatus())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "POLITICAL_AFFILIATION".equals(a.getDictType()) && bo.getPoliticalStatus().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("政治面貌中没有[" + bo.getPoliticalStatus() + "]的选项 | ");
                }
            }

            if (StringUtils.isNotBlank(bo.getProfessionalCertification())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "PROFESSIONAL_CERTIFICATION".equals(a.getDictType()) && bo.getProfessionalCertification().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("职业资格中没有[" + bo.getProfessionalCertification() + "]的选项 | ");
                }
            }

            if (StringUtils.isNotBlank(bo.getProfessionalTitle())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "PROFESSIONAL_TITLE".equals(a.getDictType()) && bo.getProfessionalTitle().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("职称中没有[" + bo.getProfessionalTitle() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getProfessionalLevel())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "PROFESSIONAL_LEVEL".equals(a.getDictType()) && bo.getProfessionalLevel().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("职业等级中没有[" + bo.getProfessionalLevel() + "]的选项 | ");
                }
            }
            if (StringUtils.isNotBlank(bo.getUserCategory())) {
                boolean bool = sysDictsMem.stream().anyMatch(a -> "USER_CATEGORY".equals(a.getDictType()) && bo.getUserCategory().equals(a.getDictValue()));
                if (!bool) {
                    bo.setCheckResult(false);
                    resultMsg.append("人员分类中没有[" + bo.getUserCategory() + "]的选项 | ");
                }
            }

            //判断部门编码是否存在，如果存在再判断名称是否匹配
            if (StringUtils.isNotBlank(bo.getOrgCode())) {
                Optional<OrganizationVO> orgOp = organizationVOListMem.stream().filter(a -> bo.getOrgCode().equals(a.getOrgCode())).findFirst();
                if (!orgOp.isPresent()) {
                    bo.setCheckResult(false);
                    resultMsg.append("机构编码[" + bo.getOrgCode() + "]不存在 | ");
                } else {
                    if (StringUtils.isNotBlank(bo.getOrgName())) {
                        if (!bo.getOrgName().equals(orgOp.get().getOrgName())) {
                            bo.setCheckResult(false);
                            resultMsg.append("机构名称不匹配| ");
                        }
                    }
                }
            }

            //判断岗位编码是否存在，如果存在再判断名称是否匹配
            if (StringUtils.isNotBlank(bo.getPostCode())) {
                Optional<Post> postOp = postsMem.stream().filter(a -> bo.getPostCode().equals(a.getPostCode())).findFirst();
                if (!postOp.isPresent()) {
                    bo.setCheckResult(false);
                    resultMsg.append("岗位编码[" + bo.getPostCode() + "]不存在 | ");
                } else {
                    if (StringUtils.isNotBlank(bo.getPostName())) {
                        if (!bo.getPostName().equals(postOp.get().getPostName())) {
                            bo.setCheckResult(false);
                            resultMsg.append("岗位名称不匹配| ");
                        }
                    }
                }
            }

            //TODO 判断直接上级工号是否存在

            //--------------下面的进行格式验证
            //年龄只能是整数
            if (StringUtils.isNotBlank(bo.getAge())) {
                if (!StringUtils.isNumeric(bo.getAge())) {
                    bo.setCheckResult(false);
                    resultMsg.append("年龄只能是整数| ");
                }
            }
            //工龄只能是整数
            if (StringUtils.isNotBlank(bo.getWorkingPeriod())) {
                if (!StringUtils.isNumeric(bo.getWorkingPeriod())) {
                    bo.setCheckResult(false);
                    resultMsg.append("工龄只能是整数| ");
                }
            }

            //司龄只能是整数
            if (StringUtils.isNotBlank(bo.getServingAge())) {
                if (!StringUtils.isNumeric(bo.getServingAge())) {
                    bo.setCheckResult(false);
                    resultMsg.append("年龄只能是整数| ");
                }
            }
            //试用（月）只能是整数
            if (StringUtils.isNotBlank(bo.getProbationPeriod())) {
                if (!StringUtils.isNumeric(bo.getProbationPeriod())) {
                    bo.setCheckResult(false);
                    resultMsg.append("试用期限（月）只能是整数| ");
                }
            }

            //任职时间只能是时间类型
            if (StringUtils.isNoneBlank(bo.getServingDate())) {
                boolean b = RegexpUtils.checkDate(bo.getServingDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("任职时间格式错误| ");
                }
            }

            //参加工作时间只能是时间类型
            if (StringUtils.isNoneBlank(bo.getFirstWorkDate())) {
                boolean b = RegexpUtils.checkDate(bo.getFirstWorkDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("参加工作时间格式错误| ");
                }
            }
            //试用到期时间只能是时间类型
            if (StringUtils.isNoneBlank(bo.getProbationDueDate())) {
                boolean b = RegexpUtils.checkDate(bo.getProbationDueDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("试用到期时间格式错误| ");
                }
            }
            //入职时间只能是时间类型
            if (StringUtils.isNoneBlank(bo.getHireDate())) {
                boolean b = RegexpUtils.checkDate(bo.getHireDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("入职时间格式错误| ");
                }
            }
            //转正时间只能是时间类型
            if (StringUtils.isNoneBlank(bo.getConverseDate())) {
                boolean b = RegexpUtils.checkDate(bo.getConverseDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("转正时间格式错误| ");
                }
            }
            //出生日期只能是时间类型
            if (StringUtils.isNoneBlank(bo.getBirthDate())) {
                boolean b = RegexpUtils.checkDate(bo.getBirthDate());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("出生日期时间格式错误| ");
                }
            }

            //联系电话不符合格式
            if (StringUtils.isNotBlank(bo.getPhone())) {
                boolean b = RegexpUtils.checkPhone(bo.getPhone());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("联系电话格式错误| ");
                }
            }

            //邮箱不符合格式
            if (StringUtils.isNotBlank(bo.getEmail())) {
                boolean b = RegexpUtils.checkEmail(bo.getEmail());
                if (!b) {
                    bo.setCheckResult(false);
                    resultMsg.append("邮箱格式错误| ");
                }
            }

            //职级
            if (StringUtils.isNotBlank(bo.getPositionLevelName())) {
                boolean bool = positionLevelsMem.stream().anyMatch(p -> null != p.getPositionLevelName() && p.getPositionLevelName().equals(bo.getPositionLevelName()));
                if(!bool){
                    bo.setCheckResult(false);
                    resultMsg.append("职级["+bo.getPositionLevelName()+"]不存在| ");
                }
            }


            if (resultMsg.length() > 2) {
                resultMsg.deleteCharAt(resultMsg.length() - 2);
            }

            bo.setResultMsg(resultMsg.toString());
            if (!bo.isCheckResult()) {
                UserArchiveVo vo = new UserArchiveVo();
                BeanUtils.copyProperties(bo, vo);
                checkList.add(vo);
            }
        }
        return checkList;
    }
}
