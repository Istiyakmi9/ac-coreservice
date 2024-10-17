package com.bot.coreservice.services;

import com.bot.coreservice.Repository.InvestmentRepository;
import com.bot.coreservice.Repository.InvestmentTypeRepository;
import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.InvestmentDetail;
import com.bot.coreservice.entity.InvestmentType;
import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.FilterModel;
import com.bot.coreservice.model.InvestmentDetailDTO;
import com.bot.coreservice.model.MenuAndPermission;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvestmentService implements IInvestmentService {
    @Autowired
    InvestmentRepository investmentRepository;
    @Autowired
    InvestmentTypeRepository investmentTypeRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;

    public InvestmentDetail addInvestmentService(InvestmentDetail investmentDetail) throws Exception {
        validateInvestmentDetail(investmentDetail);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());
        investmentDetail.setCreatedBy(1L);
        investmentDetail.setUpdatedBy(1L);
        investmentDetail.setCreatedOn(currentDate);
        investmentDetail.setUpdatedOn(currentDate);
        if (investmentDetail.getInvestmentDate() == null) {
            investmentDetail.setInvestmentDate(currentDate);
        }

        investmentRepository.save(investmentDetail);

        return investmentDetail;
    }

    public List<InvestmentDetail> getInvestmentService(long userId) throws Exception {
        if (userId == 0)
            throw new Exception("Invalid user");

        return investmentRepository.getInvestmentByUserId(userId);
    }

    private void validateInvestmentDetail(InvestmentDetail investmentDetail) throws Exception {
        if (investmentDetail.getUserId() == 0)
            throw new Exception("Invalid user");

        if (investmentDetail.getInvestmentAmount() == 0)
            throw new Exception("Invalid investment amount");

        if (investmentDetail.getPrincipalAmount() == 0)
            throw new Exception("Invalid principal amount");

        if (investmentDetail.getProfitAmount() == 0)
            throw new Exception("Invalid profit amount");

        if (investmentDetail.getMonths() == 0)
            throw new Exception("Invalid month");

        if (investmentDetail.getIstPaymentDate() == null)
            throw new Exception("Invalid first payment date");

        if (investmentDetail.getLastPaymentDate() == null)
            throw new Exception("Invalid last payment date");

        if (investmentDetail.getTotalProfitAmount() != (investmentDetail.getAddOn() + investmentDetail.getPrincipalAmount() + investmentDetail.getProfitAmount()))
            throw new Exception("Invalid total profit amount");
    }

    public List<InvestmentType> getAllInvestmentTypeService() {
        return  investmentTypeRepository.findAll();
    }

    public List<InvestmentType> addInvestmentTypeService(InvestmentType investmentType) throws Exception {
        if (investmentType.getAmount() == 0)
            throw new Exception("Invalid investment amount");

        if (investmentType.getMonth() == 0)
            throw new Exception("Invalid investment month");

        investmentTypeRepository.save(investmentType);

        return getAllInvestmentTypeService();
    }

    public List<InvestmentDetailDTO> dailyTransactionService(FilterModel filterModel) {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_searchString", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageIndex", filterModel.getPageIndex(), Types.INTEGER));
        dbParameters.add(new DbParameters("_pageSize", filterModel.getPageSize(), Types.INTEGER));
        dbParameters.add(new DbParameters("_sortBy", filterModel.getSortBy(), Types.VARCHAR));

        var dataSet = lowLevelExecution.executeProcedure("sp_daily_investment_by_filter", dbParameters);

        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<InvestmentDetailDTO>>() {
        });
    }
}
