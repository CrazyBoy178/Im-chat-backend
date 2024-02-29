package com.chat.imbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("tbl_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfo {

    @TableId(type = IdType.AUTO)
    private int id;

    private String groupId;

    private String groupName;

    private String groupOwner;

    private String groupMember;
}
