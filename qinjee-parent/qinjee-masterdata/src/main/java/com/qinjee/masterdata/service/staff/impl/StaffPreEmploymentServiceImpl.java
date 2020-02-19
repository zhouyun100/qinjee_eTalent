package com.qinjee.masterdata.service.staff.impl;

import com.github.pagehelper.PageHelper;
import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.AttachmentRecordDao;
import com.qinjee.masterdata.dao.CompanyInfoDao;
import com.qinjee.masterdata.dao.custom.CustomTableFieldDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.BlacklistDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.PreEmploymentChangeDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.PreEmploymentDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.UserArchiveDao;
import com.qinjee.masterdata.model.entity.*;
import com.qinjee.masterdata.model.vo.staff.PreEmploymentVo;
import com.qinjee.masterdata.model.vo.staff.StatusChangeVo;
import com.qinjee.masterdata.model.vo.staff.UserArchiveVo;
import com.qinjee.masterdata.service.employeenumberrule.IEmployeeNumberRuleService;
import com.qinjee.masterdata.service.staff.IStaffPreEmploymentService;
import com.qinjee.masterdata.service.userinfo.UserLoginService;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import com.qinjee.model.response.PageResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Service
public class StaffPreEmploymentServiceImpl implements IStaffPreEmploymentService {
    private static final Logger logger = LoggerFactory.getLogger ( StaffPreEmploymentServiceImpl.class );
    private static final String CHANGSTATUS_DELAY = "已延期";
    private static final String CHANGSTATUS_READY = "已入职";
    private static final String CHANGSTATUS_BLACKLIST = "黑名单";
    private static final String CHANGSTATUS_GIVEUP = "放弃入职";
    private static final String PRE_EMPLOYMENT = "t_pre_employment";
    private static final String PRE_DATASOURSE = "PRE";

    @Autowired
    private PreEmploymentDao preEmploymentDao;
    @Autowired
    private UserArchiveDao userArchiveDao;
    @Autowired
    private PreEmploymentChangeDao preEmploymentChangeDao;
    @Autowired
    private BlacklistDao blacklistDao;
    @Autowired
    private CustomTableFieldDao customTableFieldDao;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private IEmployeeNumberRuleService employeeNumberRuleService;
    @Autowired
    private CompanyInfoDao companyInfoDao;
    @Autowired
    private AttachmentRecordDao attachmentRecordDao;


