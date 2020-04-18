package org.edu.mail.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * Json Web Token生成和验证工具类
 * 详情见：
 * http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html
 * https://github.com/auth0/java-jwt
 */
public class TokenUtil {
    /**
     * 生成token
     * @param secret 密钥
     * @param expire 过期时间
     * @param uid 用户id
     * @return token
     */
    public static String create(String secret, long expire, String uid){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            long now = System.currentTimeMillis();
            JWTCreator.Builder builder = JWT.create()
                    .withIssuer("umail")/*发行人*/
                    .withIssuedAt(new Date(now))/*签发时间*/
                    .withNotBefore(new Date(now))/*生效时间*/
                    .withExpiresAt(new Date(now + expire))/*过期时间*/
                    .withKeyId(uid);
            String token = builder.sign(algorithm);
            System.out.println(token);
            return token;
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 验证token
     * @param token
     * @param secret 密钥
     * @return uid
     */
    public static String verify(String token, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("umail")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println(jwt.getKeyId());
            return jwt.getKeyId();
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            exception.printStackTrace();
        }
        return null;
    }
}
