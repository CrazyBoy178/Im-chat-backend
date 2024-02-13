package com.chat.imbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.imbackend.entity.FriendShip;
import com.chat.imbackend.entity.Friends;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface FriendShipMapper extends BaseMapper<FriendShip> {

    @Insert("INSERT INTO tbl_friendship (userid, friendid) VALUES (#{userid}, #{friendid}) ")
    void addFriendShip(@Param("userid") String id, @Param("friendid") String friendid);

    @Select("select friendid from v_friendship where userid=#{userid}")
    List<FriendShip> getFriendShipsByUserId(@Param("userid") String userId);

    @Select("SELECT friendid FROM v_friendship WHERE userid=#{userid} LIMIT 10 offset #{page}")
    List<FriendShip> getFriendShipsPage(@Param("userid") String userId, @Param("page") int page);




    @Select("select COUNT(*) from v_friendship where userid=#{userid}")
    int getFriendShipCount(@Param("userid") String user);

    @Select("select * from tbl_friendship where userid=#{userId} and friendid=#{friendid}")
    FriendShip getFriendShip(@Param("userId") String userId,@Param("friendid") String friendId);

    Set<String> getFriendIds(String userId);
    @Delete("delete from tbl_friendship where userid=#{userid} and friendid=#{friendid}")
    void removeFriendShip(@Param("userid") String userid, @Param("friendid") String friendid);
}
