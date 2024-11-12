package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.ICDProductInvestmentService;
import com.bot.coreservice.entity.CDProductInvestment;
import com.bot.coreservice.model.ApiResponse;
import com.bot.coreservice.model.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/cdproduct/")
public class CDProductController {
    @Autowired
    ICDProductInvestmentService ICDProductInvestmentService;

    @PostMapping("addCDProductInvestment")
    public ResponseEntity<ApiResponse> addCDProductInvestment(@RequestBody CDProductInvestment cdProductInvestment) throws Exception {
        var result = ICDProductInvestmentService.addCDProductInvestmentService(cdProductInvestment);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getCDProductInvestment/{userId}")
    public ResponseEntity<ApiResponse> getInventory(@PathVariable long userId) throws Exception {
        var result = ICDProductInvestmentService.getCDProductInvestmentService(userId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("dailyCDProductTransaction")
    public ResponseEntity<ApiResponse> dailyCdProductTransaction(@RequestBody FilterModel filterModel) throws Exception {
        var result = ICDProductInvestmentService.dailyCDProductTransactionService(filterModel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("payInvestmentAmount/{cdProductId}")
    public ResponseEntity<ApiResponse> payInvestmentAmount(@PathVariable long cdProductId) throws Exception {
        var result = ICDProductInvestmentService.payCDProductEMIService(cdProductId);
        return  ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
