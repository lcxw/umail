package org.edu.mail.api.util;

import org.edu.mail.api.domain.Account;
import org.edu.mail.usage.UMailAccount;

/**
 * 账户格式化为usage包中的UMailAccount
 */
public class UAccountFormat {
    public static UMailAccount format(Account account){
        UMailAccount uaccount = new UMailAccount(account.getRecprotocol(), account.getAccount(), account.getPassword(),
                account.getRechost(), Integer.valueOf(account.getRecport()), Boolean.valueOf(account.getRecisssl()),
                account.getSendhost(),Integer.valueOf(account.getSendport()),Boolean.valueOf(account.getSendisssl()));
        return uaccount;
    }
}
