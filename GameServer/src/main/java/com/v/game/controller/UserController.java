package com.v.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.v.game.common.R;
import com.v.game.entity.User;
import com.v.game.entity.Vip;
import com.v.game.service.UserService;
import com.v.game.service.VipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关请求")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    private R<User> register(@RequestBody User user) {
        //查询用户名是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, user.getName());
        User userDataInDb = userService.getOne(queryWrapper);
        if (userDataInDb != null) {
            return R.error("注册失败，此用户名已存在");
        }

        user.setCreateTime(LocalDateTime.now());

        //保存用户数据
        userService.save(user);

        return R.success(user);
    }

    /**
     * 用户登录
     *
     * @RequestBody 用来接收请求协议中的body-json数据
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    private R<User> login(HttpServletRequest request, @RequestBody User user) {
        String password = user.getPassword();

        //查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, user.getName());
        User userData = userService.getOne(queryWrapper);
        if (userData == null) {
            return R.error("登录失败，没有此用户");
        }

        //密码是否正确
        if (!userData.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }

        //登录成功，将员工id存入Session并返回登录成功结果
        //request.getSession().setAttribute("user", user.getId());

        //登录成功，将员工id缓存到redis中
        redisTemplate.opsForValue().set("user", userData.getId());

        return R.success(userData);
    }

    /**
     * 用户退出登录
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    private R<String> logout(HttpServletRequest request) {
        //request.getSession().removeAttribute("user");
        //从redis中移除key
        redisTemplate.delete("user");

        return R.success("退出成功");
    }

    @GetMapping("/search")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true),
            @ApiImplicitParam(name = "name", value = "用户名", required = false)
    })
    private R<Page> search(Integer page, Integer pageSize, String name)
    {
        //构造分页构造器
        Page<User> pageInfo = new Page<User>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        boolean nameNotEmpty = name != null && !name.isEmpty();
        queryWrapper.like(nameNotEmpty,User::getName,name);
        //添加排序条件
        //queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        userService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * @PathVariable映射url绑定的占位符
     */
    @ApiOperation("根据id查找用户")
    @GetMapping("/get/{id}")
    public R<User> getById(@PathVariable Long id)
    {
        User user = userService.getById(id);
        return R.success(user);
    }
}
