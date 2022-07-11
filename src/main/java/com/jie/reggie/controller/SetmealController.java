package com.jie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.reggie.common.R;
import com.jie.reggie.domain.Category;
import com.jie.reggie.domain.Dish;
import com.jie.reggie.domain.Setmeal;
import com.jie.reggie.domain.SetmealDish;
import com.jie.reggie.dto.DishDto;
import com.jie.reggie.dto.SetmealDto;
import com.jie.reggie.service.CategoryService;
import com.jie.reggie.service.SetmealDishService;
import com.jie.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐信息分页查询
     * @param page 页数
     * @param pageSize 每页展示数目
     * @param name 查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页查询器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（模糊查询）:名字不为空时添加此条件
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件 根据更新时间降序进行排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询 传入分页数据
        setmealService.page(pageInfo, queryWrapper);
        //查询表格中套餐分类尚未展示，用SetmealDto展示菜品分类
        //对象拷贝(排除的records，因为后面泛型由Setmeal改成SetmealDto)
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        //查询分类id-》通过分类id查询分类名字-》展示菜品分类名字
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> { //stream流处理records
            SetmealDto setmealDto = new SetmealDto();//存贮数据
            BeanUtils.copyProperties(item,setmealDto);//对象拷贝
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//分类对象
            if (category != null){
                String categoryName = category.getName();//分类名字
                setmealDto.setCategoryName(categoryName);//赋值名字
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids 返回的id值或id数组
     * @return
     */
    @DeleteMapping
    public R<String> delete(List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

}
