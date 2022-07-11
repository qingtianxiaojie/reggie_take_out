package com.jie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.reggie.domain.DishFlavor;
import com.jie.reggie.mapper.DishFlavorMapper;
import com.jie.reggie.service.DishFlavorService;
import com.jie.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
