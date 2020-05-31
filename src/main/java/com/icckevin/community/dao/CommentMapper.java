package com.icckevin.community.dao;

import com.icckevin.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
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
     * 查询评论/回复的条数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 插入一条评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);
}
