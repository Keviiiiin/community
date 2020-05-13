package com.icckevin.community.dao;

import com.icckevin.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    /**
     * 根据id查询
     * @param id
     * @return
     */
    User selectById(int id);
}
