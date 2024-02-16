package com.chat.imbackend.controller;


import com.chat.imbackend.entity.FriendShip;
import com.chat.imbackend.entity.User;
import com.chat.imbackend.mapper.FriendShipMapper;
import com.chat.imbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/friend")
public class FriendShipController extends HttpController<FriendShipMapper,FriendShip> {

    @Autowired
    private  FriendShipMapper friendshipMapper;
    @Autowired
    private UserMapper userMapper;

    private  RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/add")
    public String addFriend(@RequestBody FriendShip friendship) {
        String userId = friendship.getUserid();
        String friendId = friendship.getFriendid();
        System.out.println(userId+' '+friendId);
        User exuser = userMapper.getUserByUid(friendId);
        System.out.println(exuser);

        if(exuser !=null && !Objects.equals(userId, friendId)) {
            try {
                // 确保 userId 和 friendId 不为空
                if ( friendshipMapper.getFriendShip(userId,friendId) == null) {
                    addSingleFriend(userId, friendId);
                    addSingleFriend(friendId, userId);
                    return "200";
                } else {
                    return "203";
                }
            } catch (Exception e) {
                // 具体处理异常的逻辑，例如记录日志
                System.err.println("Error while processing addFriend request: " + e.getMessage());
                return "202";
            }
        }else{
            return "201";
        }
    }


    private void addSingleFriend(String userId, String friendId) {
        System.out.println("Adding friend to database: " + userId + " -> " + friendId);
        friendshipMapper.addFriendShip(userId, friendId);

//        // 更新Redis缓存
//        String userKey = "user:" + userId + ":friends";
//        redisTemplate.opsForSet().add(userKey, friendId);
//        System.out.println("Friend added successfully: " + userId + " -> " + friendId);
    }


    //获取好友信息
    @GetMapping("/find/{userid}")
    public Set<Object> getFriends(@PathVariable("userid") String userId) {
        Set<Object> friends = new HashSet<>();

        // 查询数据库获取朋友关系列表
        List<FriendShip> friendShips = friendshipMapper.getFriendShipsByUserId(userId);

        // 处理从数据库中查询的朋友关系列表
        for (FriendShip friendShip : friendShips) {
            // 这里可以根据需要将朋友关系对象转换为 Set<Object> 中的元素
            // 比如，可以将 friendShip 转换为 Map<String, String> 然后添加到 friends 中
            Map<String, String> friendData = new HashMap<>();
            friendData.put("friendId", friendShip.getFriendid());
            User user = userMapper.getUserByUid(friendShip.getFriendid());
            friendData.put("friendAvatar", user.getAvatar());
            friendData.put("friendnickname", user.getNickname());

            friends.add(friendData);
        }

        // 在此处理缓存逻辑

        return friends;
    }

    @GetMapping("/find/{userid}/page/{page}")
    public Set<Object> getFriendsPage(@PathVariable("userid") String userId,@PathVariable("page") int page) {
        Set<Object> friends = new HashSet<>();

        // 查询数据库获取朋友关系列表
        List<FriendShip> friendShips = friendshipMapper.getFriendShipsPage(userId,(page - 1) * 10);

        if(!friendShips.isEmpty()){
            for (FriendShip friendShip : friendShips) {
                // 这里可以根据需要将朋友关系对象转换为 Set<Object> 中的元素
                // 比如，可以将 friendShip 转换为 Map<String, String> 然后添加到 friends 中
                Map<String, String> friendData = new HashMap<>();
                friendData.put("friendId", friendShip.getFriendid());
                User user = userMapper.getUserByUid(friendShip.getFriendid());
                friendData.put("friendAvatar", user.getAvatar());
                friendData.put("friendnickname", user.getNickname());

                friends.add(friendData);
            }

        }
        return friends;
        // 处理从数据库中查询的朋友关系列表
    }

    @GetMapping("/count/{userid}")
    public int getFriendsPage(@PathVariable("userid") String userId) {
        return friendshipMapper.getFriendShipCount(userId);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeFriend(@RequestBody FriendShip friendship) {
        try {
            String userId = friendship.getUserid();
            String friendId = friendship.getFriendid();
            System.out.println("remove friend "+userId + "->" + friendId);
            removeSingleFriend(userId, friendId);
            removeSingleFriend(friendId, userId);
            return ResponseEntity.ok("Friend removed successfully.");
        } catch (Exception e) {
            // 处理异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove friend: " + e.getMessage());
        }
    }

    private void removeSingleFriend(String userId, String friendId) {
        // 在这里实现删除好友的逻辑
        friendshipMapper.removeFriendShip(userId, friendId);
    }

}
