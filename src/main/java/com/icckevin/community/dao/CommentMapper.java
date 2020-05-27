package com.icckevin.community.dao;

import com.icckevin.community.entity.Comment;

import java.util.List;

public interface CommentMapper {

    /**
     * 根据类别和id分页查询评论和回复
     * 如：给帖子的评论（类别1），帖子id为228
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 查询回复的条数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);
}
