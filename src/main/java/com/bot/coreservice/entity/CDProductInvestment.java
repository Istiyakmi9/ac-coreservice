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
@Table(name="cd_products_investment")
public class CDProductInvestment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long cdProductId;

    String productName;

    long userId;

    double emiAmount;

    double finalPrice;

    int period;

    double downPayment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date emiStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date emiEndDate;

    double loanAmount;

    double totalPayableAmount;

    double percentage;

    long createdBy;

    long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;

    @Transient
    String accountId;
}
