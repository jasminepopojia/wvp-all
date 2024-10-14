package com.genersoft.iot.vmp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.genersoft.iot.vmp.service.IUserService;
import com.genersoft.iot.vmp.storager.dao.UserMapper;
import com.genersoft.iot.vmp.storager.dao.dto.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@DS("master")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser(String username, String password) {
        return userMapper.select(username, password);
    }

    @Override
    public boolean changePassword(int id, String password) {
        User user = userMapper.selectById(id);
        user.setPassword(password);
        return userMapper.update(user) > 0;
    }

    @Override
    public User getUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public int addUser(User user) {
        User userByUsername = userMapper.getUserByUsername(user.getUsername());
        if (userByUsername != null) {
            return 0;
        }
        return userMapper.add(user);
    }
    @Override
    public int addRegUser(User user) {
        User userByUsername = userMapper.getUserByUsername(user.getUsername());
        if (userByUsername != null) {
            return 0;
        }
        return userMapper.add_regUser(user);
    }

    @Override
    public int addUser_app(User user) {
        return 0;
    }

    @Override
    public int deleteUser(int id) {
        return userMapper.delete(id);
    }
    @Override
    public int deleteRegUser(int id) {
        return userMapper.delete_regUser(id);
    }

    @Override
    public PageInfo<User> getAllUsers(int page, int count) {
        PageHelper.startPage(page, count);
        List<User> users = userMapper.selectAll();
        return new PageInfo<>(users);
    }
//    @Override
//    public List<User> getAllUsers() {
//        return userMapper.selectAll();
//    }

    @Override
    public int updateUsers(User user) {
        return userMapper.update(user);
    }


    @Override
    public boolean checkPushAuthority(String callId, String sign) {

        List<User> users = userMapper.getUsers();
        if (users.size() == 0)  {
            return false;
        }
        for (User user : users) {
            if (user.getPushKey() == null) {
                continue;
            }
            String checkStr = callId == null? user.getPushKey():(callId + "_" + user.getPushKey())  ;
            String checkSign = DigestUtils.md5DigestAsHex(checkStr.getBytes());
            if (checkSign.equals(sign)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PageInfo<User> getUsers(int page, int count) {
        PageHelper.startPage(page, count);
        List<User> users = userMapper.getUsers();
        return new PageInfo<>(users);
    }

    @Override
    public int changePushKey(int id, String pushKey) {
        return userMapper.changePushKey(id,pushKey);
    }

    @Override
    public List<User> getAllRegUsers() {
        return userMapper.getAllRegUsers();
    }



    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }
}
