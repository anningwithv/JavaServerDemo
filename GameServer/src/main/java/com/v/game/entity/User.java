package com.v.game.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 用户实体类
 */
@Data
public class User implements Serializable {
    private long id;
    private String name;
    private String password;
    private LocalDateTime createTime;
}
