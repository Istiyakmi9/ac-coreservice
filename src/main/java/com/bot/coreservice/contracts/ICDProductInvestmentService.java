package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.CDProductInvestment;
import com.bot.coreservice.model.CDProductInvestmentDTO;
import com.bot.coreservice.model.FilterModel;

import java.util.List;

public interface ICDProductInvestmentService {
    CDProductInvestment addCDProductInvestmentService(CDProductInvestment cdProductInvestment) throws Exception;

    List<CDProductInvestment> getCDProductInvestmentService(long userId) throws Exception;

    List<CDProductInvestmentDTO> dailyCDProductTransactionService(FilterModel filterModel);

    List<CDProductInvestmentDTO> payCDProductEMIService(long cdProductId) throws Exception;
}
