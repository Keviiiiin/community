package com.icckevin.community.service;

import com.icckevin.community.dao.DiscussPostMapper;
import com.icckevin.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 帖子业务
 * @author: iccKevin
 * @create: 2020-05-13 21:11
 **/
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 查询所有帖子并分页显示（有userId则查询用户帖子）
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /**
     * 查询用户记录数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}