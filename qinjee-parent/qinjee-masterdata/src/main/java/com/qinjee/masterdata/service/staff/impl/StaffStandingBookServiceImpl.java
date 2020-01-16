package com.qinjee.masterdata.service.staff.impl;

import com.github.pagehelper.PageHelper;
import com.qinjee.exception.ExceptionCast;
import com.qinjee.masterdata.dao.custom.CustomTableFieldDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.BlacklistDao;
import com.qinjee.masterdata.dao.staffdao.staffstandingbookdao.StandingBookDao;
import com.qinjee.masterdata.dao.staffdao.staffstandingbookdao.StandingBookFilterDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.UserArchiveDao;
import com.qinjee.masterdata.dao.staffdao.userarchivedao.UserArchivePostRelationDao;
import com.qinjee.masterdata.model.entity.Blacklist;
import com.qinjee.masterdata.model.entity.StandingBook;
import com.qinjee.masterdata.model.entity.StandingBookFilter;
import com.qinjee.masterdata.model.vo.custom.CustomFieldVO;
import com.qinjee.masterdata.model.vo.custom.CustomTableVO;
import com.qinjee.masterdata.model.vo.staff.*;
import com.qinjee.masterdata.service.custom.CustomTableFieldService;
import com.qinjee.masterdata.service.staff.IStaffStandingBookService;
import com.qinjee.masterdata.utils.SqlUtil;
import com.qinjee.model.request.UserSession;
import com.qinjee.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 */
@Service
public class StaffStandingBookServiceImpl implements IStaffStandingBookService {
    private static final Logger logger = LoggerFactory.getLogger(StaffStandingBookServiceImpl.class);
    private static final String ARCHIVE="ARC";
    private static final String TYPEDATE="date";
    private static final String TYPENUMBER="number";
    private static final String TYPETEXT="text";
    private static final String TYPECODE="code";
    private static final String DENGYU="等于";
    private static final String BUDENGYU="不等于";
    private static final String BAOHAN="包含";
    private static final String BUBAOHAN="不包含";
    private static final String AND="并且";
    private static final String OR="或者";

    @Autowired
    private StandingBookDao standingBookDao;
    @Autowired
    private StandingBookFilterDao standingBookFilterDao;
    @Autowired
    private BlacklistDao blacklistDao;
    @Autowired
    private UserArchiveDao userArchiveDao;
    @Autowired
    private CustomTableFieldDao customTableFieldDao;
    @Autowired
    private UserArchivePostRelationDao userArchivePostRelationDao;
    @Autowired
    private CustomTableFieldService customTableFieldService;

