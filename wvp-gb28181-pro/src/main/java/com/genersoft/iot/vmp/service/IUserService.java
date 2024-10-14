package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.storager.dao.dto.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IUserService {

    User getUser(String username, String password);


    boolean changePassword(int id, String password);

    User getUserById(int id);

    User getUserByUsername(String username);

    int addUser(User user);
    int addRegUser(User user);

    int addUser_app(User user);

    int deleteUser(int id);
    int deleteRegUser(int id);

    PageInfo<User> getAllUsers(int page, int count);
//    List<User> getAllUsers();

    List<User> getAllRegUsers();


    int updateUsers(User user);

    boolean checkPushAuthority(String callId, String sign);

    PageInfo<User> getUsers(int page, int count);

    int changePushKey(int id, String pushKey);



    User getUserById(Integer id);






}
