package com.jie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.reggie.domain.Category;
import com.jie.reggie.domain.Employee;
import com.jie.reggie.mapper.CategoryMapper;
import com.jie.reggie.mapper.EmployeeMapper;
import com.jie.reggie.service.CategoryService;
import com.jie.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
}
