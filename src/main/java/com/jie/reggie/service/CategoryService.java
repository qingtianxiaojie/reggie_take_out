package com.jie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
