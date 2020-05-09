package com.newblog.huil.controller;

import com.newblog.huil.service.DataService;
import com.newblog.huil.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author HuilLIN
 */
@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private DataService dataService;
    private Map<String,Object> resMap = null;

    /**
     * 统计网址UV
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(path = "/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end){

        long uv = dataService.calculateUV(start, end);
        resMap = new HashMap<>();
        resMap.put("uvResult",uv);
        resMap.put("uvStartDate",start);
        resMap.put("uvEndDate",end);
        String jsonString = JsonUtil.getJSONString(1, "获取数据成功!",resMap);
        return jsonString;
    }

    /**
     * 统计活跃用户
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(path = "/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd")Date end){
        long dau = dataService.calculateDAU(start, end);
        resMap = new HashMap<>();
        resMap.put("dauResult",dau);
        resMap.put("dauStartDate",start);
        resMap.put("dauEndDate",end);
        String jsonString = JsonUtil.getJSONString(1, "获取数据成功!",resMap);
        return jsonString;
    }


}
