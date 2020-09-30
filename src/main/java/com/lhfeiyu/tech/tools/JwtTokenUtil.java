package com.lhfeiyu.tech.tools;

import com.lhfeiyu.tech.DTO.JwtParams;
import com.lhfeiyu.tech.DTO.RtvConsoleUser;
import com.lhfeiyu.tech.exception.LogonException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {

    public static final String AUTH_HEADER_KEY = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    private static Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * 解析jwt
     * @param jsonWebToken
     * @param base64Security
     * @return
     */
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (ExpiredJwtException eje) {
            log.error("===== Token过期 =====", eje);
            throw new LogonException("Token过期");
        } catch (Exception e){
            log.error("===== token解析异常 =====", e);
            throw new LogonException("token解析异常");
        }
    }

    /**
     * 构建jwt
     * @param user        用户
     * @param msg         用户信息
     * @param role        角色
     * @param expire      过期时间
     * @return
     */
    public static String createJWT(RtvConsoleUser user, String msg, String role, String expire) {
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JwtParams.SECRET);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setId(String.valueOf(user.getId()))
                    .setHeaderParam("typ", "JWT")
                    // 可以将基本不重要的对象信息放到claims
                    .claim("role", role)
                    .claim("uniqueId", user.getDepartmentId())
                    .claim("corpId", user.getCorpId())
                    .claim("userId", user.getId())
                    .setSubject(msg)           // 代表这个JWT的主体，即它的所有人
                    .setIssuedAt(new Date())        // 是一个时间戳，代表这个JWT的签发时间；
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            int TTLMillis = Integer.parseInt(expire) * 1000;
            if (TTLMillis >= 0) {
                long expMillis = nowMillis + TTLMillis;
                Date exp = new Date(expMillis);
                builder.setExpiration(exp)  // 是一个时间戳，代表这个JWT的过期时间；
                        .setNotBefore(now); // 是一个时间戳，代表这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
            }
            //生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new RuntimeException("签名失败");
        }
    }

    /**
     * 从token中获取corpId
     * @param token
     * @return
     */
    public static Integer getCorpId(String token){
        return parseJWT(token.substring(7), JwtParams.SECRET).get("corpId", Integer.class);
    }

    /**
     * 从token中获取用户ID
     * @param token
     * @return
     */
    public static Integer getUserId(String token){
        Integer userId = parseJWT(token.substring(7), JwtParams.SECRET).get("userId", Integer.class);
        return userId;
    }

    /**
     * 是否已过期
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) {
        return parseJWT(token.substring(7), JwtParams.SECRET).getExpiration().before(new Date());
    }


}
