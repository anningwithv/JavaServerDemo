package com.v.game.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author zhang-wei
 * @since 2024-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户Id")
    private Long id;
    @ApiModelProperty("用户名字")
    private String name;
    @ApiModelProperty("用户密码")
    private String password;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
