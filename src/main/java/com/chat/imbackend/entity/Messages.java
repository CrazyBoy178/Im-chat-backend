package com.chat.imbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messages {
    /**
     * 消息类型
     */
    private String type;
    /**
     * 发送好友昵称
     */
    private String sendUid;

    /**
     * 接收好友昵称
     */
    private String receiveUid;

    /**
     * 消息内容
     */
    private Object messages;
}
