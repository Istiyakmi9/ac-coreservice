package com.bot.coreservice.services;

import com.bot.coreservice.Repository.CDProductInvestmentRepository;
import com.bot.coreservice.contracts.ICDProductInvestmentService;
import com.bot.coreservice.entity.CDProductInvestment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class CDProductInvestmentService implements ICDProductInvestmentService {
    @Autowired
    CDProductInvestmentRepository CDProductInvestmentRepository;

    public CDProductInvestment addCDProductInvestmentService(CDProductInvestment cdProductInvestment) throws Exception {
        validatecdProductInvestment(cdProductInvestment);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());
        cdProductInvestment.setCreatedBy(1L);
        cdProductInvestment.setUpdatedBy(1L);
        cdProductInvestment.setCreatedOn(currentDate);
        cdProductInvestment.setUpdatedOn(currentDate);

        CDProductInvestmentRepository.save(cdProductInvestment);

        return cdProductInvestment;
    }

    public List<CDProductInvestment> getCDProductInvestmentService(long userId) throws Exception {
        if (userId == 0)
            throw new Exception("Invalid user");

        return CDProductInvestmentRepository.getCDProductInvestmentByUserId(userId);
    }

    private void validatecdProductInvestment(CDProductInvestment cdProductInvestment) throws Exception {
        if (cdProductInvestment.getProductName() == null || cdProductInvestment.getProductName().isEmpty())
            throw new Exception("Inventory name is invalid");

        if (cdProductInvestment.getUserId() == 0)
            throw new Exception("Invalid user selected");

        if (cdProductInvestment.getEmiAmount() == 0)
            throw new Exception("Invalid emi amount");

        if (cdProductInvestment.getFinalPrice() == 0)
            throw new Exception("Invalid final amount");

        if (cdProductInvestment.getPeriod() == 0)
            throw new Exception("Invalid no of period");

        if (cdProductInvestment.getDownPayment() == 0)
            throw new Exception("Invalid down payment");

        if (cdProductInvestment.getEmiEndDate() == null)
            throw new Exception("Invalid emi start date");

        double totalPayableAmount = cdProductInvestment.getEmiAmount() * cdProductInvestment.getPeriod();
        if (totalPayableAmount != cdProductInvestment.getTotalPayableAmount())
            throw new Exception("Total payable amount calculation is not match");

        double loanAmount = cdProductInvestment.getFinalPrice() - cdProductInvestment.getDownPayment();
        if (loanAmount != cdProductInvestment.getLoanAmount())
            throw new Exception("Invalid loan amount calculation");

        if (cdProductInvestment.getPercentage() == 0)
            throw new Exception("Percentage value is null");
    }
}
