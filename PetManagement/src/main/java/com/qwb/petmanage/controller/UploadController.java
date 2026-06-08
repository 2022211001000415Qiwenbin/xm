
package com.qwb.petmanage.controller;

import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private OssService ossService;

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

        // 限制文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过5MB");
        }

        try {
            String fileUrl = ossService.uploadFile(file);
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
