package org.edu.mail.api.dao;


import org.apache.ibatis.annotations.Mapper;
import org.edu.mail.api.domain.Sign;

import java.util.List;
@Mapper
public interface SignDao {
    int insert(Sign record);

    int insertSelective(Sign record);

    List<Sign> selectByUserId(String uid);

    int update(Sign record);

    int delete(String name);

    Sign isExist(String name);

    Sign selectByName(String name);
}