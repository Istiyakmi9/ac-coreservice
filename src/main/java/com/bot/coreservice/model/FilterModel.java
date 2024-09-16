package com.bot.coreservice.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class FilterModel {
    boolean isActive;

    String searchString ;

    int pageIndex;

    int pageSize;

    String sortBy;

    int companyId;

    int offsetIndex;

    Long employeeId;
}
