package com.lhfeiyu.tech.tools;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ActionUtil {

    public static Map<String, Object> getRequeseParams (HttpServletRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        Enumeration emum = request.getParameterNames();
        while (emum.hasMoreElements()) {
            String en = (String) emum.nextElement();
            String value = request.getParameter(en);
            requestMap.put(en, value);
        }
        return requestMap;
    }

}
