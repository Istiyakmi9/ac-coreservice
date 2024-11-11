package com.bot.coreservice.services;

import com.bot.coreservice.Repository.InvestmentRepository;
import com.bot.coreservice.Repository.InvestmentTypeRepository;
import com.bot.coreservice.Repository.PaymentDetailRepository;
import com.bot.coreservice.contracts.IInvestmentService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.InvestmentDetail;
import com.bot.coreservice.entity.InvestmentType;
import com.bot.coreservice.entity.PaymentDetail;
import com.bot.coreservice.model.ApplicationConstant;
import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.FilterModel;
import com.bot.coreservice.model.InvestmentDetailDTO;
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
public class InvestmentService implements IInvestmentService {
    @Autowired
    InvestmentRepository investmentRepository;
    @Autowired
    InvestmentTypeRepository investmentTypeRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PaymentDetailRepository paymentDetailRepository;

    @Transactional(rollbackFor = Exception.class)
    public InvestmentDetailDTO addInvestmentService(InvestmentDetail investmentDetail) throws Exception {
        validateInvestmentDetail(investmentDetail);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        investmentDetail.setCreatedBy(1L);
        investmentDetail.setUpdatedBy(1L);
        investmentDetail.setCreatedOn(currentDate);
        investmentDetail.setUpdatedOn(currentDate);
        investmentDetail.setLastPaymentAmount(0);

        if (investmentDetail.getInvestmentDate() == null) {
            investmentDetail.setInvestmentDate(currentDate);
        }

        investmentDetail = investmentRepository.save(investmentDetail);

        var paymentDetails = addPaymentDetail(investmentDetail);

        InvestmentDetailDTO investmentDetailDTO = objectMapper.convertValue(investmentDetail, InvestmentDetailDTO.class);
        investmentDetailDTO.setPaymentDetail(paymentDetails);

        return investmentDetailDTO;
    }

    private  List<PaymentDetail> addPaymentDetail(InvestmentDetail investmentDetail) {
        List<PaymentDetail> paymentDetails = new ArrayList<>();

        Date paymentDate = investmentDetail.getIstPaymentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(paymentDate);

        for (int i = 0; i < investmentDetail.getPeriod(); i++) {
            Calendar paymentCalendar = (Calendar) calendar.clone();
            paymentCalendar.add(Calendar.MONTH, i);

            Date newPaymentDate = paymentCalendar.getTime();

            paymentDetails.add(PaymentDetail.builder()
                            .paymentDetailId(0)
                            .investmentCategoryTypeId(ApplicationConstant.InvestmentByUser)
                            .investmentId(investmentDetail.getInvestmentId())
                            .installmentNumber(i + 1)
                            .amount(investmentDetail.getTotalProfitAmount())
                            .paymentDate(newPaymentDate)
                            .isPaid((i + 1) <= investmentDetail.getPaidInstallment() ? true : false)
                            .build());
        }

        paymentDetails = paymentDetailRepository.saveAll(paymentDetails);

        return paymentDetails;
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

        if (investmentDetail.getPeriod() == 0)
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

    @Transactional(rollbackFor = Exception.class)
    public List<InvestmentDetailDTO> payInvestmentAmountService(long investmentId) throws Exception {
        if (investmentId == 0)
            throw new Exception("Invalid investment id");
        
        var investmentDetail = investmentRepository.findById(investmentId).orElseThrow(() -> new Exception("Investment detail not found"));

        var paymentDetail = findAndUpdateCurrentPayment(investmentId, investmentDetail.getPaidInstallment() + 1);

        investmentDetail.setPaidInstallment(paymentDetail.getInstallmentNumber());
        investmentDetail.setLastPaymentAmount(paymentDetail.getAmount());

        investmentRepository.save(investmentDetail);

        var filterModel = FilterModel.builder()
                .searchString("1=1")
                .pageIndex(1)
                .pageSize(10)
                .build();

        return dailyTransactionService(filterModel);
    }

    private PaymentDetail findAndUpdateCurrentPayment(long investmentId, int payInstallment) throws Exception {
        var paymentDetails = paymentDetailRepository.getPaymentDetailByInvId(investmentId, ApplicationConstant.InvestmentByUser);
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
}
