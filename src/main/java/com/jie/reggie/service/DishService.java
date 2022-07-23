package com.jie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.reggie.domain.Dish;
import com.jie.reggie.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish，dishFlavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品和口味
    public DishDto getByIdWithFlavor(Long id);
    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
    //删除菜品信息和口味信息
    public void deleteWithFlavor(List<Long> ids);
}