    /**
     * 说明：这一张表对应两个页面，一个为延期入职页面，一个为放弃入职页面，在PreEmploymentChange中进行了整合
     * 梳理：预入职状态分为未入职，已入职，延期入职，放弃入职，黑名单。
     * 当变更表的状态为放弃入职时，将放弃入职原因入库，描述为变更描述
     * 为延期入职时，需要将页面的延期入职时间传过来，存到预入职表中
     * 黑名单时需要将黑名单同步到黑名单表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertStatusChange(UserSession userSession, StatusChangeVo statusChangeVo)  {
        String changeState = statusChangeVo.getChangeState ();
        List < Integer > preEmploymentList = statusChangeVo.getPreEmploymentList ();
        for (Integer preEmploymentId : preEmploymentList) {
            PreEmployment preEmployment = preEmploymentDao.selectByPrimaryKey ( preEmploymentId );
            List < PreEmploymentChange > preEmploymentChanges = preEmploymentChangeDao.selectByPreId ( preEmploymentId );
            if (CHANGSTATUS_READY.equals ( changeState )) {
                PreEmploymentChange existPreChange = isExistPreChange ( CHANGSTATUS_READY, preEmploymentChanges );
                //如果存在就更新
                if (existPreChange != null) {
                    BeanUtils.copyProperties ( statusChangeVo, existPreChange );
                    preEmploymentChangeDao.updateByPrimaryKeySelective ( existPreChange );
                } else {
                    //新增变更表
                    getPreEmploymentChange ( userSession.getArchiveId (), preEmploymentId, statusChangeVo );
                }
                preEmployment.setEmploymentState ( CHANGSTATUS_READY );
                preEmploymentDao.updateByPrimaryKey ( preEmployment );
                //根据预入职id查找预入职对象
                //预入职信息转化为档案信息
                //新增档案表
                String phone = preEmployment.getPhone ();
                List<Integer> integer = userArchiveDao.selectByPhoneAndCompanyId ( phone, userSession.getCompanyId () );
                if(CollectionUtils.isNotEmpty(integer)){
                    ExceptionCast.cast ( CommonCode.STAFF_IS_EXIST );
                }else{
                  List<UserArchiveVo> userArchiveVo=userArchiveDao.selectByIdNumber ( preEmployment.getIdNumber (),userSession.getCompanyId () );
                  if(CollectionUtils.isNotEmpty ( userArchiveVo )){
                      ExceptionCast.cast ( CommonCode.STAFF_IS_EXIST );
                  }else{
                      UserArchive userArchive = new UserArchive ();
                      preEmployment.setEmploymentState ( CHANGSTATUS_READY );
                      BeanUtils.copyProperties ( preEmployment, userArchive );
                      Integer userId = userLoginService.getUserIdByPhone ( phone,userSession.getCompanyId () );
                      //目前一家公司只有一个参数表
                      userArchive.setUserId ( userId );
                      userArchiveDao.insertSelective ( userArchive );

                      checkEmployeeNumber ( userSession,userArchive );
                      userArchiveDao.updateByPrimaryKeySelective ( userArchive );
                     //将附件信息同步到档案
                      List<AttachmentRecord> list=attachmentRecordDao.selectByBuinessId(preEmployment.getEmploymentId (),"employment",userSession.getCompanyId ());
                      for (AttachmentRecord attachmentRecord : list) {
                          attachmentRecord.setBusinessType ( "archive" );
                          attachmentRecord.setBusinessId ( userArchive.getArchiveId () );
                          attachmentRecord.setBusinessModule ( "ARC" );
                          attachmentRecord.setOperatorId ( userSession.getArchiveId () );
                      }
                      attachmentRecordDao.updateByPrimaryKeySelectiveList (list);
                  }
                }
            }
            if (CHANGSTATUS_DELAY.equals ( changeState )) {

                PreEmploymentChange existPreChange1 = isExistPreChange ( CHANGSTATUS_DELAY, preEmploymentChanges );
                if (existPreChange1 != null) {
                    BeanUtils.copyProperties ( statusChangeVo, existPreChange1 );
                    preEmploymentChangeDao.updateByPrimaryKeySelective ( existPreChange1 );
                } else {
                    //新增变更表
                    getPreEmploymentChange ( userSession.getArchiveId (), preEmploymentId, statusChangeVo );
                }
                //将预入职的入职时间重新设置
                preEmployment.setEmploymentState ( CHANGSTATUS_DELAY );
                preEmploymentDao.updateByPrimaryKey ( preEmployment );
            }
            if (CHANGSTATUS_GIVEUP.equals ( changeState )) {
                operatePro ( userSession, statusChangeVo, preEmployment, preEmploymentChanges, preEmploymentId, CHANGSTATUS_GIVEUP );
            }
            if (CHANGSTATUS_BLACKLIST.equals ( changeState )) {
                operatePro ( userSession, statusChangeVo, preEmployment, preEmploymentChanges, preEmploymentId, CHANGSTATUS_BLACKLIST );
                //加入黑名单
                Blacklist blacklist = new Blacklist ();
                blacklist.setBlockReason ( statusChangeVo.getAbandonReason () );
                blacklist.setDataSource ( PRE_DATASOURSE );
                BeanUtils.copyProperties ( preEmployment, blacklist );
                blacklist.setOperatorId ( userSession.getArchiveId () );
                blacklistDao.insertSelective ( blacklist );
            }
        }
    }

    private void operatePro(UserSession userSession, StatusChangeVo statusChangeVo, PreEmployment preEmployment, List < PreEmploymentChange > preEmploymentChanges, Integer preEmploymentId, String changstatusBlacklist) {
        PreEmploymentChange existPreChange = isExistPreChange ( changstatusBlacklist, preEmploymentChanges );
        if (existPreChange != null) {
            BeanUtils.copyProperties ( statusChangeVo, existPreChange );
            preEmploymentChangeDao.updateByPrimaryKey ( existPreChange );
        } else {
            //新增变更表
            getPreEmploymentChange ( userSession.getArchiveId (), preEmploymentId, statusChangeVo );
        }
        preEmployment.setEmploymentState ( changstatusBlacklist );
        preEmploymentDao.updateByPrimaryKey ( preEmployment );
    }

    private PreEmploymentChange isExistPreChange(String status, List < PreEmploymentChange > list) {
        for (PreEmploymentChange preEmploymentChange : list) {
            if (status.equals ( preEmploymentChange.getChangeState () )) {
                return preEmploymentChange;
            }
        }
        return null;
    }

    private void getPreEmploymentChange(Integer archiveId, Integer preEmploymentId, StatusChangeVo statusChangeVo) {
        PreEmploymentChange preEmploymentChange = new PreEmploymentChange ();
        BeanUtils.copyProperties ( statusChangeVo, preEmploymentChange );
        preEmploymentChange.setEmploymentId ( preEmploymentId );
        preEmploymentChange.setOperatorId ( archiveId );
        preEmploymentChangeDao.insertSelective ( preEmploymentChange );
    }

    @Override
    public void insertPreEmployment(PreEmploymentVo preEmploymentVo, UserSession userSession)  {
       List<Integer> integer = preEmploymentDao.selectIdByNumber ( preEmploymentVo.getPhone (), userSession.getCompanyId () );
        if (CollectionUtils.isNotEmpty ( integer )) {
            ExceptionCast.cast ( CommonCode.PHONE_ALREADY_EXIST );
        }
        List < Blacklist > blacklistList = blacklistDao.selectByPage ( userSession.getCompanyId () );
        boolean bool = blacklistList.stream ().anyMatch ( a -> StringUtils.isNotBlank ( preEmploymentVo.getPhone () ) && preEmploymentVo.getPhone ().equals ( a.getPhone () ) ||
                StringUtils.isNotBlank ( preEmploymentVo.getIdNumber () ) && preEmploymentVo.getIdNumber ().equals ( a.getIdNumber () ) );
        if (bool) {
            CompanyInfo companyInfo = companyInfoDao.selectByPrimaryKey ( userSession.getCompanyId () );
            CommonCode isExistBlacklistCommonCode = CommonCode.IS_EXIST_BLACKLIST;
            SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy年MM月dd日" );
            String reason="";
            if(null!=blacklistList.get(0).getBlockReason()){
                reason=blacklistList.get(0).getBlockReason();
            }
            String msg = blacklistList.get(0).getUserName() + "曾于" + sdf.format(blacklistList.get(0).getBlockTime()) + "被" + companyInfo.getCompanyName() + "因[ " + reason + " ]原因列入黑名单，不允许入职/投递简历，请联系该公司处理!";
            isExistBlacklistCommonCode.setMessage ( msg );
            ExceptionCast.cast ( isExistBlacklistCommonCode );
        }
        PreEmployment preEmployment = new PreEmployment ();
        BeanUtils.copyProperties ( preEmploymentVo, preEmployment );
        preEmployment.setEmploymentState ( "未入职" );
        preEmployment.setEmploymentRegister ( "未发送" );
        preEmployment.setDataSource ( "手工录入" );
        preEmployment.setCompanyId ( userSession.getCompanyId () );
        preEmployment.setOperatorId ( userSession.getArchiveId () );
        preEmployment.setCompanyId ( userSession.getCompanyId () );
        preEmployment.setOperatorId ( userSession.getArchiveId () );
        preEmployment.setIsDelete ( ( short ) 0 );
        preEmploymentDao.insert ( preEmployment );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePreEmployment(List < Integer > list){
        preEmploymentDao.deletePreEmploymentList ( list );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePreEmployment(PreEmploymentVo preEmploymentVo, UserSession userSession) {
        PreEmployment preEmployment = new PreEmployment ();
        BeanUtils.copyProperties ( preEmploymentVo, preEmployment );
        preEmployment.setCompanyId ( userSession.getCompanyId () );
        preEmployment.setOperatorId ( userSession.getArchiveId () );
        preEmployment.setIsDelete ( ( short ) 0 );
        preEmploymentDao.updateByPrimaryKey ( preEmployment );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePreEmploymentField(Map < Integer, String > map) {
        customTableFieldDao.updatePreEmploymentField ( map );
    }

    @Override
    public PageResult < PreEmploymentVo > selectPreEmployment(UserSession userSession, Integer currentPage, Integer pageSize) {
        //通过部门找到预入职Vo
        PageHelper.startPage ( currentPage, pageSize );
        List < PreEmploymentVo > preEmploymentList = preEmploymentDao.selectPreEmploymentVo ( userSession.getCompanyId () );
        List < Integer > integers = new ArrayList <> ();

        for (PreEmploymentVo preEmploymentVo : preEmploymentList) {
            integers.add ( preEmploymentVo.getEmploymentId () );
        }
        //通过预入职id找到更改记录
        if(CollectionUtils.isNotEmpty ( integers )){
            List < PreEmploymentChange > preEmploymentChanges = preEmploymentChangeDao.selectByPreIdList ( integers );
            //通过匹配设值
            for (PreEmploymentVo preEmploymentVo : preEmploymentList) {
                for (PreEmploymentChange preEmploymentChange : preEmploymentChanges) {
                    if (preEmploymentChange.getEmploymentId ().equals ( preEmploymentVo.getEmploymentId () )) {
                        if (CHANGSTATUS_DELAY.equals ( preEmploymentChange.getChangeState () )) {
                            preEmploymentVo.setDelayDate ( preEmploymentChange.getDelayDate () );
                            preEmploymentVo.setDelayReson ( preEmploymentChange.getChangeRemark () );
                        }
                        if (CHANGSTATUS_GIVEUP.equals ( preEmploymentChange.getChangeState () )) {
                            preEmploymentVo.setAbandonReason ( preEmploymentChange.getAbandonReason () );
                        }
                        if (CHANGSTATUS_BLACKLIST.equals ( preEmploymentChange.getChangeState () )) {
                            preEmploymentVo.setBlockReson ( preEmploymentChange.getChangeRemark () );
                        }
                    }
                }
            }
        }
        return new PageResult <> ( preEmploymentList );
    }

    @Override
    public Map < String, String > selectPreEmploymentField(UserSession userSession) {
        Map < String, String > map = new HashMap <> ();
        List < CustomArchiveField > list = customTableFieldDao.selectFieldNameByTableName ( userSession.getCompanyId (), PRE_EMPLOYMENT );
        for (CustomArchiveField customArchiveField : list) {
            map.put ( customArchiveField.getFieldCode (), customArchiveField.getFieldName () );
        }
        return map;
    }

    @Override
    public PreEmployment selectPreEmploymentSingle(Integer employeeId) {
        return preEmploymentDao.selectByPrimaryKey ( employeeId );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmEmployment(List < Integer > list, UserSession userSession){
        StatusChangeVo statusChangeVo = new StatusChangeVo ();
        statusChangeVo.setPreEmploymentList ( list );
        statusChangeVo.setAbandonReason ( "" );
        statusChangeVo.setChangeState ( CHANGSTATUS_READY );
        insertStatusChange ( userSession, statusChangeVo );
    }
    private void checkEmployeeNumber(UserSession userSession, UserArchive userArchive)  {
        String empNumber = employeeNumberRuleService.createEmpNumber ( userSession.getCompanyId () );
        if(null==userArchive.getEmployeeNumber ()||"".equals (userArchive.getEmployeeNumber ()  )) {
            userArchive.setEmployeeNumber ( empNumber );
        }else{
            List < Integer > employnumberList = userArchiveDao.selectEmployNumberByCompanyId ( userSession.getCompanyId (), userArchive.getEmployeeNumber () );
            if (CollectionUtils.isEmpty ( employnumberList ) || (employnumberList.size () == 1 && employnumberList.get ( 0 ).equals ( userArchive.getArchiveId () ))) {
                userArchive.setEmployeeNumber ( userArchive.getEmployeeNumber () );
            } else {
                ExceptionCast.cast ( CommonCode.EMPLOYEENUMBER_IS_EXIST );
            }
        }
    }

    public static void main(String[] args) {
        PreEmployment preEmployment=new PreEmployment ();
        preEmployment.setIdNumber ( "11112354646141" );
        Blacklist blacklist = new Blacklist ();
        List < Blacklist > blacklists = new ArrayList <> ();
        blacklist.setIdNumber ( "11112354646141"  );
        blacklists.add ( blacklist );
        if (blacklists.stream().anyMatch(a -> ( null != preEmployment.getIdNumber()&&preEmployment.getIdNumber().equals(a.getIdNumber())))){
            System.out.println ("方法有效");
        }
    }
}
