package com.bot.coreservice.entity;

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
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("userId")
    Long userId;

    @JsonProperty("firstName")
    String firstName;

    @JsonProperty("lastName")
    String lastName;

    @JsonProperty("address")
    String address;

    @JsonProperty("mobileNumber")
    String mobileNumber;

    @JsonProperty("aadharNumber")
    String aadharNumber;

    @JsonProperty("emailId")
    String emailId;

    @JsonProperty("referenceBy")
    String referenceBy;

    @JsonProperty("dob")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date dob;

    @JsonProperty("createdBy")
    Long createdBy;

    @JsonProperty("updatedBy")
    Long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("createdOn")
    Date createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("updatedOn")
    Date updatedOn;

    @JsonProperty("accountId")
    String accountId;

    @Transient
    int total;

    @Transient
    int productType;
}
