package org.edu.mail.api.service;

import org.edu.mail.api.dao.AccountSignDao;
import org.edu.mail.api.domain.AccountSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountSignService {
    @Autowired
    private AccountSignDao accountSignDao;
    public void update(AccountSign accountSign){
        accountSignDao.update(accountSign);
    }
    public AccountSign selectAll(String account){
        return accountSignDao.selectByAccountName(account);
    }
}
