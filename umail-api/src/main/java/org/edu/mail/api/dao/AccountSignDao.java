package org.edu.mail.api.dao;


import org.apache.ibatis.annotations.Mapper;
import org.edu.mail.api.domain.AccountSign;

@Mapper
public interface AccountSignDao {
    int insert(AccountSign record);

    int insertSelective(AccountSign record);

    AccountSign selectByAccountName(String account);

    int update(AccountSign record);

    AccountSign isExist(String account);
}