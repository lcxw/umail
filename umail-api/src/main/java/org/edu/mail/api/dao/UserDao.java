package org.edu.mail.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.edu.mail.api.domain.User;

import java.util.List;

@Mapper
public interface UserDao {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User isExist(User user);

    List<User> selectAll();
}