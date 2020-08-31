package org.edu.mail.api.dao;

import org.apache.ibatis.annotations.Mapper;
import org.edu.mail.api.domain.Account;

import java.util.List;

@Mapper
public interface AccountDao {
    int insert(Account record);

    int insertSelective(Account record);

    Account isExist(Account record);

    List<Account> selectByUserId(String uid);

    int delete(String account);

    int update(Account account);
}