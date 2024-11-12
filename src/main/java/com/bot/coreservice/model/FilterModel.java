package com.bot.coreservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
