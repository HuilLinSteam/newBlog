package com.newblog.huil.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuilLIN
 */
public class JsonUtil {
    public static String getJSONString(int code, String message, Map<String ,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("message",message);
        if(map != null){
            for (String key : map.keySet()) {
                json.put(key,map);
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String message, Object data){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("message",message);
        json.put("data",data);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String message) {
        return getJSONString(code, message, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }

}
