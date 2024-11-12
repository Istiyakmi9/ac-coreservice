package com.bot.coreservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_detail")
public class PaymentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("paymentDetailId")
    int paymentDetailId;

    @JsonProperty("investmentCategoryTypeId")
    int investmentCategoryTypeId;

    @JsonProperty("investmentId")
    long investmentId;

    @JsonProperty("amount")
    double amount;

    @JsonProperty("isPaid")
    boolean isPaid;

    @JsonProperty("installmentNumber")
    int installmentNumber;

    @JsonProperty("paymentDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date paymentDate;
}
