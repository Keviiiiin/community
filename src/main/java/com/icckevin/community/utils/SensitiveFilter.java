package com.icckevin.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 敏感词过滤/字典树
 * @author: iccKevin
 * @create: 2020-05-26 10:06
 **/

@Component
public class SensitiveFilter {

    public static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    private TrieNode rootNode = new TrieNode();

    private static final String REPLACEMENT = "***";

    // 前缀树
    private class TrieNode{
        private Map<Character,TrieNode> subNode = new HashMap<>();
        private boolean isEnd;

        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }

        public void addSubNode(Character c,TrieNode trieNode) {
            subNode.put(c,trieNode);
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }
    }

    @PostConstruct
    public void init(){
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword =bufferedReader.readLine())!=null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败！" + e.getMessage());
        }
    }

    // 往前缀树中添加子节点
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = rootNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;
            if(i == keyword.length() - 1)
                tempNode.setEnd(true);
        }
    }

    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    public String filter(String text) {
        if(text == null)
            return null;

        TrieNode tempNode = rootNode;
        int begin = 0;
        int current = 0;

        StringBuilder sb = new StringBuilder();

        while(current < text.length()){
            char c = text.charAt(current);

            // 如果有特殊符号
            if(isSymbol(c)){
                // 如果当前指针之前无疑似敏感词
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                current++;
                continue;
            }

            // 前缀树的指针下移
            tempNode = tempNode.getSubNode(c);
            // 如果c没有对应的节点
            if(tempNode == null){
                sb.append(text.charAt(begin));
                begin++;
                // current++;
                current = begin;
                tempNode = rootNode;
            }// 如果是结束节点
            else if(tempNode.isEnd){
                sb.append(REPLACEMENT);
                current++;
                begin = current;
            }//如果是敏感词中间的节点
            else {
                current++;
            }
        }

        sb.append(text.substring(begin));

        return sb.toString();

    }

}