
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.PetReserve;
import com.qwb.petmanage.mapper.PetReserveMapper;
import com.qwb.petmanage.service.PetReserveService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetReserveServiceImpl extends ServiceImpl<PetReserveMapper, PetReserve> implements PetReserveService {

    @Override
    public Page<PetReserve> pageList(Integer current, Integer size, String reserveStatus, Long userId, Long petId, String realName) {
        Page<PetReserve> page = new Page<>(current, size);
        List<PetReserve> records = baseMapper.selectReservePage(page, reserveStatus, userId, petId, realName);
        page.setRecords(records);
        return page;
    }
}
