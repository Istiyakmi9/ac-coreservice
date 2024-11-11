package com.bot.coreservice.services;

import com.bot.coreservice.Repository.CDProductInvestmentRepository;
import com.bot.coreservice.Repository.PaymentDetailRepository;
import com.bot.coreservice.contracts.ICDProductInvestmentService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.CDProductInvestment;
import com.bot.coreservice.entity.InvestmentDetail;
import com.bot.coreservice.entity.PaymentDetail;
import com.bot.coreservice.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CDProductInvestmentService implements ICDProductInvestmentService {
    @Autowired
    CDProductInvestmentRepository CDProductInvestmentRepository;
    @Autowired
    PaymentDetailRepository paymentDetailRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;

    public CDProductInvestment addCDProductInvestmentService(CDProductInvestment cdProductInvestment) throws Exception {
        validatecdProductInvestment(cdProductInvestment);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());
        cdProductInvestment.setCreatedBy(1L);
        cdProductInvestment.setUpdatedBy(1L);
        cdProductInvestment.setCreatedOn(currentDate);
        cdProductInvestment.setUpdatedOn(currentDate);

        cdProductInvestment = CDProductInvestmentRepository.save(cdProductInvestment);

        var paymentDetails = addPaymentDetail(cdProductInvestment);
        return cdProductInvestment;
    }

    private  List<PaymentDetail> addPaymentDetail(CDProductInvestment cdProductInvestment) {
        List<PaymentDetail> paymentDetails = new ArrayList<>();

        Date paymentDate = cdProductInvestment.getEmiStartDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(paymentDate);

        for (int i = 0; i < cdProductInvestment.getPeriod(); i++) {
            Calendar paymentCalendar = (Calendar) calendar.clone();
            paymentCalendar.add(Calendar.MONTH, i);

            Date newPaymentDate = paymentCalendar.getTime();

            paymentDetails.add(PaymentDetail.builder()
                    .paymentDetailId(0)
                    .investmentCategoryTypeId(ApplicationConstant.CDProductByUser)
                    .investmentId(cdProductInvestment.getCdProductId())
                    .installmentNumber(i + 1)
                    .amount(cdProductInvestment.getEmiAmount())
                    .paymentDate(newPaymentDate)
                    .isPaid((i + 1) <= cdProductInvestment.getPaidInstallment() ? true : false)
                    .build());
        }

        paymentDetails = paymentDetailRepository.saveAll(paymentDetails);

        return paymentDetails;
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

    @Transactional(rollbackFor = Exception.class)
    public List<CDProductInvestmentDTO> payCDProductEMIService(long cdProductId) throws Exception {
        if (cdProductId == 0)
            throw new Exception("Invalid investment id");

        var cdProductDetail = CDProductInvestmentRepository.findById(cdProductId).orElseThrow(() -> new Exception("CD product detail not found"));

        var paymentDetail = findAndUpdateCurrentPayment(cdProductId, cdProductDetail.getPaidInstallment() + 1);

        cdProductDetail.setPaidInstallment(paymentDetail.getInstallmentNumber());

        CDProductInvestmentRepository.save(cdProductDetail);

        var filterModel = FilterModel.builder()
                .searchString("1=1")
                .pageIndex(1)
                .pageSize(10)
                .build();

        return dailyCDProductTransactionService(filterModel);
    }

    private PaymentDetail findAndUpdateCurrentPayment(long cdProductId, int payInstallment) throws Exception {
        var paymentDetails = paymentDetailRepository.getPaymentDetailByInvId(cdProductId, ApplicationConstant.CDProductByUser);
        if (paymentDetails == null || paymentDetails.isEmpty())
            throw new Exception("Payment detail not found");

        var currentPayment = paymentDetails.stream().filter(x -> x.getInstallmentNumber() == payInstallment)
                .findFirst()
                .orElseThrow(() -> new Exception("Installment detail not found"));

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        currentPayment.setPaid(true);
        currentPayment.setPaymentDate(currentDate);

        paymentDetailRepository.save(currentPayment);

        return currentPayment;
    }

    public List<CDProductInvestmentDTO> dailyCDProductTransactionService(FilterModel filterModel) {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_searchString", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageIndex", filterModel.getPageIndex(), Types.INTEGER));
        dbParameters.add(new DbParameters("_pageSize", filterModel.getPageSize(), Types.INTEGER));
        dbParameters.add(new DbParameters("_sortBy", filterModel.getSortBy(), Types.VARCHAR));

        var dataSet = lowLevelExecution.executeProcedure("sp_cd_product_by_filter", dbParameters);

        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<CDProductInvestmentDTO>>() {
        });
    }
}
