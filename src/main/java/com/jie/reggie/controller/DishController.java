package com.jie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.reggie.common.R;
import com.jie.reggie.domain.Category;
import com.jie.reggie.domain.Dish;
import com.jie.reggie.domain.DishFlavor;
import com.jie.reggie.domain.Employee;
import com.jie.reggie.dto.DishDto;
import com.jie.reggie.service.CategoryService;
import com.jie.reggie.service.DishFlavorService;
import com.jie.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto 返回的菜品数据
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**]
     * 菜品信息分页查询
     * @param page 页数
     * @param pageSize 每页展示数目
     * @param name 查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页查询器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（模糊查询）:名字不为空时添加此条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件 根据创建时间顺序进行排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询 传入分页数据
        dishService.page(pageInfo,queryWrapper);
        //查询表格中菜品分类尚未展示，用DishDto展示菜品分类
        //对象拷贝(排除的records，因为后面泛型由Dish改成DishDto)
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //查询分类id-》通过分类id查询分类名字-》展示菜品分类名字
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> { //stream流处理records
            DishDto dishDto = new DishDto();//存贮数据
            BeanUtils.copyProperties(item,dishDto);//对象拷贝
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();//分类名字
                dishDto.setCategoryName(categoryName);//赋值名字
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询修改菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 修改菜品
     * @param dishDto 返回的菜品数据
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

/*    *//**
     * 添加菜品到套餐中
     * @param dish
     * @return
     *//*
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //赋值
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
    /**
     * 添加菜品到套餐中
     * 添加了手机端展示口味的功能
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //赋值
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        //更新：添加手机端展示口味的功能
        List<DishDto> dishDtoList = list.stream().map((item) -> { //stream流处理list
            DishDto dishDto = new DishDto();//存贮数据
            BeanUtils.copyProperties(item,dishDto);//对象拷贝
//            Long categoryId = item.getCategoryId();//分类id
//            Category category = categoryService.getById(categoryId);
//            if (category != null){
//                String categoryName = category.getName();//分类名字
//                dishDto.setCategoryName(categoryName);//赋值名字
//            }
            Long dishId = item.getId();//当前菜品id
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);//存入口味
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
