package com.v.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.v.game.common.R;
import com.v.game.entity.User;
import com.v.game.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private R<User> register(@RequestBody User user)
    {
        //查询用户名是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, user.getName());
        User userDataInDb = userService.getOne(queryWrapper);
        if(userDataInDb != null)
        {
            return R.error("注册失败，此用户名已存在");
        }

        user.setCreateTime(LocalDateTime.now());

        //保存用户数据
        userService.save(user);

        return R.success(user);
    }
    /**
     * 用户登录
     * @RequestBody 用来接收请求协议中的body-json数据
     */
    @PostMapping("/login")
    private R<User> login(HttpServletRequest request, @RequestBody User user)
    {
        String password = user.getPassword();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, user.getName());
        User userData = userService.getOne(queryWrapper);

        if(userData == null)
        {
            return R.error("登录失败，没有此用户");
        }

        if(!userData.getPassword().equals(password))
        {
            return R.error("登录失败，密码错误");
        }

        return R.success(userData);
    }
}
