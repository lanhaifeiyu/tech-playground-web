package com.lhfeiyu.tech.tools;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class ReturnCode {

    public static JSONObject success (JSONObject json, Map<String, Object> requestMap) {
        json.put("success", true);
        json.put("code", 1);

        if (requestMap.get("key_start") != null) {
            json.put(requestMap.get("key_start").toString(), requestMap.get(requestMap.get("key_start")));
        } else {
            json.put("start", requestMap.get("start"));
        }

        if (requestMap.get("key_count") != null) {
            json.put(requestMap.get("key_count").toString(), requestMap.get(requestMap.get("key_count")));
        } else {
            json.put("count", requestMap.get("count"));
        }

        if (requestMap.get("key_req_id") != null) {
            json.put(requestMap.get("key_req_id").toString(), requestMap.get(requestMap.get("key_req_id")));
        } else {
            json.put("req_id", requestMap.get("req_id"));
        }

        if (requestMap.get("key_data") != null) {
            json.put(requestMap.get("key_data").toString(), json.get("data"));
            json.remove("data");
        }

        if (requestMap.get("key_total") != null) {
            json.put(requestMap.get("key_total").toString(), json.get("total"));
            json.remove("total");
        }
        json.remove("err_msg");

        return json;
    }

    public static JSONObject success (JSONObject json) {
        json.put("success", true);
        json.put("code", 1);
        return json;
    }

    public static JSONObject failure (JSONObject json, String errorMsg, Map<String, Object> requestMap) {
        json.put("success", false);
        json.put("code", 0);
        json.put("err_msg", errorMsg);
        if (requestMap.get("key_start") != null) {
            json.put(requestMap.get("key_start").toString(), requestMap.get(requestMap.get("key_start")));
        } else {
            json.put("start", requestMap.get("start"));
        }

        if (requestMap.get("key_count") != null) {
            json.put(requestMap.get("key_count").toString(), requestMap.get(requestMap.get("key_count")));
        } else {
            json.put("count", requestMap.get("count"));
        }

        if (requestMap.get("key_req_id") != null) {
            json.put(requestMap.get("key_req_id").toString(), requestMap.get(requestMap.get("key_req_id")));
        } else {
            json.put("req_id", requestMap.get("req_id"));
        }

        if (requestMap.get("key_data") != null) {
            json.put(requestMap.get("key_data").toString(), json.get("data"));
            json.remove("data");
        }

        if (requestMap.get("key_total") != null) {
            json.put(requestMap.get("key_total").toString(), json.get("total"));
            json.remove("total");
        }
        return json;
    }

    public static JSONObject failure (JSONObject json, String errorMsg) {
        json.put("success", false);
        json.put("code", 0);
        json.put("err_msg", errorMsg);
        return json;
    }

    public static JSONObject failure (JSONObject json, String errorMsg, int httpStatus) {
        json.put("success", false);
        json.put("code", 0);
        json.put("err_msg", errorMsg);
        json.put("status", httpStatus);
        return json;
    }


}
