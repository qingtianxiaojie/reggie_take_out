package com.jie.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jie.reggie.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
