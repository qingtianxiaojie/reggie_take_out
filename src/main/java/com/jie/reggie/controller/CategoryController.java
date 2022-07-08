package com.jie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.reggie.common.R;
import com.jie.reggie.domain.Category;
import com.jie.reggie.domain.Employee;
import com.jie.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品和套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 菜品信息分页查询
     * @param page 页数
     * @param pageSize 每页查询个数
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //构造分页查询器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件 根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询 传入分页数据
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids){
        log.info("id:{}",ids);
        categoryService.removeById(ids);
        return R.success("分类信息删除成功");
    }
}
