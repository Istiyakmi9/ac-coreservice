package com.bot.coreservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CDProductInvestmentDTO {
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

    String accountId;

    int paidInstallment;

    int total;

    String firstName;

    String lastName;

    String mobileNumber;

    @JsonProperty("isPaid")
    boolean isPaid;

    int rowIndex;
}