    @Override
    public void insertBlackList(List<BlackListVo> blackListVos, UserSession userSession) throws IllegalAccessException {
        for (BlackListVo blacklistVo : blackListVos) {
            checkBlackList(blacklistVo);
            Blacklist blacklist = new Blacklist();
            BeanUtils.copyProperties(blacklistVo, blacklist);
            blacklist.setOperatorId(userSession.getArchiveId());
            blacklist.setBlockTime(new Date());
            blacklist.setCompanyId(userSession.getCompanyId());
            blacklist.setDataSource(blacklistVo.getDataSource ());
            blacklistDao.insertSelective(blacklist);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBlackList(List<Integer> list)  {
        blacklistDao.deleteBlackList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBalckList(Blacklist blacklist) {
        blacklistDao.updateByPrimaryKeySelective(blacklist);
    }

    @Override
    public List<Blacklist> selectBalckList(UserSession userSession) {
       return  blacklistDao.selectByPage(userSession.getCompanyId ());

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStandingBook(Integer standingBookId) {
        //删除台账属性
        standingBookDao.deleteStandingBook(standingBookId);
        //删除对应筛选表
        standingBookFilterDao.deleteStandingBookFilter(standingBookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStandingBook(UserSession userSession, StandingBookInfoVo standingBookInfoVo) {
        StandingBook standingBook = new StandingBook();
        if (standingBookInfoVo.getStandingBookVo().getStandingBookId() == null || standingBookInfoVo.getStandingBookVo().getStandingBookId() == 0 ) {
            //说明是新增操作
            //新增台账属性
            BeanUtils.copyProperties(standingBookInfoVo.getStandingBookVo(), standingBook);
            standingBook.setArchiveId(userSession.getArchiveId());
            standingBook.setCompanyId(userSession.getCompanyId());
            standingBook.setCreatorId(userSession.getArchiveId());
            standingBook.setIsEnable ( ( short ) 1 );
            standingBookDao.insertSelective(standingBook);
            insertStandingFilter ( userSession, standingBookInfoVo, standingBook );
        }else {
            //说明是更新操作
            BeanUtils.copyProperties(standingBookInfoVo.getStandingBookVo(), standingBook);
            standingBook.setArchiveId(userSession.getArchiveId());
            standingBook.setCompanyId(userSession.getCompanyId());
            standingBook.setCreatorId(userSession.getArchiveId());
            standingBook.setIsEnable ( ( short ) 1 );
            standingBookDao.updateByPrimaryKeySelective(standingBook);
            //删除已有的筛选方案
            standingBookFilterDao.deleteStandingBookFilter ( standingBookInfoVo.getStandingBookVo ().getStandingBookId () );
            insertStandingFilter ( userSession, standingBookInfoVo, standingBook );
        }
    }

    private void insertStandingFilter(UserSession userSession, StandingBookInfoVo standingBookInfoVo, StandingBook standingBook) {
        if (!CollectionUtils.isEmpty ( standingBookInfoVo.getListVo () )) {
            //设置台账属性表的id给筛选表
            for (StandingBookFilterVo standingBookFilterVo : standingBookInfoVo.getListVo ()) {
                StandingBookFilter standingBookFilter = new StandingBookFilter ();
                BeanUtils.copyProperties ( standingBookFilterVo, standingBookFilter );
                standingBookFilter.setStandingBookId ( standingBook.getStandingBookId () );
                standingBookFilter.setOperatorId ( userSession.getArchiveId () );
                standingBookFilter.setSqlStr ( getWhereSql ( standingBookFilter, userSession.getCompanyId (), "ARC" ) );
                standingBookFilterDao.insertSelective ( standingBookFilter );
            }
        }
    }

    @Override
    public StandingBookInfo selectStandingBook(Integer id) {
        StandingBook standingBook = standingBookDao.selectByPrimaryKey(id);
        List < StandingBookFilterVo > standingBookFilterVos = standingBookFilterDao.selectByStandingBookId ( id );
        StandingBookInfo standingBookInfo= new StandingBookInfo();
        standingBookInfo.setList(standingBookFilterVos);
        standingBookInfo.setStandingBook(standingBook);
        return standingBookInfo;
    }
    @Override
    public List<StandingBook> selectMyStandingBook(UserSession userSession) {

        return standingBookDao.selectByAchiveId(userSession.getArchiveId());

    }

    @Override
    public List<StandingBook> selectMyStandingBookShare(UserSession userSession) {

        return standingBookDao.selectShare(userSession.getArchiveId (),userSession.getCompanyId());
    }



    @Override
    public List<UserArchiveVo> selectStaff(StandingBookReturnVo standingBookReturnVo, UserSession userSession) {
        StringBuffer stringBuffer=new StringBuffer();
        List<Integer> fieldIdList=new ArrayList <> (  );
        List<CustomFieldVO> fieldVoList=new ArrayList <> (  );
        stringBuffer.append(" where ");
        //在查询台账之前被筛选的数据
        //存储大数据表字段解析出的档案id
        //key是用来存是第几个筛选条件，value存档案id(可能多个)
        //没经过台账之前筛选的档案id
        //通过id查询档案
        //通过台账id找到台账筛选表，直接返回台账筛选表对象
        List<StandingBookFilterVo> filters = standingBookFilterDao.selectByStandingBookId(standingBookReturnVo.getStangdingBookId ());
        if(!CollectionUtils.isEmpty (filters)) {
            for (StandingBookFilterVo filter : filters) {
                stringBuffer.append ( filter.getSqlStr () );
            }
            for (StandingBookFilterVo filter : filters) {
                fieldIdList.add ( filter.getFieldId () );
            }
        }else{
            ExceptionCast.cast ( CommonCode.STANDINGBOOK_IS_EMPTY );
        }
        CustomTableVO customTableVO=new CustomTableVO ();
        customTableVO.setCompanyId ( userSession.getCompanyId () );
        customTableVO.setFuncCode ( "ARC" );
        List < CustomTableVO > customTableVOS = customTableFieldService.searchCustomTableListByCompanyIdAndFuncCode ( customTableVO );
        List < CustomFieldVO > list1 = customTableFieldDao.selectFieldByIdList ( fieldIdList,userSession.getCompanyId (),"ARC" );
        for (CustomFieldVO customFieldVO : list1) {
            if(customFieldVO.getIsSystemDefine ()==0){
                fieldVoList.add ( customFieldVO );
            }
        }
        String sql=getBaseSql ( userSession.getCompanyId (),fieldVoList,customTableVOS )+stringBuffer.toString();
        PageHelper.startPage ( standingBookReturnVo.getCurrentPage (),standingBookReturnVo.getPageSize () );
        //主职集合
        List<Integer> integerList= null;
        try {
            integerList = userArchiveDao.selectStaff(sql,standingBookReturnVo.getArchiveType (),
                    standingBookReturnVo.getOrgIdList (),"主职");
        } catch (Exception e) {
            ExceptionCast.cast ( CommonCode.SQL_MAY_MISTAKE );
        }
        List < UserArchiveVo > list = userArchiveDao.selectByPrimaryKeyList ( integerList );
        //兼职集合
        List<UserArchiveVo> list2=userArchiveDao.selectPartTimeArchive(integerList,userSession.getCompanyId ());
        if(standingBookReturnVo.getType ().contains ( "兼职" )&&!standingBookReturnVo.getType ().contains ( "主职" )){
            standingBookReturnVo.setTotal ( list2.size () );
           return list2;
        }
        if(!standingBookReturnVo.getType ().contains ( "兼职" )&&standingBookReturnVo.getType ().contains ( "主职" )){
            standingBookReturnVo.setTotal ( list.size () );
            return list;
         }
         if(standingBookReturnVo.getType ().contains ( "兼职" )&&standingBookReturnVo.getType ().contains ( "主职" )){
            list.addAll ( list2 );
             standingBookReturnVo.setTotal ( list.size () );
            return list;
         }
      return null;
    }

    @Override
    public void updateStandingBook(Integer standingBookId, String name,Short isShare) {
       standingBookDao.updateStandingBook ( standingBookId,name,isShare );
    }


    private String getBaseSql(Integer companyId,List<CustomFieldVO> fieldvos,List<CustomTableVO> tableVOS){
        String a="select distinct t.archive_id from( select t0.*,  ";
        if(CollectionUtils.isEmpty ( fieldvos )){
            int i = a.lastIndexOf ( "," );
            a=a.substring ( 0,i );
        }
        return a+ SqlUtil.getsql( companyId,  fieldvos,  tableVOS);
    }

    /**
     * @return String
     * 字段类型分四种：
     * 日期 DATE，数字 NUMBER，代码 CODE，文本 TEXT
     */
    private String getWhereSql(StandingBookFilter filter,Integer companyId,String funcCode){
        Integer fieldId = filter.getFieldId();
        String condition=null;
        //根据id获得字段类型
        CustomFieldVO customFieldVO=customTableFieldDao.selectFieldById (fieldId,companyId,funcCode);
        String textType = customFieldVO.getTextType ();
        Short isSystemDefine = customFieldVO.getIsSystemDefine ();
        String fieldCode = customFieldVO.getFieldCode ();
         String fieldName = customFieldVO.getFieldName ();
        if(textType !=null && isSystemDefine==0 ) {
            if (  TYPENUMBER.equals(textType)) {
                condition = "t."+fieldName + "" + filter.getOperateSymbol() + "" + filter.getFieldValue();
            }
            if(TYPEDATE.equals(TYPEDATE)){
                if("<".equals(filter.getOperateSymbol())){
                    condition = "t."+fieldName + "" + "<![CDATA[<]]>" + "" + "'"+filter.getFieldValue()+"'";
                }else {
                    condition = "t."+fieldName + "" + filter.getOperateSymbol() + ""+ "'" + filter.getFieldValue()+ "'";
                }
            }
            if (TYPETEXT.equals(textType)) {
                if (DENGYU.equals(filter.getOperateSymbol())) {
                    condition ="t."+fieldName + " = " + "'"+ filter.getFieldValue()+ "'";
                }
                if (BUDENGYU.equals(filter.getOperateSymbol())) {
                    condition ="t."+fieldName + " != " + "'"+ filter.getFieldValue()+ "'";
                }
                if (BAOHAN.equals(filter.getOperateSymbol())) {
                    condition = "t."+fieldName + " like "+"'%" + filter.getFieldValue() + "%' ";
                }

                if (BUBAOHAN.equals(filter.getOperateSymbol())) {
                    condition = "t."+fieldName + " not like "+"'%" + filter.getFieldValue() + "%' ";
                }
            }
            if (TYPECODE.equals(textType)) {
                if (BAOHAN.equals(filter.getOperateSymbol())) {
                    condition ="t."+fieldName + " = " + "'"+ filter.getFieldValue()+ "'";
                }

                if (BUBAOHAN.equals(filter.getOperateSymbol())) {
                    condition ="t."+fieldName + " != " + "'"+ filter.getFieldValue()+ "'";
                }
            }
        }
        if(textType !=null && isSystemDefine==1 ) {
            if ( TYPENUMBER.equals(textType)) {
                condition = fieldCode + "" + filter.getOperateSymbol() + "" + filter.getFieldValue();
            }
            if(TYPEDATE.equals(TYPEDATE)){
                if("<".equals(filter.getOperateSymbol())){
                    condition = "t."+fieldCode + "" + "<![CDATA[<]]>" + "" + "'"+ filter.getFieldValue()+ "'";
                }else {
                    condition = "t."+fieldCode + "" + filter.getOperateSymbol() + "" + "'"+ filter.getFieldValue()+ "'";
                }
            }
            if (TYPETEXT.equals(textType)) {
                if (DENGYU.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " = " + "'"+ filter.getFieldValue()+ "'";
                }
                if (BUDENGYU.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " != "+ "'" + filter.getFieldValue()+ "'";
                }
                if (BAOHAN.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " like "+"'%" + filter.getFieldValue() + "%' ";
                }

                if (BUBAOHAN.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " not like "+"'%" + filter.getFieldValue() + "%' ";
                }
            }
            if (TYPECODE.equals(textType)) {
                if (BAOHAN.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " = " + "'"+ filter.getFieldValue()+ "'";
                }

                if (BUBAOHAN.equals(filter.getOperateSymbol())) {
                    condition = fieldCode + " != "+ "'" + filter.getFieldValue()+ "'";
                }
            }
        }
            if (filter.getIsLeftBrackets()==1 ) {
               return "("+condition+getLinkSymbol(filter);
            }
            if (filter.getIsRightBrackets()==1) {
                return condition+")"+getLinkSymbol(filter);
            }
        return condition+getLinkSymbol(filter)+"\t";
    }

    private String getLinkSymbol(StandingBookFilter filter){
        if(AND.equals(filter.getLinkSymbol())){
            return " AND ";
        }
        if(OR.equals(filter.getLinkSymbol())){
            return " OR ";
        }
        return "";
    }

    private void checkBlackList (BlackListVo blacklistVo) throws IllegalAccessException {
        Boolean flag1=false;
        Boolean flag2=false;
        Class  aClass = blacklistVo.getClass ();
        Field[] declaredFields = aClass.getDeclaredFields ();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if("phone".equals ( declaredField.getName () )){
                String phone = (String)declaredField.get ( blacklistVo );
                if(StringUtils.isNotBlank(phone)){
                    flag1=true;
                }
            }
            if("idNumber".equals ( declaredField.getName () )){
                String idNumber = (String)declaredField.get ( blacklistVo );
                if(StringUtils.isNotBlank(idNumber)){
                    flag2=true;
                }
            }
        }
        if(!(flag2||flag1)){
            ExceptionCast.cast ( CommonCode.CANNOT_TWO_NULL);
        }
    }

}
