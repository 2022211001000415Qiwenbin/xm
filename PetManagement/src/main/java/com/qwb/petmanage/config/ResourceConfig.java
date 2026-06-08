
package com.qwb.petmanage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置
 * 图片已迁移至阿里云OSS存储，不再需要本地文件映射
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {
}
