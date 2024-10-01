package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.InventoryDetail;

import java.util.List;

public interface IInventoryService {
    InventoryDetail addInventoryService(InventoryDetail inventoryDetail) throws Exception;
    List<InventoryDetail> getInventoryService(long userId) throws Exception;
}
