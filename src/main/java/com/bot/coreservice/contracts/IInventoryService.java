package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.InventoryDetail;

public interface IInventoryService {
    InventoryDetail addInventoryService(InventoryDetail inventoryDetail) throws Exception;
}
