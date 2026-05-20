
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.KnowledgeComment;
import com.qwb.petmanage.mapper.KnowledgeCommentMapper;
import com.qwb.petmanage.service.KnowledgeCommentService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeCommentServiceImpl extends ServiceImpl<KnowledgeCommentMapper, KnowledgeComment> implements KnowledgeCommentService {
}
