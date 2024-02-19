package com.chat.imbackend.controller;


import com.chat.imbackend.entity.BCryptExample;
import com.chat.imbackend.entity.FriendShip;
import com.chat.imbackend.entity.User;
import com.chat.imbackend.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.RandomStringUtils;


import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController extends HttpController<UserMapper,User> {


    @Autowired
    private UserMapper userMapper;

    
    private static final String secret = "9bf18b6f32f9e29e4b3b442d8a41e376b216b1b0a6228625a2c1a1376a46b16a51012b4db13e0b2f48732f7df8dcba15c3b7d16759f4c10192e9f692a157e82f"; // 注入JWT密钥

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
//         检查是否已存在相同用户名
        String uid = generateRandomUid();

        User exuser = userMapper.getUserByUid(uid);

        while(exuser!=null) {
            //System.out.println(userMapper.selectById(uid));
            uid=generateRandomUid();
            exuser = userMapper.getUserByUid(uid);
        }


        user.setUid(uid);
        user.setNickname(user.getNickname());
        user.setPassword(BCryptExample.hashPassword(user.getPassword()));
        user.setJtime(new Date());

        // 执行注册
        int result = userMapper.insert(user);

        if(result>0){
            //return "200";
            return generateToken(user);

        }else{
            return "0";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        System.out.println(user.getUid());
        User existingUser = userMapper.getUserByUid(user.getUid());
        System.out.println(existingUser);
        if (existingUser != null && BCryptExample.checkPassword(user.getPassword(), existingUser.getPassword())) {
            // Generate a token and return it upon successful login
            String token = generateToken(existingUser); // You need to implement this method
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<String> modifyUser(@RequestBody User user) {
        try {
            userMapper.modifyUserName(user.getUid(), user.getNickname());
            User Newuser = userMapper.getUserByUid(user.getUid());

            return ResponseEntity.ok(generateToken(Newuser));
        } catch (Exception e) {
            // 可以根据具体情况进行日志记录或其他处理
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to modify user");
        }
    }


    @GetMapping("/page/count")
    public int count() {
        return userMapper.getCount();
    }


    @GetMapping("/{userid}/page/{page}")
    public Set<Object> getUserPage(@PathVariable("userid") String userid,@PathVariable("page") int page) {
        Set<Object> Users = new HashSet<>();

        // 查询数据库获取朋友关系列表
        List<User> users = userMapper.getPage(userid,(page - 1) * 10);

        if(!users.isEmpty()){
            for (User user : users) {
                Map<String, String> userData = new HashMap<>();
                userData.put("uid", user.getUid());
                userData.put("avatar", user.getAvatar());
                userData.put("nickname", user.getNickname());
                userData.put("jtime", user.getJtime().toString());

                Users.add(userData);
            }

        }
        return Users;
        // 处理从数据库中查询的朋友关系列表
    }

    @GetMapping("/getUsers")
    private List<String> getUsers(@RequestParam("uid") String uid){
        List<String> uidList = new ArrayList<>();
        List<User> userList = userMapper.getAllUser(uid);
        for (User user : userList) {
            uidList.add(user.getUid());
        }
        return uidList;
    }




    private String generateRandomUid() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
    public static String generateToken(User user) {
        // 生成token
        return Jwts.builder()
                .setSubject(user.getUid())
                .claim("nickname", user.getNickname())
                .claim("avatar",user.getAvatar())
                .claim("jointime",user.getJtime())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 8640000)) // 有效期为10天
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
