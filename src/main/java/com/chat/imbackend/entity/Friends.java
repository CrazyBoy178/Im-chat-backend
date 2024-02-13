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
    
}
