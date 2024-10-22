package com.bot.coreservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetail {
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
