
package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qwb.petmanage.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
