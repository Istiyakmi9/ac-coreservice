package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IInventoryService;
import com.bot.coreservice.entity.InventoryDetail;
import com.bot.coreservice.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/inventory/")
public class InventoryController {
    @Autowired
    IInventoryService iInventoryService;

    @PostMapping("addInventory")
    public ResponseEntity<ApiResponse> addInventory(@RequestBody InventoryDetail inventoryDetail) throws Exception {
        var result = iInventoryService.addInventoryService(inventoryDetail);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
