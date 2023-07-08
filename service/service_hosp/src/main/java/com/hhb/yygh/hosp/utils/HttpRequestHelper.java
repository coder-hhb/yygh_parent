package com.hhb.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpRequestHelper {
    public static Map<String, Object> switchRequest(Map<String, String[]> parameterMap) {
//        Map<String, Object> resultMap = new HashMap<>();
//        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
//        for (Map.Entry<String, String[]> entry : entries) {
//            String key = entry.getKey();
//            String value = entry.getValue()[0];
//            resultMap.put(key,value);
//        }
        //stream流写法
        Map<String, Object> resultMap = parameterMap.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()[0]));
        return resultMap;
    }
}
