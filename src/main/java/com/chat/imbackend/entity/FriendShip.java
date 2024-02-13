package com.chat.imbackend.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.imbackend.util.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("tbl_friendship")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendShip {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotNull
    private String userid;
    @NotNull
    private String friendid;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatar;
}
