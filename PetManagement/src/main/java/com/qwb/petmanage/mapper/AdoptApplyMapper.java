
package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.entity.AdoptApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdoptApplyMapper extends BaseMapper<AdoptApply> {

    List<AdoptApply> selectApplyPage(Page<AdoptApply> page,
                                     @Param("auditStatus") String auditStatus,
                                     @Param("realName") String realName);
}
