package com.chat.imbackend.controller;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/token")
public class TokenController {

    @Value("${jwt.secret}")
    private String secret; // 注入JWT密钥

    @PostMapping("/decodeToken")
    public ResponseEntity<?> decodeToken(@RequestBody String token) {
        try {
            // 解密 token，替换 YOUR_SECRET_KEY 为实际的密钥
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            // 获取解密后的 uid
            String uid = claimsJws.getBody().getSubject();
            String nickname = (String) claimsJws.getBody().get("nickname");
            String avatar = (String) claimsJws.getBody().get("avatar");
            String jtime = (claimsJws.getBody().get("jointime")).toString();


            // 构建 JSON 响应
            TokenResponse tokenResponse = new TokenResponse(uid,nickname,avatar,jtime);
            return ResponseEntity.ok(tokenResponse);
        } catch (SignatureException e) {
            // 处理签名错误
            System.err.println("Invalid Token Signature");
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ErrorResponse("Invalid Token Signature"));
        } catch (Exception e) {
            // 处理其他解密错误
            System.err.println("Failed to decode token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ErrorResponse("Failed to decode token: " + e.getMessage()));
        }
    }

    // 定义 Token 响应类
    @Getter
    static class TokenResponse {
        private String uid;
        private String nickname;
        private String avatar;
        private String jtime;

        public TokenResponse(String uid,String nickname,String avatar,String jtime) {

            this.uid = uid;
            this.nickname = nickname;
            this.avatar = avatar;
            this.jtime = jtime;
        }

    }

    // 定义错误响应类
    @Getter
    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

    }
}
