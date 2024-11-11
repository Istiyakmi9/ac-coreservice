package com.bot.coreservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "investment_category_type")
public class InvestmentCategoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int investmentCategoryTypeId;

    String typeDescription;

    long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;
}
