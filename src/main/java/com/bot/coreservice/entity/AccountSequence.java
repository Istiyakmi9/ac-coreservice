package com.bot.coreservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="account_num_sequence")
public class AccountSequence {
    @Id
    int id;

    int lastSequenceNumber;
}
