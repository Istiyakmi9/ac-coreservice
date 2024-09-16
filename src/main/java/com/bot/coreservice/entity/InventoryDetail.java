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
@Table(name="inventory_detail")
public class InventoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long inventoryId;

    String inventoryName;

    long userId;

    double emiAmount;

    double onRoadPrice;

    int months;

    double downPayment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date emiStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date emiEndDate;

    double loanAmount;

    double totalPayableAmount;

    long createdBy;

    long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;
}
