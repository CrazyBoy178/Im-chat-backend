package com.chat.imbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@TableName("tbl_msg_info")
public class MsgInfo {
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 消息发送者id
     */
    private String sendid;

    /**
     * 消息发送者名称
     */
    private String receiveid;

    /**
     * 消息内容
     */
    private String content;

    private LocalDateTime timestamp;

    private long currenttimestamp;




}