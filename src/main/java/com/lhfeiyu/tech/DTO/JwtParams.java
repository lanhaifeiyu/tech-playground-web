package com.lhfeiyu.tech.DTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtParams {

    public static String SECRET;
//    public static String EXPIRE;

    @Value("${jwt.secret}")
    public void setSECRET(String SECRET) {
        JwtParams.SECRET = SECRET;
    }

    /*@Value("${jwt.expire}")
    public static void setEXPIRE(String EXPIRE) {
        JwtParams.EXPIRE = EXPIRE;
    }*/
}
