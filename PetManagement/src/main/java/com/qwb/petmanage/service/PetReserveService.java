
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.PetReserve;

public interface PetReserveService extends IService<PetReserve> {

    Page<PetReserve> pageList(Integer current, Integer size, String reserveStatus, Long userId, Long petId, String realName);

    /**
     * 提交预约
     * @param petReserve 预约信息
     */
    void submitReserve(PetReserve petReserve);

    /**
     * 确认预约
     * @param reserveId 预约ID
     * @param confirmAdmin 确认管理员ID
     */
    void confirmReserve(Long reserveId, Long confirmAdmin);

    /**
     * 取消预约
     * @param id 预约ID
     * @param cancelReason 取消原因
     */
    void cancelReserve(Long id, String cancelReason);

    /**
     * 完成预约
     * @param id 预约ID
     */
    void completeReserve(Long id);
}
