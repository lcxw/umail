package org.edu.mail.api.service;

import org.edu.mail.api.dao.ContactDao;
import org.edu.mail.api.domain.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {
    @Autowired
    private ContactDao contactDao;

    public List<Contact> selectAll(String uid){
        List<Contact> contacts = new ArrayList<>();
        contacts = contactDao.selectByUserId(uid);
        return contacts;
    }
    public int save(Contact contact){
        return contactDao.insert(contact);
    }
    public int delete(Contact contact){
        return contactDao.delete(contact);
    }
    public int update(Contact contact){
        return contactDao.update(contact);
    }
}

