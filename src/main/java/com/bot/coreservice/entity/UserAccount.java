package com.bot.coreservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    private String accountId;

    private BigDecimal totalInstallment;

    private BigDecimal installmentPaid;

    private int tenure; // in period

    private int tenurePaid; // in period

    private BigDecimal emi;
}
