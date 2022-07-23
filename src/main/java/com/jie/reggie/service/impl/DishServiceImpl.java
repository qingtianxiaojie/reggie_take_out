package com.jie.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.reggie.common.CustomException;
import com.jie.reggie.domain.Dish;
import com.jie.reggie.domain.DishFlavor;
import com.jie.reggie.dto.DishDto;
import com.jie.reggie.mapper.DishMapper;
import com.jie.reggie.service.DishFlavorService;
import com.jie.reggie.service.DishService;
import com.jie.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;
    /**
     * 新增菜品，同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional //开启事务控制(多张表处理)
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish
        this.save(dishDto);
        //菜品id
        Long dishId = dishDto.getId();
        //菜品口味的dishId存入dishFlavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId); // 遍历数组并赋值dishId
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味到dishFlavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    /**
     *  根据id查询菜品和口味
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息-dish
        Dish dish = this.getById(id);
        //信息拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味信息-dishFlavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     *     //更新菜品信息和口味信息
     * @param dishDto
     */
    @Override
    @Transactional//开启事务控制(多张表处理)
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本数据
        this.updateById(dishDto);
        //清理当前口味数据(dishDto.getId == dishId)
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交口味数据
        //菜品口味的dishId存入dishFlavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId()); // 遍历数组并赋值dishId
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品和口味信息
     * @param ids
     */
    @Override
    @Transactional//开启事务控制(多张表处理)
    public void deleteWithFlavor(List<Long> ids) {
        for (Long id : ids){
            //删除菜品
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId,id);
            Dish dish = dishService.getOne(queryWrapper);
            if (dish.getStatus() == 0){
                this.dishService.remove(queryWrapper);
            }else {
                throw new CustomException("删除菜品中有正在售卖的菜品，无法全部删除");
            }
            //删除口味信息表
            LambdaQueryWrapper<DishFlavor> queryWrapperFlavor = new LambdaQueryWrapper<>();
            queryWrapperFlavor.eq(DishFlavor::getDishId,id);
            this.dishFlavorService.remove(queryWrapperFlavor);
        }
    }
}
