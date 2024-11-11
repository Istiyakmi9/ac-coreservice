package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.InvestmentDetail;
import com.bot.coreservice.entity.InvestmentType;
import com.bot.coreservice.model.FilterModel;
import com.bot.coreservice.model.InvestmentDetailDTO;

import java.util.List;

public interface IInvestmentService {
    InvestmentDetailDTO addInvestmentService(InvestmentDetail inventoryDetail) throws Exception;

    List<InvestmentDetail> getInvestmentService(long userId) throws Exception;

    List<InvestmentType> addInvestmentTypeService(InvestmentType investmentType) throws Exception;

    List<InvestmentType> getAllInvestmentTypeService();

    List<InvestmentDetailDTO> dailyTransactionService(FilterModel filterModel);

    List<InvestmentDetailDTO> payInvestmentAmountService(long investmentId) throws Exception;
}
