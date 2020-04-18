package org.edu.mail.api.service;

import org.edu.mail.api.dao.UserDao;
import org.edu.mail.api.domain.User;
import org.edu.mail.api.util.MD5;
import org.edu.mail.api.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    public void saveUser(User user){
        user.setSecret(MD5.getInstance().getMD5(user.getSecret()));
        userDao.insert(user);
    }
    public List<User> selectAll(){
        return userDao.selectAll();
    }
    public int update(User user){
        user.setSecret(MD5.getInstance().getMD5(user.getSecret()));
        return userDao.updateByPrimaryKeySelective(user);
    }
    public String login(User user){
        user.setName(user.getName());
        user.setEmail(user.getName());
        user.setSecret(MD5.getInstance().getMD5(user.getSecret()));
        User newUser = userDao.isExist(user);
        if(newUser != null){
            // 7天
            return TokenUtil.create("uid", 7*24*60*60*1000, newUser.getId());
        }
        return null;
    }
}
