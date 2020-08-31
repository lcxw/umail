package org.edu.mail.api.service;

import org.edu.mail.api.dao.SignDao;
import org.edu.mail.api.domain.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignService {
    @Autowired
    private SignDao signDao;
    public List<Sign> selectAll(String uid){
        return signDao.selectByUserId(uid);
    }
    public int update(Sign sign){
        return signDao.update(sign);
    }
    public int save(Sign sign){
        return signDao.insertSelective(sign);
    }
    public int delete(Sign sign){
        return signDao.delete(sign.getName());
    }

    public Sign selectByName(String name){
        return signDao.selectByName(name);
    }


}
