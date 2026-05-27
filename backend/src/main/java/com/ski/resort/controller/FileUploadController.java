package com.ski.resort.controller;

import com.ski.resort.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/api/v1/upload")
@Slf4j
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 上传图片文件
     */
    @ApiOperation("上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(
            @ApiParam("图片文件") @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 检查文件大小（限制5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过5MB");
        }

        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String fullUploadPath = uploadDir + File.separator + datePath;
            File uploadPath = new File(fullUploadPath);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(fullUploadPath, newFilename);
            Files.write(filePath, file.getBytes());

            // 返回访问URL（相对路径）
            String fileUrl = "/" + uploadDir + "/" + datePath.replace("\\", "/") + "/" + newFilename;
            
            log.info("文件上传成功: {}", fileUrl);
            // 使用明确的方法：success(message, data) 确保 data 字段有值
            return Result.success("上传成功", fileUrl);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 通过URL保存图片
     */
    @ApiOperation("保存图片URL")
    @PostMapping("/save-url")
    public Result<String> saveImageUrl(
            @ApiParam("图片URL") @RequestParam("imageUrl") String imageUrl) {
        
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return Result.error("图片URL不能为空");
        }

        // 简单验证URL格式
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://") && !imageUrl.startsWith("/")) {
            return Result.error("无效的图片URL");
        }

        // 直接返回URL（不进行下载保存）
        return Result.success(imageUrl);
    }

    /**
     * 删除图片
     */
    @ApiOperation("删除图片")
    @DeleteMapping("/image")
    public Result<Void> deleteImage(
            @ApiParam("图片URL") @RequestParam("imageUrl") String imageUrl) {
        
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return Result.error("图片URL不能为空");
        }

        // 只删除本地上传的文件
        if (!imageUrl.startsWith("http")) {
            try {
                File file = new File(imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl);
                if (file.exists() && file.delete()) {
                    log.info("文件删除成功: {}", imageUrl);
                    return Result.success();
                }
            } catch (Exception e) {
                log.error("文件删除失败", e);
                return Result.error("文件删除失败: " + e.getMessage());
            }
        }

        return Result.success();
    }
}

