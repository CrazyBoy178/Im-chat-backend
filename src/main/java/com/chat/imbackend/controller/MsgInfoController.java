package com.chat.imbackend.controller;

import com.chat.imbackend.entity.MsgInfo;
import com.chat.imbackend.mapper.MsgInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/msgInfo")
public class MsgInfoController {
    @Autowired
    MsgInfoMapper msgInfoMapper;


    @GetMapping("/{sendid}/{receiveid}")
    public ResponseEntity<String> getHistoryMessage(@PathVariable("sendid") String sendid, @PathVariable("receiveid") String receiveid){
        List<MsgInfo> allMessages = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        allMessages.addAll(msgInfoMapper.getHistoryMessages(sendid,receiveid));
        allMessages.addAll(msgInfoMapper.getHistoryMessages(receiveid,sendid));

        if (allMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("历史消息为空");
        }

        Comparator<MsgInfo> comparator = Comparator.comparingLong(MsgInfo::getCurrenttimestamp);

        // 使用 Collections.sort() 方法对列表进行排序
        Collections.sort(allMessages, comparator);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = currentTime.format(formatter);
        String fileName = sendid + "_" + receiveid + "_" + timestamp + "_Message.txt";
        String directoryPath = "src/main/resources/msgFile/";
        String filePath = directoryPath + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (MsgInfo message : allMessages) {
                // 构造消息字符串
                String msgString = "时间: " + message.getTimestamp() + "\n"
                        + "发送: " + message.getSendid() + "  接收: " + message.getReceiveid() + "\n"
                        + "消息内容: " + message.getContent() + "\n\n";
                // 写入消息字符串
                writer.write(msgString);
            }
            System.out.println("消息已写入到文件：" + filePath);
            // 返回文件路径给前端
            return ResponseEntity.ok().body(filePath);
        } catch (IOException e) {
            System.out.println("写入文件时出现错误：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("写入文件时出现错误：" + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        System.out.println("Requested file path: " + filePath);

        Path path = Paths.get(filePath);

        try {
            // 读取文件内容
            byte[] data = Files.readAllBytes(path);
            ByteArrayResource resource = new ByteArrayResource(data);

            // 构造响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName().toString());
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            // 返回 ResponseEntity
            ResponseEntity<Resource> responseEntity = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(data.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

            System.out.println("File download successful");

            return responseEntity;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to download file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
