//package com.chat.imbackend.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.chat.imbackend.entity.Friends;
//import com.chat.imbackend.entity.GroupInfo;
//import com.chat.imbackend.entity.Messages;
//import com.chat.imbackend.entity.MsgInfo;
//import com.chat.imbackend.mapper.GroupMapper;
//import com.chat.imbackend.mapper.MsgInfoMapper;
//import com.chat.imbackend.mapper.SocketMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationContext;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@RestController
//@CrossOrigin
//@ServerEndpoint(value = "/chatroom/{groupId}/{uid}")
//public class WebsocketChatroomController {
//
//
//    private GroupMapper groupMapper;
//
//    private GroupController groupController;
//
//    private static ApplicationContext applicationContext;
//
//    private static final Map<String, ConcurrentHashMap<String, Session>> rooms = new ConcurrentHashMap<>();
//
//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        WebsocketChatroomController.applicationContext = applicationContext;
//    }
//    @OnOpen
//    public void onOpen(@PathParam(value = "groupId") String groupId,
//                       @PathParam(value = "uid") String uid,
//                       Session session) {
//        groupMapper = applicationContext.getBean(GroupMapper.class);
//        groupController = applicationContext.getBean(GroupController.class);
//        List<GroupInfo> groupMembers = groupController.getAllUserGroup(groupId); // 获取群聊成员
//
//        if (groupMembers.stream().anyMatch(groupInfo -> groupInfo.getGroupId().equals(groupId))) {
//            rooms.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>()).put(uid, session);
//            log.info("User {} joined group {}", uid, groupId);
//        } else {
//            log.warn("User {} tried to join group {} but not authorized", uid, groupId);
//            try {
//                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "You are not authorized to join this group"));
//            } catch (IOException e) {
//                log.error("Failed to close session", e);
//            }
//        }
//    }
//
//    @OnMessage
//    public void onMessage(@PathParam(value = "groupId") String groupId,
//                          @PathParam(value = "uid") String uid,
//                          String message) {
//        log.info("Received message from user {} in group {}: {}", uid, groupId, message);
//        broadcast(groupId, uid, message);
//    }
//
//    @OnClose
//    public void onClose(@PathParam(value = "groupId") String groupId,
//                        @PathParam(value = "uid") String uid) {
//        rooms.computeIfPresent(groupId, (k, v) -> {
//            v.remove(uid);
//            log.info("User {} left group {}", uid, groupId);
//            if (v.isEmpty()) {
//                rooms.remove(k);
//                log.info("Group {} is empty and removed", groupId);
//            }
//            return v;
//        });
//    }
//
//    private void broadcast(String groupId, String uid, String message) {
//        ConcurrentHashMap<String, Session> room = rooms.get(groupId);
//        if (room != null) {
//            for (Session session : room.values()) {
//                try {
//                    session.getBasicRemote().sendText(uid + ": " + message);
//                } catch (IOException e) {
//                    log.error("Failed to send message to session", e);
//                }
//            }
//        }
//    }
//}
