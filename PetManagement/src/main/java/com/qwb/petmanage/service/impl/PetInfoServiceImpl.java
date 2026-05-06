
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.PetInfo;
import com.qwb.petmanage.mapper.PetInfoMapper;
import com.qwb.petmanage.service.PetInfoService;
import org.springframework.stereotype.Service;

@Service
public class PetInfoServiceImpl extends ServiceImpl<PetInfoMapper, PetInfo> implements PetInfoService {
}
