package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IPaymentDetailService;
import com.bot.coreservice.model.ApiResponse;
import com.bot.coreservice.model.ApplicationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/paymentDetail/")
public class PaymentDetailController {
    @Autowired
    IPaymentDetailService iPaymentDetailService;

    @GetMapping("getInvestmentPaymentDetail/{investmentId}")
    public ResponseEntity<ApiResponse> getInvestmentPaymentDetail(@PathVariable long investmentId) throws Exception {
        var result = iPaymentDetailService.getPaymentDetailService(investmentId, ApplicationConstant.InvestmentByUser);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getCDPaymentDetail/{cdProductId}")
    public ResponseEntity<ApiResponse> getCDPaymentDetail(@PathVariable long cdProductId) throws Exception {
        var result = iPaymentDetailService.getPaymentDetailService(cdProductId, ApplicationConstant.CDProductByUser);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
