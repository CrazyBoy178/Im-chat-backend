package com.chat.imbackend.mapper;

import com.chat.imbackend.entity.Friends;
import com.chat.imbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SocketMapper {
    @Select("select * from tbl_users where uid= #{uid}")
    User getUserByUid(@Param("uid") String uid);

    @Select("SELECT friendid as uid ,nickname ,avatar FROM v_friendship WHERE userid=#{userid}")
    List<Friends> getFriendsByUserId(@Param("userid") String user);


}
