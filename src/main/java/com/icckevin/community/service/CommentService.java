package com.icckevin.community.service;

import com.icckevin.community.dao.CommentMapper;
import com.icckevin.community.entity.Comment;
import com.icckevin.community.entity.DiscussPost;
import com.icckevin.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-05-27 17:21
 **/
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment){

        comment.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(comment.getContent())));
        int rows = commentMapper.insertComment(comment);

        return rows;
    }
}