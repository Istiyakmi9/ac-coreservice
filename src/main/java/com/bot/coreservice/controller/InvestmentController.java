package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
