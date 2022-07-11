package com.jie.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *  全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class}) // 捕获带有这些注解的异常
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 添加账户名相同异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //拦截这类异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //提示用户这个用户名已存在
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");

    }

    /**
     * 菜品和套餐异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class) //拦截这类异常
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());

    }

}
