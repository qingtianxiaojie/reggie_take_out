package com.jie.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jie.reggie.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
