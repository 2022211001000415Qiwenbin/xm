
package com.qwb.petmanage.controller;

import com.qwb.petmanage.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${upload.path:D:/upload}")
    private String uploadPath;

    /**
     * 上传图片
     */
    @Operation(summary = "上传图片")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            return Result.error("只能上传图片文件");
        }

        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);

            // 返回文件访问路径
            String fileUrl = "http://localhost:8080/pet-api/images/" + filename;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
