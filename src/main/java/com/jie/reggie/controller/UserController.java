package com.jie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jie.reggie.common.R;
import com.jie.reggie.common.ValidateCodeUtils;
import com.jie.reggie.domain.User;
import com.jie.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码（用邮箱代替）
     * @param user 接收的手机号
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用邮箱发送验证码
            log.info("code={}",code);
            //将验证码保存到session
//            session.setAttribute(phone,code);
            //将生成的验证码缓存到Redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }
        return R.success("短信发送失败");
    }

    /**
     * 手机号登陆
     * @param map 接收的手机号和验证码
     * @param session 发送的验证码
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取提交的验证码
        String code = map.get("code").toString();
        //从session中获得保存的验证码
//        Object codeInSession = session.getAttribute(phone);
        //从Redis中获得保存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //进行验证码的比对
        if (codeInSession != null && codeInSession.equals(code)){
            //比对成功，登陆成功
            //判断手机号是否为新用户，如果是新用户自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登陆成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        //比对失败，登陆失败
        return R.error("验证码错误");
    }
}
