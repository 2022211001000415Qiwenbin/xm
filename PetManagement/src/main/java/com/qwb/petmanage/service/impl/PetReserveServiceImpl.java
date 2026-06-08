
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

    @Override
    public void submitReserve(PetReserve petReserve) {
        petReserve.setReserveStatus("待确认");
        petReserve.setCreateTime(java.time.LocalDateTime.now());
        save(petReserve);
    }

    @Override
    public void confirmReserve(Long reserveId, Long confirmAdmin) {
        PetReserve petReserve = getById(reserveId);
        if (petReserve == null) {
            throw new RuntimeException("预约记录不存在");
        }
        petReserve.setReserveStatus("已确认");
        petReserve.setConfirmAdmin(confirmAdmin);
        petReserve.setUpdateTime(java.time.LocalDateTime.now());
        updateById(petReserve);
    }

    @Override
    public void cancelReserve(Long id, String cancelReason) {
        PetReserve petReserve = getById(id);
        if (petReserve == null) {
            throw new RuntimeException("预约记录不存在");
        }
        petReserve.setReserveStatus("已取消");
        petReserve.setReserveRemark(cancelReason);
        petReserve.setUpdateTime(java.time.LocalDateTime.now());
        updateById(petReserve);
    }

    @Override
    public void completeReserve(Long id) {
        PetReserve petReserve = getById(id);
        if (petReserve == null) {
            throw new RuntimeException("预约记录不存在");
        }
        petReserve.setReserveStatus("已完成");
        petReserve.setUpdateTime(java.time.LocalDateTime.now());
        updateById(petReserve);
    }
}
