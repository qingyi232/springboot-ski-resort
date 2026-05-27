package com.ski.resort.service.impl;

import com.ski.resort.common.Constants;
import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.User;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.UserMapper;
import com.ski.resort.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户Service实现类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getUserByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        return userMapper.selectByPhone(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        // 参数校验
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(user.getPhone())) {
            existUser = userMapper.selectByPhone(user.getPhone());
            if (existUser != null) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(Constants.USER_STATUS_NORMAL);
        }
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole(Constants.ROLE_USER);
        }
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar(Constants.DEFAULT_AVATAR);
        }

        int result = userMapper.insert(user);
        if (result == 0) {
            throw new BusinessException("添加用户失败");
        }

        log.info("添加用户成功，用户名：{}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 检查用户是否存在
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(existUser.getUsername())) {
            User userWithSameUsername = userMapper.selectByUsername(user.getUsername());
            if (userWithSameUsername != null) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
            }
        }

        // 如果修改了手机号，检查新手机号是否已存在
        if (StringUtils.hasText(user.getPhone()) && !user.getPhone().equals(existUser.getPhone())) {
            User userWithSamePhone = userMapper.selectByPhone(user.getPhone());
            if (userWithSamePhone != null) {
                throw new BusinessException("手机号已被使用");
            }
        }

        int result = userMapper.update(user);
        if (result == 0) {
            throw new BusinessException("更新用户失败");
        }

        log.info("更新用户成功，用户ID：{}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        int result = userMapper.deleteById(id);
        if (result == 0) {
            throw new BusinessException("删除用户失败");
        }

        log.info("删除用户成功，用户ID：{}", id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        if (!StringUtils.hasText(role)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        return userMapper.selectByRole(role);
    }

    @Override
    public PageResult<User> getUserPage(Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) {
            pageNum = Constants.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }
        if (pageSize > Constants.MAX_PAGE_SIZE) {
            pageSize = Constants.MAX_PAGE_SIZE;
        }

        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据
        List<User> list = userMapper.selectPage(offset, pageSize);
        Long total = userMapper.countAll();

        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public PageResult<User> getUserPageByCondition(String username, String realName, String phone,
                                                    String role, Integer status, Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) {
            pageNum = Constants.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }
        if (pageSize > Constants.MAX_PAGE_SIZE) {
            pageSize = Constants.MAX_PAGE_SIZE;
        }

        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据
        List<User> list = userMapper.selectByCondition(username, realName, phone, role, status, offset, pageSize);
        Long total = userMapper.countByCondition(username, realName, phone, role, status);

        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user) {
        // 参数校验
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查手机号是否已存在（如果提供了手机号）
        if (StringUtils.hasText(user.getPhone())) {
            existUser = userMapper.selectByPhone(user.getPhone());
            if (existUser != null) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 设置默认值
        user.setRole(Constants.ROLE_USER);  // 注册用户默认为普通用户
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setAvatar(Constants.DEFAULT_AVATAR);

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        int result = userMapper.insert(user);
        if (result == 0) {
            throw new BusinessException("注册失败");
        }

        log.info("用户注册成功，用户名：{}", user.getUsername());
    }

    @Override
    public User login(String username, String password) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus().equals(Constants.USER_STATUS_DISABLED)) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        log.info("用户登录成功，用户名：{}", username);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 参数校验
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.update(user);
        if (result == 0) {
            throw new BusinessException("修改密码失败");
        }

        log.info("修改密码成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        // 参数校验
        if (userId == null || !StringUtils.hasText(newPassword)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.update(user);
        if (result == 0) {
            throw new BusinessException("重置密码失败");
        }

        log.info("重置密码成功，用户ID：{}", userId);
    }

}



