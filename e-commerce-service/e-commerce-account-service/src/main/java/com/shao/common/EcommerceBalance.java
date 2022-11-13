package com.shao.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <h1>用户账户余额表实体类定义</h1>
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ecommerce_balance")
public class EcommerceBalance {

    /** 自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户 id */
    @TableField("user_id")
    private Long userId;

    /** 账户余额 */
    @TableField("balance")
    private Long balance;

    /** 创建时间 */

    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
