package com.chat.imbackend.controller;

import com.chat.imbackend.entity.BCryptExample;
import com.chat.imbackend.entity.GroupInfo;
import com.chat.imbackend.entity.User;
import com.chat.imbackend.mapper.GroupMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.annotations.Param;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/group")
public class GroupController {

    @Autowired
    GroupMapper groupMapper;

    @GetMapping("/getGroup")
    private String getUsers(@RequestParam("group_id") String group_id ){

        GroupInfo exuser = groupMapper.getGroupInfoByGroupId(group_id);
        if(exuser == null){
            return null;
        }else{
            String group_number = exuser.getGroupMember();
            return group_number;
        }

    }

    @GetMapping("/add/{user_id}/{group_id}")
    private String addNewUser(@PathVariable("user_id") String user_id,@PathVariable("group_id") String group_id){

        GroupInfo exuser = groupMapper.getGroupInfoByGroupId(group_id);
        if(exuser.getGroupOwner().equals(user_id)){
            return "202";
        }


        String[] oldMember = exuser.getGroupMember().split(",");
        for(String member : oldMember){
            if(member.equals(user_id)){
                return "203";
            }
        }

        String newMembers = exuser.getGroupMember() + "," + user_id;

        int rowsAffected = groupMapper.updateGroupMember(newMembers, group_id);

        if (rowsAffected > 0) {
            System.out.println("更新成功，影响行数：" + rowsAffected);
            return "200";
        } else {
            System.out.println("更新失败，未找到匹配的记录。");
            return "201";
        }
    }


    @GetMapping("/getAllGroup/{user_id}/page/{page}")
    private List<GroupInfo> getAllGroup(@PathVariable("user_id") String user_id,@PathVariable("page") int page){
        return getGroup(user_id,page);
    }

    @GetMapping("/getAllGroup/{user_id}")
    private int getAllGroup(@PathVariable("user_id") String user_id){
        return getGroupLength(user_id);
    }

    private int getGroupLength(String user_id){
        List<GroupInfo> groups = new ArrayList<GroupInfo>();
        List<GroupInfo> user_owner_group = groupMapper.getGroupInfoByGroupOwner(user_id);
        List<GroupInfo> user_in_group = groupMapper.getGroupInfoWithoutGroupOwner(user_id);

        groups = user_owner_group;
        for (GroupInfo group :user_in_group){
            String[] groupMembers = group.getGroupMember().split(",");
            for(String member:groupMembers){
                if(member.equals(user_id)){
                    groups.add(group);
                    break;
                }
            }
        }
        return groups.size();
    }


    private List<GroupInfo> getGroup(String user_id,int page){
        int pageSize = 10; // 每页的大小
        int startIndex = (page - 1) * pageSize;
        int endIndex = startIndex + pageSize;

        List<GroupInfo> user_owner_group = groupMapper.getGroupInfoByGroupOwner(user_id);
        List<GroupInfo> user_in_group = groupMapper.getGroupInfoWithoutGroupOwner(user_id);

        System.out.println(user_in_group);
        List<GroupInfo> groups = new ArrayList<GroupInfo>(user_owner_group);
        for (GroupInfo group :user_in_group){
            String[] groupMembers = group.getGroupMember().split(",");
            System.out.println(Arrays.toString(groupMembers));
            for(String member:groupMembers){
                if(member.equals(user_id)){
                    groups.add(group);
                    break;
                }
            }
        }
        List<GroupInfo> pageGroups = new ArrayList<>();
        for (int i = startIndex; i < Math.min(endIndex, groups.size()); i++) {
            pageGroups.add(groups.get(i));
        }
        System.out.println(pageGroups);

        return pageGroups;
    }

    @GetMapping("/getUserGroups/{user_id}")
    List<GroupInfo> getAllUserGroup(@PathVariable("user_id") String user_id){
        List<GroupInfo> user_owner_group = groupMapper.getGroupInfoByGroupOwner(user_id);
        List<GroupInfo> user_in_group = groupMapper.getGroupInfoWithoutGroupOwner(user_id);

        System.out.println(user_in_group);
        List<GroupInfo> groups = new ArrayList<GroupInfo>(user_owner_group);
        for (GroupInfo group :user_in_group){
            String[] groupMembers = group.getGroupMember().split(",");
            System.out.println(Arrays.toString(groupMembers));
            for(String member:groupMembers){
                if(member.equals(user_id)){
                    groups.add(group);
                    break;
                }
            }
        }
        return groups;
    }

    @GetMapping("/delete/{user_id}/{group_id}")
    private String deleteGroup(@PathVariable("user_id") String user_id, @PathVariable("group_id") String group_id){
        groupMapper.removeGroup(user_id, group_id);
        return "200";
    }

    @GetMapping("/deletegroup/{user_id}/{group_id}")
    private String deleteGroupUser(@PathVariable("user_id") String user_id,@PathVariable("group_id") String group_id){
        GroupInfo groupInfo = groupMapper.getGroupInfoByGroupId(group_id);
        String[]members = groupInfo.getGroupMember().split(",");

        // 将数组转换为列表
        List<String> updatedMembers = new ArrayList<>(Arrays.asList(members));

        // 从列表中移除 user_id
        updatedMembers.remove(user_id);

        // 将列表转换回字符串
        String updatedMemberList = String.join(",", updatedMembers);
        int rowsAffected = groupMapper.updateGroupMember(updatedMemberList, group_id);

        if (rowsAffected > 0) {
            System.out.println("更新成功，影响行数：" + rowsAffected);
            return "200";
        } else {
            System.out.println("更新失败，未找到匹配的记录。");
            return "201";
        }
    }


    @GetMapping("/getGroupName/{groupId}")
    public String getGroupName(@PathVariable("groupId") String groupId){
        return groupMapper.getGroupName(groupId);
    }






    @PostMapping("/addgroup")
    public String addGroup(@RequestBody GroupInfo groupInfo) {
        System.out.println(groupInfo);
//         检查是否已存在相同用户名
        String groupId = generateRandomGroupUid();

        GroupInfo exuser = groupMapper.getGroupInfoByGroupId(groupId);

        while(exuser!=null) {
            groupId=generateRandomGroupUid();
            exuser = groupMapper.getGroupInfoByGroupId(groupId);
        }


        groupInfo.setGroupId(groupId);
        groupInfo.setGroupName(groupInfo.getGroupName());
        groupInfo.setGroupOwner(groupInfo.getGroupOwner());
        System.out.println(groupInfo.getGroupMember());
        groupInfo.setGroupMember(groupInfo.getGroupMember());

        // 执行注册
        int result = groupMapper.insert(groupInfo);

        if(result>0){
            return "200";
        }else{
            return "0";
        }
    }

    private String generateRandomGroupUid() {
        return RandomStringUtils.randomAlphanumeric(5);
    }



}
