
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.PetKnowledge;
import com.qwb.petmanage.mapper.PetKnowledgeMapper;
import com.qwb.petmanage.service.PetKnowledgeService;
import org.springframework.stereotype.Service;

@Service
public class PetKnowledgeServiceImpl extends ServiceImpl<PetKnowledgeMapper, PetKnowledge> implements PetKnowledgeService {
}
