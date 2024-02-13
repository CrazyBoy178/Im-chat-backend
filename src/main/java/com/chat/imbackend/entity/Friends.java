package com.chat.imbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    /**
     * 好友昵称
     */
    private String nickname;
    private String uid;
    private String avatar;

    private int status = 0;

    private int messageCount = 0;

    public void addMessageConut(){
        messageCount++;
    }

    public void resetMessageCount(){
        messageCount = 0;
    }
}
