package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.PaymentDetail;

import java.util.List;

public interface IPaymentDetailService {
    List<PaymentDetail> getPaymentDetailService(long investmentId, int investmentCategoryTypeId) throws Exception;
}
