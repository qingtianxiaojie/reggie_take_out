package com.jie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.reggie.common.R;
import com.jie.reggie.domain.Employee;
import com.jie.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 方便获取员工信息
     * @param employee 传入为JSON形式：RequestBody
     * @return 返回登录的信息
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        //将页面提交的密码进行MD5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据Username查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //如果没有查询==》登陆失败
        if (emp == null){
            return R.error("登陆失败");
        }
        //密码错误==》登陆失败
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }
        //查看员工状态；0==》禁用
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //登陆成功，id存入session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param request 方便删除员工信息
     * @return 返回退出信息
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //删除session中的账户信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee 前端返回的员工信息
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工：{}",employee);
        //设置初始密码123456并md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now()); //创建时间
//        employee.setUpdateTime(LocalDateTime.now());//更新时间
        //获得当前登录用户的id
//        Long empId =(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId); //创始人
//        employee.setUpdateUser(empId); //更新人
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page 页数
     * @param pageSize 每页查询个数
     * @param name 查询名字
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页查询器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件（模糊查询）:一：条件不为空，二：
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件 根据创建时间顺序进行排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询 传入分页数据
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id启用或禁用员工信息
     * @param employee 启用或禁用的员工信息
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());//更新修改时间
//        employee.setUpdateUser(empId);//更新修改人
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息（编辑员工信息）
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> GetById(@PathVariable Long id){
        log.info("根据id查询编辑员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}








































