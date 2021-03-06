package com.qinjee.masterdata.service.staff.impl;

import com.alibaba.fastjson.JSON;
import com.qinjee.masterdata.dao.custom.TemplateCustomTableFieldDao;
import com.qinjee.masterdata.dao.staffdao.entryregistration.EntryRegistrationDao;
import com.qinjee.masterdata.dao.staffdao.preemploymentdao.PreEmploymentDao;
import com.qinjee.masterdata.model.Thread.SendSmsAndMail;
import com.qinjee.masterdata.model.entity.PreEmployment;
import com.qinjee.masterdata.model.vo.custom.CustomFieldVO;
import com.qinjee.masterdata.model.vo.custom.EntryRegistrationTableVO;
import com.qinjee.masterdata.model.vo.staff.AboutMobileMessage;
import com.qinjee.masterdata.model.vo.staff.PreRegistVo;
import com.qinjee.masterdata.model.vo.staff.entryregistration.EntryTableListWithValueVo;
import com.qinjee.masterdata.model.vo.staff.entryregistration.EntryTemplateValueVo;
import com.qinjee.masterdata.service.custom.CustomTableFieldService;
import com.qinjee.masterdata.service.custom.TemplateCustomTableFieldService;
import com.qinjee.masterdata.service.sms.SmsRecordService;
import com.qinjee.masterdata.service.staff.IPreTemplateService;
import com.qinjee.model.request.UserSession;
import com.qinjee.utils.AesUtils;
import com.qinjee.utils.FileUploadUtils;
import com.qinjee.utils.QRCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreTemplateServiceImpl implements IPreTemplateService {

    private static final int IMAGE_WIDTH = 512;
    private static final int IMAGE_HEIGHT = 512;

    @Autowired
    private TemplateCustomTableFieldService templateCustomTableFieldService;
    @Autowired
    private StaffCommonServiceImpl staffCommonService;
    @Autowired
    private PreEmploymentDao preEmploymentDao;
    @Autowired
    private SmsRecordService smsRecordService;
    @Autowired
    private EntryRegistrationDao entryRegistrationDao;
    @Autowired
    private CustomTableFieldService customTableFieldService;
    @Autowired
    private TemplateCustomTableFieldDao templateCustomTableFieldDao;


    //    private static final String CHANGSTATUS_READY = "已入职";
//    private static final String CHANGSTATUS_BLACKLIST = "黑名单";
//    private static final String CHANGSTATUS_GIVEUP = "放弃入职";
    private static final String AESENCRYPT = "QINJEE_EHR";

    @Override
    public String createBackGroundPhoto(MultipartFile file, UserSession userSession) throws Exception {
        File file1 = FileUploadUtils.multipartFileToFile ( file );
        return FileUploadUtils.uploadFile ( file1, FileUploadUtils.COMPANY_LOGO_DIRECTORY, file.getOriginalFilename () );
    }

    /**
     * 生成预入职二维码
     *
     * @param templateId
     * @param response
     */
    @Override
    public void createPreRegistQrcode(Integer templateId, HttpServletResponse response, UserSession userSession) throws Exception {
        //给相应添加头部信息，主要告诉浏览器返回的是图片流
        response.setHeader ( "Cache-Control", "no-store" );
        // 不设置缓存
        response.setHeader ( "Pragma", "no-cache" );
        response.setDateHeader ( "Expires", 0 );
        response.setContentType ( "image/png" );
        //TODO  前端登陆页面url由前端提供
        String s = AesUtils.aesEncrypt ( templateId + "@@" + userSession.getCompanyId (), AESENCRYPT );
        String baseUrl = "www.baidu.com" + "?" + s;
        //数据库查询logurl
        String url = entryRegistrationDao.searchLogurlByComanyIdAnadTemplateId ( templateId, userSession.getCompanyId () );
        QRCodeUtil.outputQRCodeImageByLogoUrl ( url, IMAGE_WIDTH, IMAGE_HEIGHT, baseUrl, response.getOutputStream () );
    }


    @Override
    public String toCompleteMessage(String phone, String s, String code) throws Exception {
        String s1 = AesUtils.aesDecode ( s, AESENCRYPT );
        String[] split = s1.split ( "@@" );
        boolean b = smsRecordService.checkPreLoginCodeByPhoneAndCode ( phone, code );
        if (b) {
            List<Integer> preId = preEmploymentDao.selectIdByNumber ( phone, Integer.parseInt ( split[1] ) );
            Map < String, Integer > map = new HashMap <> ();
            map.put ( "preId", preId.get(0) );
            map.put ( "templateId", Integer.parseInt ( split[0] ) );
            return JSON.toJSONString ( map );
        } else {
            return "验证不通过！";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commitHr(Integer preId, Integer templateId, Integer companyId) {
        //判断preId是否存在
        PreEmployment preEmployment1 = preEmploymentDao.selectByPrimaryKey(preId);
        if(preEmployment1!=null){
            //将状态更改为审核中，将预入职模板id更新
            preEmployment1.setEmploymentRegister("审核中");
            preEmployment1.setTemplateId(templateId);
            preEmploymentDao.updateByPrimaryKey(preEmployment1);
        }else {
            PreEmployment preEmployment = new PreEmployment();
            preEmployment.setEmploymentRegister("审核中");
            preEmployment.setTemplateId(templateId);
            preEmployment.setEmploymentId(preId);
            preEmploymentDao.insert(preEmployment);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendRegisterMessage(PreRegistVo preRegistVo, UserSession userSession) {
        Thread thread = new Thread ( new SendSmsAndMail ( preRegistVo, userSession ) );
        thread.run ();
        for (Integer integer : preRegistVo.getList ()) {
            PreEmployment preEmployment = preEmploymentDao.selectByPrimaryKey ( integer );
            preEmployment.setEmploymentRegister ( "已发送" );
            preEmploymentDao.updateByPrimaryKey ( preEmployment );
        }
    }


    @Override
    public List<Integer> selectPreIdByPhone(String phone, UserSession userSession) {
        return preEmploymentDao.selectIdByNumber ( phone, userSession.getCompanyId () );
    }


    @Override
    public AboutMobileMessage handlerCustomTableGroupFieldList(Integer companyId, Integer preId, Integer templateId)
            throws IllegalAccessException {
        AboutMobileMessage aboutMobileMessage=new AboutMobileMessage();
        Map < Integer, String > map = new HashMap <> ();
        List < Integer > integers = new ArrayList <> ();
        List < EntryTableListWithValueVo > entryTableListWithValueVos = new ArrayList <> ();
        //根据模板id获取模板数据
        List < EntryRegistrationTableVO > entryRegistrationTableVOList =
                templateCustomTableFieldService.searchCustomTableListByTemplateId ( templateId );
        //将对象属性进行copy
        for (EntryRegistrationTableVO entryRegistrationTableVO : entryRegistrationTableVOList) {
            EntryTableListWithValueVo entryTableListWithValueVo = new EntryTableListWithValueVo ();
            entryTableListWithValueVo.setSort ( entryRegistrationTableVO.getSort () );
            entryTableListWithValueVo.setTableId ( entryRegistrationTableVO.getTableId () );
            entryTableListWithValueVo.setTableName ( entryRegistrationTableVO.getTableName () );
            entryTableListWithValueVo.setIsSystemDefine ( entryRegistrationTableVO.getIsSystemDefine () );
            entryTableListWithValueVos.add ( entryTableListWithValueVo );
        }
        //筛选出tableId
        for (EntryRegistrationTableVO entryRegistrationTableVO : entryRegistrationTableVOList) {
            integers.add ( entryRegistrationTableVO.getTableId () );
        }
        //找到对应的值
        Integer bigDataId=0;
        for (EntryTableListWithValueVo entryTableListWithValueVo : entryTableListWithValueVos) {
            for (Integer integer : integers) {
                if (entryTableListWithValueVo.getTableId ().equals ( integer )) {
                    List < EntryTemplateValueVo > entryTemplateValueVos = new ArrayList <> ();
                    List < Map < Integer, String > > mapList = staffCommonService.selectValue ( integer, preId );
                    //属性对象组装
                    for (int i = 0; i < mapList.size (); i++) {
                        for (Map.Entry < Integer, String > integerStringEntry : mapList.get(i).entrySet ()) {
                            Integer key = integerStringEntry.getKey();
                            if(!key.equals(-1)) {
                                map.put(key, integerStringEntry.getValue());
                            }else {
                                if (StringUtils.isNotBlank(integerStringEntry.getValue())) {
                                    bigDataId = Integer.parseInt(integerStringEntry.getValue());
                                }
                            }
                        }
                        //通过tableid寻找对应的值
                        //给list赋值
                        EntryTemplateValueVo entryTemplateValueVo=new EntryTemplateValueVo () ;
                        List < CustomFieldVO > customFieldList = templateCustomTableFieldDao.searchCustomFieldListByTemplateIdAndTableId ( integer, templateId );
                        customTableFieldService.handlerCustomTableGroupFieldList ( customFieldList, map );
                        entryTemplateValueVo.setBigDataId ( bigDataId );
                        entryTemplateValueVo.setList ( customFieldList );
                        entryTemplateValueVos.add ( entryTemplateValueVo );
                    }
                    entryTableListWithValueVo.setEntryTemplateValueVos ( entryTemplateValueVos );
                }
            }
        }
        //通过preId与companyId查询判断是否已经提交hr
        Boolean isCommit=false;
        PreEmployment preEmployment=preEmploymentDao.selectemploymentRegisterByPreId(preId,companyId);
        if(preEmployment!=null){
            isCommit=true;
        }
        aboutMobileMessage.setIsCommitHr(isCommit);
        aboutMobileMessage.setList(entryTableListWithValueVos);
        return aboutMobileMessage;
    }
}

