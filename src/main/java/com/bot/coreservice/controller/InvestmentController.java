package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.entity.InvestmentType;
import com.bot.coreservice.model.ApiResponse;
import com.bot.coreservice.model.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/investment/")
public class InvestmentController {
    @Autowired
    IInvestmentService iInvestmentService;

    @GetMapping("getInvestment/{userId}")
    public ResponseEntity<ApiResponse> getInvestment(@PathVariable long userId) throws Exception {
        var result = iInvestmentService.getInvestmentService(userId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getAllInvestmentType")
    public ResponseEntity<ApiResponse> getAllInvestmentType(){
        var result = iInvestmentService.getAllInvestmentTypeService();
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addInvestmentType")
    public ResponseEntity<ApiResponse> addInvestmentType(@RequestBody InvestmentType investmentType) throws Exception {
        var result = iInvestmentService.addInvestmentTypeService(investmentType);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("dailyTransaction")
    public ResponseEntity<ApiResponse> dailyTransaction(@RequestBody FilterModel filterModel) throws Exception {
        var result = iInvestmentService.dailyTransactionService(filterModel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("payInvestmentAmount/{investmentId}")
    public ResponseEntity<ApiResponse> payInvestmentAmount(@PathVariable long investmentId) throws Exception {
        var result = iInvestmentService.payInvestmentAmountService(investmentId);
        return  ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
