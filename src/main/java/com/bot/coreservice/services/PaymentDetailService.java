package com.bot.coreservice.services;

import com.bot.coreservice.contracts.IPaymentDetailService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.PaymentDetail;
import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.InvestmentDetailDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentDetailService implements IPaymentDetailService {
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<PaymentDetail> getPaymentDetailService(long investmentId, int investmentCategoryTypeId) throws Exception {
        if (investmentId == 0)
            throw new Exception("Invalid investment id");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_investmentId", investmentId, Types.BIGINT));
        dbParameters.add(new DbParameters("_investmentCategoryTypeId", investmentCategoryTypeId, Types.INTEGER));

        var dataSet = lowLevelExecution.executeProcedure("sp_payment_detail_by_id_category_type", dbParameters);

        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<PaymentDetail>>() {
        });
    }
}
