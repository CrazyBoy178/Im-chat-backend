package com.chat.imbackend.controller;


import com.alibaba.fastjson.JSON;
import com.chat.imbackend.entity.*;
import com.chat.imbackend.mapper.MsgInfoMapper;
import lombok.extern.slf4j.Slf4j;

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
@ServerEndpoint(value = "/chatroom/{uid}/{groupId}")
public class WebsocketChatroomController  {


    private Session session;




    /**
     * 好友列表
     */
    public List<GroupInfo> groupsList = new ArrayList<>();

    /**
     * 定义并发HashMap存储好友WebSocket集合
     */
    public static ConcurrentHashMap<String, List<WebsocketChatroomController>> groupSessions = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, List<GroupInfo>> groupsListMap = new ConcurrentHashMap<>();


    private static ApplicationContext applicationContext;

    private GroupController groupController;

    private MsgInfoMapper msgInfoMapper;

    private String username;

    private String getUid() {
        return username;
    }



    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebsocketChatroomController.applicationContext = applicationContext;
    }


    @OnOpen
    public void onOpen(@PathParam(value = "uid") String uid, @PathParam(value = "groupId") String groupId,  Session session) throws IOException {
        log.info("【WebSocket消息】 收到客户端[{}] 连接群聊:{}", uid , groupId);
        // 设置session
        this.session = session;
        this.username = uid;

        if (!groupSessions.containsKey(groupId)) {
            groupSessions.put(groupId, new ArrayList<>());
        }
        groupSessions.get(groupId).add(this);
        updateGroupInformationList(groupId);
    }

    private synchronized void updateGroupInformationList(String groupId) throws IOException {
        String messages = JSON.toJSONString(Messages.builder()
                .type("updateFriendsList")
                .receiveUid(username)
                .messages(groupId)
                .build());
        log.info("【WebSocket消息】点对点发送消息, uid={} , message={}", this.username,messages );
        session.getBasicRemote().sendText(messages);

    }


    @OnMessage
    public void onMessage(@PathParam(value = "uid") String uid, @PathParam(value = "groupId") String groupId, String message) {
        log.info("【WebSocket消息】 收到客户端[{}] 发送消息:{} 连接总数:{}", uid, message, groupSessions.size());

        // 验证消息内容
        if (StringUtils.hasLength(message)) {
            try {
                msgInfoMapper = applicationContext.getBean(MsgInfoMapper.class);
                GroupMessages groupMessage = JSON.parseObject(message, GroupMessages.class);
                // 发送消息给群组中的所有成员
                sendGroupMessage(groupId,uid, message);
                LocalDateTime currentTime = LocalDateTime.now();
                long timestamp = currentTime.toEpochSecond(ZoneOffset.UTC);
                MsgInfo msgInfo = MsgInfo.builder()
                        .sendid(uid)
                        .receiveid(groupMessage.getReceiveGroupId())
                        .content(groupMessage.getMessages().toString())
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
    public void onClose(@PathParam(value = "uid") String uid, @PathParam(value = "groupId") String groupId) {
        // 移除关闭的 WebSocket 连接
        if (groupSessions.containsKey(groupId)) {
            groupSessions.get(groupId).remove(this);
            // 如果群组为空了，也可以考虑将其从群组列表中移除
            if (groupSessions.get(groupId).isEmpty()) {
                groupSessions.remove(groupId);
            }

        }
        log.info("【WebSocket消息】客户端[{}]连接断开, 剩余连接总数:{}", uid, groupSessions.size());
    }


    public static synchronized void sendGroupMessage(String groupId,String uid, String message) {
        log.info("【WebSocket消息】群组发送消息, groupId={}, message={}", groupId, message);
        if (groupSessions.containsKey(groupId)) {
            for (WebsocketChatroomController session : groupSessions.get(groupId)) {
                try {
                    log.info(uid+' '+session.getUid());
                    if (!session.getUid().equals(uid)) {
                        // 将发送者信息包含在消息中
                        session.session.getBasicRemote().sendText(message);
                    }
                } catch (IOException e) {
                    log.error("群组消息发送异常:", e);
                }
            }
        }
    }



}
