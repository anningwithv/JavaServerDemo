package com.v.game.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@ApiModel("付费用户")
public class Vip implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户Id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("付费等级")
    private Integer level;


}
