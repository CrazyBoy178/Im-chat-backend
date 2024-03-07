package com.chat.imbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.imbackend.entity.GroupInfo;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<GroupInfo> {

    @Select("select * from tbl_group where group_id = #{groupId}")
    GroupInfo getGroupInfoByGroupId(@Param("groupId") String groupId);

    @Update("update tbl_group set group_member = #{groupMember} where group_id = #{groupId}")
    int updateGroupMember(@Param("groupMember") String groupMember, @Param("groupId") String groupId);

    @Select("select * from tbl_group where group_owner = #{groupOwner}")
    List<GroupInfo> getGroupInfoByGroupOwner(@Param("groupOwner") String groupOwner);

    @Select("select * from tbl_group where group_owner !=#{groupOwner}")
    List<GroupInfo> getGroupInfoWithoutGroupOwner(@Param("groupOwner") String groupOwner);

    @Delete("delete from tbl_group where group_owner = #{groupOwner} and group_id = #{groupId}")
    void removeGroup(@Param("groupOwner") String groupOwner,@Param("groupId") String groupId);

    @Select("select group_name from tbl_group where group_id=#{groupId}")
    String getGroupName(@Param("groupId") String groupId);




}
