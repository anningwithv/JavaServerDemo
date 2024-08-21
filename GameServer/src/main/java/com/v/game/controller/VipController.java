package com.v.game.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.v.game.common.R;
import com.v.game.entity.User;
import com.v.game.entity.Vip;
import com.v.game.service.UserService;
import com.v.game.service.VipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhang-wei
 * @since 2024-08-19
 */
@RestController
@RequestMapping("/vip")
@Api(tags = "付费用户相关请求")
public class VipController {

    @Autowired
    private VipService vipService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String redisVipListKey = "vipList";

    @ApiOperation("获取所有付费用户")
    @Cacheable(value = "allVipCache", key = "#root.methodName")
    @GetMapping("/get")
    public R<List<Vip>> getAllVips()
    {
        //先从redis中查询数据，如果存在则直接返回
//        List<Vip> vip = (List<Vip>) redisTemplate.opsForValue().get(redisVipListKey);
//        if(vip != null)
//        {
//            return R.success(vip);
//        }

        List<Vip> list = vipService.list();

        //查询到的数据缓存到redis，方便后面直接使用
        //redisTemplate.opsForValue().set(redisVipListKey,list, 60, TimeUnit.MINUTES);

        return R.success(list);
    }

    /**
     * CachePut:将方法返回值放入缓存 value:缓存名称，key:缓存key(可以有多个key)
     */
    @ApiOperation("添加付费用户")
    @CachePut(value = "vipCache", key = "#result.data.id", condition = "#result.data.id != null")
    @PostMapping("/add")
    public R<Vip> AddVip(@RequestBody User user, @RequestParam int level)
    {
        //查询用户是否存在
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getName, user.getName());
        User userData = userService.getOne(userQueryWrapper);
        if (userData == null) {
            return R.error("添加vip失败，没有此用户");
        }

        //插入或更新vip数据
        Vip vip = new Vip();
        vip.setId(userData.getId());
        vip.setLevel(level);
        LambdaUpdateWrapper<Vip> vipUpdateWrapper = new LambdaUpdateWrapper<>();
        vipUpdateWrapper.eq(Vip::getId, user.getId());
        vipService.saveOrUpdate(vip, vipUpdateWrapper);

        //vip数据更新了，需要清除redis缓存
        //redisTemplate.delete(redisVipListKey);

        return R.success(vip);
    }
}

