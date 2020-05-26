package com.icckevin.community.service;

import com.icckevin.community.dao.DiscussPostMapper;
import com.icckevin.community.entity.DiscussPost;
import com.icckevin.community.utils.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询所有帖子并分页显示（有userId则查询用户帖子）
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /**
     * 查询用户记录数
     * @param userId
     * @return
     */
    public int findDiscussPostRows(@Param("userId") int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 插入一条帖子
     * @param discussPost
     * @return
     */
    public int insertDiscussPost(DiscussPost discussPost){
        if(discussPost == null)
            throw new IllegalArgumentException("参数不能为空！");

        // 转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }
}