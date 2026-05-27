package com.ski.resort.mapper;

import com.ski.resort.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 根据ID删除用户（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 根据角色查询用户列表
     */
    List<User> selectByRole(@Param("role") String role);

    /**
     * 分页查询用户列表
     */
    List<User> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计用户总数
     */
    Long countAll();

    /**
     * 根据条件统计用户数
     */
    Long countByCondition(@Param("username") String username,
                          @Param("realName") String realName,
                          @Param("phone") String phone,
                          @Param("role") String role,
                          @Param("status") Integer status);

    /**
     * 根据条件查询用户列表
     */
    List<User> selectByCondition(@Param("username") String username,
                                  @Param("realName") String realName,
                                  @Param("phone") String phone,
                                  @Param("role") String role,
                                  @Param("status") Integer status,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

}



