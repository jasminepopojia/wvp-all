package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.storager.dao.dto.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    @Insert("INSERT INTO wvp_user (username, password, role_id, push_key, create_time, update_time,email,phoneNumber) VALUES" +
            "(#{username}, #{password}, #{role.id}, #{pushKey}, #{createTime}, #{updateTime},#{email},#{phoneNumber})")
    int add(User user);

    @Insert("INSERT INTO wvp_reg_user (username, password, role_id, push_key, create_time, update_time,email,phoneNumber) VALUES" +
            "(#{username}, #{password}, #{role.id}, #{pushKey}, #{createTime}, #{updateTime},#{email},#{phoneNumber})")
    int add_regUser(User user);

    @Update(value = {" <script>" +
            "UPDATE wvp_user " +
            "SET update_time=#{updateTime} " +
            "<if test=\"pushKey != null\">, push_key=#{pushKey}</if>" +
            "<if test=\"role != null\">, role_id=#{role.id}</if>" +
            "<if test=\"password != null\">, password=#{password}</if>" +
            "<if test=\"username != null\">, username=#{username}</if>" +
            "<if test=\"phoneNumber != null\">, phoneNumber=#{phoneNumber}</if>" +
            "<if test=\"email != null\">, email=#{email}</if>" +
            "WHERE id=#{id}" +
            " </script>"})
    int update(User user);

    @Update(value = {" <script>" +
            "UPDATE wvp_reg_user " +
            "SET update_time=#{updateTime} " +
            "<if test=\"pushKey != null\">, push_key=#{pushKey}</if>" +
            "<if test=\"role != null\">, role_id=#{role.id}</if>" +
            "<if test=\"password != null\">, password=#{password}</if>" +
            "<if test=\"username != null\">, username=#{username}</if>" +
            "<if test=\"phoneNumber != null\">, phoneNumber=#{phoneNumber}</if>" +
            "<if test=\"email != null\">, email=#{email}</if>" +
            "WHERE id=#{id}" +
            " </script>"})
    int update_regUser(User user);

    @Delete("DELETE from wvp_user WHERE id != 1 and id=#{id}")
    int delete(int id);

    @Delete("DELETE from wvp_reg_user WHERE id=#{id}")
    int delete_regUser(int id);

    @Select("select u.*, r.id as role_id, r.name as roleName, r.authority as roleAuthority , r.create_time as role_create_time , r.update_time as role_update_time from wvp_user u, wvp_user_role r WHERE u.role_id=r.id and u.username=#{username} AND u.password=#{password}")
    @Results(id = "roleMap", value = {
            @Result(column = "role_id", property = "role.id"),
            @Result(column = "role_name", property = "role.name"),
            @Result(column = "role_authority", property = "role.authority"),
            @Result(column = "role_create_time", property = "role.createTime"),
            @Result(column = "role_update_time", property = "role.updateTime")
    })
    User select(@Param("username") String username, @Param("password") String password);

    @Select("select u.*, r.id as role_id, r.name as role_name, r.authority as role_authority , r.create_time as role_create_time , r.update_time as role_update_time from wvp_user u, wvp_user_role r WHERE u.role_id=r.id and u.id=#{id}")
    @ResultMap(value="roleMap")
    User selectById(int id);

    @Select("select u.*, r.id as role_id, r.name as role_name, r.authority as role_authority , r.create_time as role_create_time , r.update_time as role_update_time from wvp_user u, wvp_user_role r WHERE u.role_id=r.id and u.username=#{username}")
    @ResultMap(value="roleMap")
    User getUserByUsername(String username);

    @Select("select u.*, r.id as role_id, r.name as role_name, r.authority as role_authority , r.create_time as role_create_time , r.update_time as role_update_time from wvp_user u, wvp_user_role r WHERE u.role_id=r.id")
    @ResultMap(value="roleMap")
    List<User> selectAll();

    @Select("select u.id, u.username,u.push_key,u.role_id, r.id as role_id, r.name as role_name, r.authority as role_authority , r.create_time as role_create_time , r.update_time as role_update_time from wvp_user u join wvp_user_role r on u.role_id=r.id")
    @ResultMap(value="roleMap")
    List<User> getUsers();

    @Update("UPDATE wvp_user set push_key=#{pushKey} where id=#{id}")
    int changePushKey(@Param("id") int id, @Param("pushKey") String pushKey);

    @Select("select u.* from wvp_reg_user u")
    List<User> getAllRegUsers();

    @Select("SELECT * FROM wvp_reg_user WHERE id = #{id}")
    User getUserById(@Param("id") Integer id);

    @Select("SELECT phoneNumber FROM wvp_user WHERE role_id IN (1, 2)")
    List<String> findPhoneNumbers();





}
