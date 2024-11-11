package com.bot.coreservice.model;
import com.bot.coreservice.entity.PaymentDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDetailDTO {
    long investmentId;

    long userId;

    double investmentAmount;

    double addOn;

    double principalAmount;

    double profitAmount;

    int period;

    double totalProfitAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date investmentDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date istPaymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date lastPaymentDate;

    long createdBy;

    long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;

    int paidInstallment;

    String firstName;

    String lastName;

    int total;

    String accountId;

    String mobileNumber;

    List<PaymentDetail> paymentDetail;

    @JsonProperty("isPaid")
    boolean isPaid;

    int rowIndex;
}
