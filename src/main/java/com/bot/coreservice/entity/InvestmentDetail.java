package com.bot.coreservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "investment_detail")
public class InvestmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    String paymentDetail;

    double lastPaymentAmount;

    String accountId;

    @Transient
    int month;

    @Transient
    int year;
}
