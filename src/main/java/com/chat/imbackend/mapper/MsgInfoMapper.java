package com.chat.imbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.imbackend.entity.MsgInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MsgInfoMapper  extends BaseMapper<MsgInfo> {


    @Select("select * from tbl_msg_info where sendid = #{sendid} and receiveid = #{receiveid}")
    List<MsgInfo> getHistoryMessages(@Param("sendid") String sendid,@Param("receiveid") String receiveid);

    @Select("select * from tbl_msg_info where receiveid =#{receiveid}")
    List<MsgInfo> getGroupHistoryMessages(@Param("receiveid") String receiveid);
}
