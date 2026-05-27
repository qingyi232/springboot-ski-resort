package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.dto.LoginDTO;
import com.ski.resort.dto.RegisterDTO;
import com.ski.resort.entity.User;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.service.UserService;
import com.ski.resort.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 *
 * @author XXX
 * @date 2025-10-08
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 测试接口
     */
    @ApiOperation("测试接口")
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("飞跃滑雪场管理系统 - 后端接口测试成功！");
    }

    /**
     * 根据ID查询用户
     */
    @ApiOperation("根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getUserById(@ApiParam("用户ID") @PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 查询所有用户
     */
    @ApiOperation("查询所有用户")
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        List<User> list = userService.getAllUsers();
        return Result.success(list);
    }

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<User> pageResult = userService.getUserPage(pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据条件分页查询用户列表
     */
    @ApiOperation("根据条件分页查询用户列表")
    @GetMapping("/page/condition")
    public Result<PageResult<User>> getUserPageByCondition(
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("真实姓名") @RequestParam(required = false) String realName,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("角色") @RequestParam(required = false) String role,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<User> pageResult = userService.getUserPageByCondition(
                username, realName, phone, role, status, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据角色查询用户列表
     */
    @ApiOperation("根据角色查询用户列表")
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@ApiParam("角色") @PathVariable String role) {
        List<User> list = userService.getUsersByRole(role);
        return Result.success(list);
    }

    /**
     * 根据手机号查询用户
     */
    @ApiOperation("根据手机号查询用户")
    @GetMapping("/phone/{phone}")
    public Result<User> getUserByPhone(@ApiParam("手机号") @PathVariable String phone) {
        User user = userService.getUserByPhone(phone);
        return Result.success(user);
    }

    /**
     * 添加用户
     */
    @ApiOperation("添加用户")
    @PostMapping
    public Result<Void> addUser(@ApiParam("用户信息") @RequestBody User user) {
        userService.addUser(user);
        return Result.success("添加用户成功");
    }

    /**
     * 更新用户
     */
    @ApiOperation("更新用户")
    @PutMapping
    public Result<Void> updateUser(@ApiParam("用户信息") @RequestBody User user) {
        userService.updateUser(user);
        return Result.success("更新用户成功");
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@ApiParam("用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@ApiParam("注册信息") @RequestBody RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole("user"); // 默认普通用户角色
        
        userService.register(user);
        return Result.success("注册成功");
    }

    /**
     * 用户登录（集成JWT）
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@ApiParam("登录信息") @RequestBody LoginDTO loginDTO) {
        // 验证用户名密码
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        
        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);
        
        log.info("用户登录成功：{}", user.getUsername());
        
        return Result.success("登录成功", data);
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password/change")
    public Result<Void> changePassword(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("原密码") @RequestParam String oldPassword,
            @ApiParam("新密码") @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("修改密码成功");
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PutMapping("/password/reset")
    public Result<Void> resetPassword(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("新密码") @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success("重置密码成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            log.info("=== 获取当前用户信息 ===");
            
            // 验证token是否存在
            if (token == null || token.isEmpty()) {
                log.warn("Authorization header is missing");
                return Result.error("未登录或登录已过期，请重新登录");
            }
            
            // 从token中解析用户信息
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            log.info("Parsing JWT token...");
            
            Long userId = JwtUtil.getUserIdFromToken(jwtToken);
            log.info("User ID from token: {}", userId);
            
            if (userId == null) {
                log.warn("Failed to parse user ID from token");
                return Result.error("无效的token，请重新登录");
            }
            
            User user = userService.getUserById(userId);
            log.info("Found user: {}", user != null ? user.getUsername() : "null");
            
            if (user == null) {
                log.warn("User not found for ID: {}", userId);
                return Result.error("用户不存在");
            }
            
            // 清除密码字段
            user.setPassword(null);
            
            log.info("Successfully retrieved user info for: {}", user.getUsername());
            return Result.success(user);
        } catch (BusinessException e) {
            log.error("Business exception while getting current user: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while getting current user", e);
            return Result.error("系统错误，请稍后重试");
        }
    }

    /**
     * 更新当前用户个人信息
     */
    @ApiOperation("更新当前用户个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @RequestHeader("Authorization") String token,
            @ApiParam("用户信息") @RequestBody User user) {
        try {
            log.info("=== 更新用户个人信息 ===");
            
            // 从token中解析用户ID
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = JwtUtil.getUserIdFromToken(jwtToken);
            
            if (userId == null) {
                log.warn("Failed to parse user ID from token");
                return Result.error("无效的token");
            }
            
            user.setId(userId);
            log.info("Updating user profile for user ID: {}", userId);
            
            // 记录要更新的字段
            if (user.getAvatar() != null) {
                log.info("Updating avatar to: {}", user.getAvatar());
            }
            if (user.getRealName() != null) {
                log.info("Updating real name to: {}", user.getRealName());
            }
            
            userService.updateUser(user);
            log.info("Successfully updated user profile for user ID: {}", userId);
            
            return Result.success("更新个人信息成功");
        } catch (BusinessException e) {
            log.error("Business exception while updating profile: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating profile", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 修改当前用户密码
     */
    @ApiOperation("修改当前用户密码")
    @PutMapping("/change-password")
    public Result<Void> changeCurrentPassword(
            @RequestHeader("Authorization") String token,
            @ApiParam("密码信息") @RequestBody Map<String, String> passwordData) {
        // 从token中解析用户ID
        String jwtToken = token.replace("Bearer ", "");
        Long userId = JwtUtil.getUserIdFromToken(jwtToken);
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("修改密码成功");
    }

}



