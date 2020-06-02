package com.icckevin.community;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 测试List<Map < String, Object>>
 * @author: iccKevin
 * @create: 2020-06-02 11:46
 **/
public class ListMapTest {
    @Test
    public void testListMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        Map<String,Object> map2 = new HashMap<>();

        map1.put("a","a");
        map1.put("b","b");
        list.add(map1);

        // 此时会修改list中map1的值
        map1.put("a","aaa");
        map1.put("b","bbb");
        // 此步会再次将map1添加到list中，因为List可以有重复元素
        list.add(map1);

        System.out.println(list);
    }
}