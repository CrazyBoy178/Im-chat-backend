package com.chat.imbackend.controller;


import com.alibaba.fastjson.JSON;
import com.chat.imbackend.entity.Friends;
import com.chat.imbackend.entity.Messages;
import com.chat.imbackend.entity.MsgInfo;
import com.chat.imbackend.entity.User;
import com.chat.imbackend.mapper.MsgInfoMapper;
import com.chat.imbackend.mapper.SocketMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@CrossOrigin
@ServerEndpoint(value = "/websocket/{uid}")
public class WebSocketController  {


    private Session session;

    private static List<String> onlineFriends = new ArrayList<>();


    /**
     * 好友列表
     */
    public List<Friends> friendsList = new ArrayList<>();

    /**
     * 定义并发HashMap存储好友WebSocket集合
     */
    public static ConcurrentHashMap<String, WebSocketController> webSocketSession = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, List<Friends>> friendsListMap = new ConcurrentHashMap<>();


    private static ApplicationContext applicationContext;

    private  SocketMapper socketMapper;

    private MsgInfoMapper msgInfoMapper;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketController.applicationContext = applicationContext;
    }


    @OnOpen
    public void onOpen(@PathParam(value = "uid") String uid, Session session) {
        // 设置session
        this.session = session;
        // Put添加当前类
        webSocketSession.put(uid, this);
        friendsListMap.put(uid, new ArrayList<>()); // 初始化好友列表
        onlineFriends.add(uid);
        try {
            System.out.println(uid);
            socketMapper = applicationContext.getBean(SocketMapper.class);

            friendsList = socketMapper.getFriendsByUserId(uid);

            System.out.println(friendsList);

            friendsListMap.put(uid, friendsList); // 初始化好友列表
            System.out.println(friendsListMap);

            // 通知更新好友信息列表
            updateFriendInformationList();

//            msgInfoMapper = applicationContext.getBean(MsgInfoMapper.class);
//            List<MsgInfo> historyMessages = msgInfoMapper.getHistoryMessages(uid); // 这里假设有一个方法用于获取历史消息
//
//            // 将历史消息发送给客户端
//            for (MsgInfo msg : historyMessages) {
//                sendP2PMessage(uid, msg.getContent());
//            }




            log.info("【WebSocket消息】有新的连接[{}], 连接总数:{}", uid, webSocketSession.size());

        }catch(Exception e){
            e.printStackTrace();
            // 或者使用日志记录器记录错误信息
            log.error("处理WebSocket消息时出错: {}", e.getMessage(), e);
        }
    }
    

    private synchronized void updateFriendInformationList() {
        for (Map.Entry<String, WebSocketController> entry : webSocketSession.entrySet()) {

            // 发送消息
            String uid = entry.getKey();
            List<Friends> friends = friendsListMap.get(uid); // 获取当前用户的全部好友列表

            updateFriendsOnlineStatus(friends);

            // 发送消息
            sendP2PMessage(uid, JSON.toJSONString(Messages.builder()
                    .type("updateFriendsList")
                    .receiveUid(uid)
                    .messages(friends)
                    .build()));
        }
    }


    @OnMessage
    public void onMessage(@PathParam(value = "uid") String uid, String message) {
        log.info("【WebSocket消息】 收到客户端[{}] 发送消息:{} 连接总数:{}", uid, message, webSocketSession.size());

        // 验证消息内容
        if (StringUtils.hasLength(message)) {
            try {
                msgInfoMapper = applicationContext.getBean(MsgInfoMapper.class);
                // 消息内容转消息对象
                Messages messages = JSON.parseObject(message, Messages.class);
                // 发送消息
                sendP2PMessage(messages.getReceiveUid(), message);
                LocalDateTime currentTime = LocalDateTime.now();
                long timestamp = currentTime.toEpochSecond(ZoneOffset.UTC);
                MsgInfo msgInfo = MsgInfo.builder()
                        .sendid(uid)
                        .receiveid(messages.getReceiveUid())
                        .content(messages.getMessages().toString())
                        .timestamp(currentTime)
                        .currenttimestamp(timestamp)
                .build();
                System.out.println(msgInfo);
                msgInfoMapper.insert(msgInfo);

            } catch (Exception e) {
                log.error("WebSocket消息异常:", e);
            }
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "uid") String uid) {
        friendsList.remove(friendsList.stream().filter((friends -> friends.getUid().equals(uid))).findAny().orElse(null));
        webSocketSession.remove(uid);
        friendsListMap.remove(uid);
        onlineFriends.remove(uid);

        // 通知更新好友信息列表
        updateFriendInformationList();
        log.info("【WebSocket消息】客户端[{}]连接断开, 剩余连接总数:{}", uid, webSocketSession.size());
    }

    /**
     * 点对点发送
     */
    public static synchronized void sendP2PMessage(String uid, String message) {
        log.info("【WebSocket消息】点对点发送消息, uid={} , message={}", uid, message);
        try {
            webSocketSession.get(uid).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
        }
    }


    private void updateFriendsOnlineStatus(List<Friends> friends) {
        for (Friends friend : friends) {
            // 如果在线好友列表包含该好友，则更新其在线状态为在线（1 表示在线）
            if (onlineFriends.contains(friend.getUid())) {
                friend.setStatus(1);
            } else {
                friend.setStatus(0); // 否则更新为离线
            }
        }
    }


}
