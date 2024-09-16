package com.bot.coreservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserDetail {
    Long userId;

    String firstName;

    String lastName;

    String address;

    String mobileNumber;

    String alternateNumber;

    String emailId;

    Long referenceId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date dob;

    Long createdBy;

    Long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;

    String accountId;

    int fileId;

    String filePath;
}
