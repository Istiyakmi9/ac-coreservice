package com.bot.coreservice.services;

import com.bot.coreservice.Repository.InvestmentRepository;
import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.entity.InvestmentDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class InvestmentService implements IInvestmentService {
    @Autowired
    InvestmentRepository investmentRepository;

    public InvestmentDetail addInvestmentService(InvestmentDetail investmentDetail) throws Exception {
        validateInvestmentDetail(investmentDetail);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());
        investmentDetail.setCreatedBy(1L);
        investmentDetail.setUpdatedBy(1L);
        investmentDetail.setCreatedOn(currentDate);
        investmentDetail.setUpdatedOn(currentDate);
        investmentDetail.setInvestmentDate(currentDate);

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
}
