
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.entity.KnowledgeComment;
import com.qwb.petmanage.entity.PetKnowledge;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.AdminService;
import com.qwb.petmanage.service.KnowledgeCommentService;
import com.qwb.petmanage.service.PetKnowledgeService;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物知识控制器
 */
@RestController
@RequestMapping("/pet-knowledge")
public class PetKnowledgeController {

    @Autowired
    private PetKnowledgeService petKnowledgeService;

    @Autowired
    private KnowledgeCommentService knowledgeCommentService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    /**
     * 管理员发布知识文章
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody PetKnowledge petKnowledge) {
        petKnowledge.setViewCount(0);
        petKnowledgeService.save(petKnowledge);
        return Result.success();
    }

    /**
     * 管理员更新知识文章
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody PetKnowledge petKnowledge) {
        petKnowledgeService.updateById(petKnowledge);
        return Result.success();
    }

    /**
     * 管理员删除知识文章
     */
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 同时删除该文章下的所有评论
        QueryWrapper<KnowledgeComment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("knowledge_id", id);
        knowledgeCommentService.remove(commentQuery);
        petKnowledgeService.removeById(id);
        return Result.success();
    }

    /**
     * 管理员分页查询知识文章列表
     */
    @GetMapping("/page")
    public Result<Page<PetKnowledge>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String category,
                                            @RequestParam(required = false) String title) {
        Page<PetKnowledge> page = new Page<>(current, size);
        QueryWrapper<PetKnowledge> queryWrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("create_time");
        Page<PetKnowledge> result = petKnowledgeService.page(page, queryWrapper);
        // 填充作者名和评论数
        for (PetKnowledge k : result.getRecords()) {
            fillKnowledgeExtra(k);
        }
        return Result.success(result);
    }

    /**
     * 用户端获取知识文章列表（分页）
     */
    @GetMapping("/list")
    public Result<Page<PetKnowledge>> list(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String category) {
        Page<PetKnowledge> page = new Page<>(current, size);
        QueryWrapper<PetKnowledge> queryWrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        queryWrapper.orderByDesc("create_time");
        Page<PetKnowledge> result = petKnowledgeService.page(page, queryWrapper);
        for (PetKnowledge k : result.getRecords()) {
            fillKnowledgeExtra(k);
        }
        return Result.success(result);
    }

    /**
     * 获取知识文章详情（浏览量+1，管理员查看不增加）
     */
    @GetMapping("/detail/{id}")
    public Result<PetKnowledge> detail(@PathVariable Long id,
                                        @RequestParam(required = false) Boolean noCount) {
        PetKnowledge knowledge = petKnowledgeService.getById(id);
        if (knowledge == null) {
            return Result.error("文章不存在");
        }
        // 管理员查看不增加浏览量
        if (noCount == null || !noCount) {
            knowledge.setViewCount(knowledge.getViewCount() + 1);
            petKnowledgeService.updateById(knowledge);
        }
        fillKnowledgeExtra(knowledge);
        return Result.success(knowledge);
    }

    /**
     * 用户提交评论
     */
    @PostMapping("/comment/add")
    public Result<Void> addComment(@RequestBody KnowledgeComment comment) {
        knowledgeCommentService.save(comment);
        return Result.success();
    }

    /**
     * 获取文章的评论列表
     */
    @GetMapping("/comment/list/{knowledgeId}")
    public Result<List<KnowledgeComment>> commentList(@PathVariable Long knowledgeId) {
        QueryWrapper<KnowledgeComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("knowledge_id", knowledgeId).orderByAsc("create_time");
        List<KnowledgeComment> comments = knowledgeCommentService.list(queryWrapper);
        // 填充用户名和头像
        for (KnowledgeComment c : comments) {
            if (c.getUserId() != null) {
                User user = userService.getById(c.getUserId());
                if (user != null) {
                    c.setUserName(user.getRealName());
                    c.setUserAvatar(user.getAvatar());
                }
            }
        }
        return Result.success(comments);
    }

    /**
     * 删除评论
     */
    @PostMapping("/comment/delete/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        knowledgeCommentService.removeById(id);
        return Result.success();
    }

    /**
     * 填充知识文章的作者名和评论数
     */
    private void fillKnowledgeExtra(PetKnowledge k) {
        if (k.getAuthorId() != null) {
            Admin admin = adminService.getById(k.getAuthorId());
            if (admin != null) {
                k.setAuthorName(admin.getRealName());
            }
        }
        QueryWrapper<KnowledgeComment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("knowledge_id", k.getKnowledgeId());
        k.setCommentCount((int) knowledgeCommentService.count(commentQuery));
    }
}
