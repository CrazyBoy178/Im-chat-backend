package com.chat.imbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.imbackend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from tbl_users where uid= #{uid}")
    User getUserByUid(@Param("uid") String uid);



    @Update("UPDATE tbl_users SET nickname = #{nickname} WHERE uid = #{uid}")
    void modifyUserName(@Param("uid") String uid, @Param("nickname") String nickname);

    @Update("UPDATE tbl_users SET avatar = #{avatar} WHERE uid = #{uid}")
    void modifyAvatar(@Param("uid") String uid, @Param("avatar") String avatar);

    @Select("SELECT uid,nickname,avatar,jtime FROM tbl_users WHERE uid != #{userid} LIMIT 10 offset #{page}")
    List<User> getPage(@Param("userid") String userid ,@Param("page") int page);

    @Select("SELECT uid FROM tbl_users where uid != #{uid}")
    List<User> getAllUser(@Param("uid") String uid);



    @Select("select COUNT(*) from tbl_users")
    int getCount();


}
