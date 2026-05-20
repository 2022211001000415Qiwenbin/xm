
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.PetReserve;

public interface PetReserveService extends IService<PetReserve> {

    Page<PetReserve> pageList(Integer current, Integer size, String reserveStatus, Long userId, Long petId, String realName);
}
