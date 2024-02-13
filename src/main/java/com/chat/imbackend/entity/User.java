package com.chat.imbackend.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("tbl_users")
public class User {

    @TableId(type = IdType.AUTO)
    Integer id;

    String uid;

    String nickname;

    String password;

    String avatar = "src/assets/Resource/avatar/defaultAvatar.png";

    @TableField(fill = FieldFill.INSERT)
    Date jtime;


}
