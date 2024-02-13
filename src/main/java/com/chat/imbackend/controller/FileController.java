package com.chat.imbackend.controller;

import com.chat.imbackend.entity.FileRequest;
import com.chat.imbackend.entity.User;
import com.chat.imbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {
    private static final String UPLOAD_DIR = "D:\\chat-main\\IMFrontend\\src\\assets\\Resource\\avatar\\";

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {
        try {
            // 获取当前时间作为文件名的一部分
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // 获取文件原始名称
            String originalFileName = file.getOriginalFilename();

            // 构建目标文件对象，包括文件名和扩展名
            String fileExtension = StringUtils.getFilenameExtension(originalFileName);
            File avatarFile = new File(UPLOAD_DIR, timestamp + "." + fileExtension);

            // 保存文件到相对路径
            file.transferTo(avatarFile);

            // 修改数据库中的用户头像路径为相对路径
            String relativePath = "src/assets/Resource/avatar/" + timestamp + "." + fileExtension;
            // 构建响应体
            return ResponseEntity.ok(relativePath);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @PostMapping("/change")
    public ResponseEntity<String> handleFileUpload(@RequestBody User user) {
        try{
            userMapper.modifyAvatar(user.getUid(), user.getAvatar());
            User Newuser = userMapper.getUserByUid(user.getUid());
            return ResponseEntity.ok(UserController.generateToken(Newuser));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }


}