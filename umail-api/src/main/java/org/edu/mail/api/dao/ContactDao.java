package org.edu.mail.api.dao;


import org.apache.ibatis.annotations.Mapper;
import org.edu.mail.api.domain.Contact;

import java.util.List;

@Mapper
public interface ContactDao {
    int insert(Contact record);

    int insertSelective(Contact record);

    List<Contact> selectByUserId(String userId);

    int delete(Contact contact);

    int update(Contact contact);
}