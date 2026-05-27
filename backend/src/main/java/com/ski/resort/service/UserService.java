package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.User;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author XXX
 * @date 2025-10-08
 */
public interface UserService {

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User getUserByPhone(String phone);

    /**
     * 添加用户
     */
    void addUser(User user);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 根据角色查询用户列表
     */
    List<User> getUsersByRole(String role);

    /**
     * 分页查询用户列表
     */
    PageResult<User> getUserPage(Integer pageNum, Integer pageSize);

    /**
     * 根据条件分页查询用户列表
     */
    PageResult<User> getUserPageByCondition(String username, String realName, String phone,
                                            String role, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

}



