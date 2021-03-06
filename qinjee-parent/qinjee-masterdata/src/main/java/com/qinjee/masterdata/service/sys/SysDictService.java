/**
 * 文件名：SysDictService
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/11/22
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.masterdata.service.sys;

import com.qinjee.masterdata.model.entity.SysDict;

import java.util.List;
import java.util.Map;

/**
 * @author 周赟
 * @date 2019/11/22
 */
public interface SysDictService {

    /**
     * 根据字典类型查询字典列表
     * @param dictType
     * @return
     */
    List<SysDict> searchSysDictListByDictType(String dictType);

    /**
     * 根据字典类型和字典CODE查询字典对象
     * @param dictType
     * @param dictCode
     * @return
     */
    SysDict searchSysDictByTypeAndCode(String dictType, String dictCode);

    String searchCodeByTypeAndValue(String dictType,String dictValue);

    Map<String, List<SysDict>> selectMoreDict(List<String> dictCodeList);

}
