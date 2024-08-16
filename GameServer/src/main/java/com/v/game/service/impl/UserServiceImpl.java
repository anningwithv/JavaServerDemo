package com.v.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.v.game.entity.User;
import com.v.game.mapper.UserMapper;
import com.v.game.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
