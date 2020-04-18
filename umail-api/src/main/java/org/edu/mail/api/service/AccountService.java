package org.edu.mail.api.service;

import org.edu.mail.api.dao.AccountDao;
import org.edu.mail.api.dao.AccountSignDao;
import org.edu.mail.api.dao.SignDao;
import org.edu.mail.api.domain.Account;
import org.edu.mail.api.domain.AccountSign;
import org.edu.mail.api.domain.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SignDao signDao;
    @Autowired
    private AccountSignDao accountSignDao;

    public void saveAccount(Account account){
        accountDao.insert(account);
    }

    public List<Account> selectAccounts(String uid){
        return accountDao.selectByUserId(uid);
    }
    public Account selectAccount(Account account){
        return accountDao.isExist(account);
    }
    public int saveIfNotExist(Account account){
        if(accountDao.isExist(account) != null){
            return -1;
        }
        String acc = account.getAccount();
        // 设置定时收取
        account.setAlias(acc);
        account.setIsscheduled("true");
        account.setScheduledperiod("15");
        // 设置签名
        Sign sign = new Sign();
        sign.setUserid(account.getUserid());
        sign.setName(acc.substring(0, acc.indexOf('@')));
        sign.setContent("<p><br></p><p><br></p><p>----------------------------</p><p>"+ acc+"</p>");

        AccountSign accountSign = new AccountSign();
        accountSign.setAccount(acc);
        accountSign.setSendsign(sign.getName());
        accountSign.setReplysign(sign.getName());

        if(signDao.isExist(sign.getName()) == null){
            signDao.insert(sign);
        }
        if(accountSignDao.isExist(accountSign.getAccount()) == null){
            accountSignDao.insert(accountSign);
        }

        return accountDao.insert(account);
    }
    public int delete(Account account){
        return accountDao.delete(account.getAccount());
    }
    public int update(Account account){
        return accountDao.update(account);
    }
}

