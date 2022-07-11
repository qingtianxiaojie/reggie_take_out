package com.jie.reggie.dto;

import com.jie.reggie.domain.Setmeal;
import com.jie.reggie.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
