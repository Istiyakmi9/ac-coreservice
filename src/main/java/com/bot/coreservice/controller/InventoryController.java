package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.ICDProductInvestmentService;
import com.bot.coreservice.entity.CDProductInvestment;
import com.bot.coreservice.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/inventory/")
public class InventoryController {
    @Autowired
    ICDProductInvestmentService ICDProductInvestmentService;

    @PostMapping("addInventory")
    public ResponseEntity<ApiResponse> addInventory(@RequestBody CDProductInvestment cdProductInvestment) throws Exception {
        var result = ICDProductInvestmentService.addCDProductInvestmentService(cdProductInvestment);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getInventory/{userId}")
    public ResponseEntity<ApiResponse> getInventory(@PathVariable long userId) throws Exception {
        var result = ICDProductInvestmentService.getCDProductInvestmentService(userId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
