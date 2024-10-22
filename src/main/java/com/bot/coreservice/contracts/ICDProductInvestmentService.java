package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.CDProductInvestment;

import java.util.List;

public interface ICDProductInvestmentService {
    CDProductInvestment addCDProductInvestmentService(CDProductInvestment cdProductInvestment) throws Exception;

    List<CDProductInvestment> getCDProductInvestmentService(long userId) throws Exception;
}
