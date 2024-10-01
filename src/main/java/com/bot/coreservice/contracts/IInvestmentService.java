package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.InvestmentDetail;

import java.util.List;

public interface IInvestmentService {
    InvestmentDetail addInvestmentService(InvestmentDetail inventoryDetail) throws Exception;

    List<InvestmentDetail> getInvestmentService(long userId) throws Exception;
}
