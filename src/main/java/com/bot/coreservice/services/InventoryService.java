package com.bot.coreservice.services;

import com.bot.coreservice.Repository.InventoryRepository;
import com.bot.coreservice.contracts.IInventoryService;
import com.bot.coreservice.entity.InventoryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class InventoryService implements IInventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    public InventoryDetail addInventoryService(InventoryDetail inventoryDetail) throws Exception {
        validateInventoryDetail(inventoryDetail);

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());
        inventoryDetail.setCreatedBy(1L);
        inventoryDetail.setUpdatedBy(1L);
        inventoryDetail.setCreatedOn(currentDate);
        inventoryDetail.setUpdatedOn(currentDate);

        inventoryRepository.save(inventoryDetail);

        return inventoryDetail;
    }

    public List<InventoryDetail> getInventoryService(long userId) throws Exception {
        if (userId == 0)
            throw new Exception("Invalid user");

        return inventoryRepository.getInventoryByUserId(userId);
    }

    private void validateInventoryDetail(InventoryDetail inventoryDetail) throws Exception {
        if (inventoryDetail.getInventoryName() == null || inventoryDetail.getInventoryName().isEmpty())
            throw new Exception("Inventory name is invalid");

        if (inventoryDetail.getUserId() == 0)
            throw new Exception("Invalid user selected");

        if (inventoryDetail.getEmiAmount() == 0)
            throw new Exception("Invalid emi amount");

        if (inventoryDetail.getOnRoadPrice() == 0)
            throw new Exception("Invalid on road amount");

        if (inventoryDetail.getMonths() == 0)
            throw new Exception("Invalid no of months");

        if (inventoryDetail.getDownPayment() == 0)
            throw new Exception("Invalid down payment");

        if (inventoryDetail.getEmiEndDate() == null)
            throw new Exception("Invalid emi start date");

        double totalPayableAmount = inventoryDetail.getEmiAmount() * inventoryDetail.getMonths();
        if (totalPayableAmount != inventoryDetail.getTotalPayableAmount())
            throw new Exception("Total payable amount calculation is not match");

        double loanAmount = inventoryDetail.getOnRoadPrice() - inventoryDetail.getDownPayment();
        if (loanAmount != inventoryDetail.getLoanAmount())
            throw new Exception("Invalid loan amount calculation");

        if (inventoryDetail.getPercentage() == 0)
            throw new Exception("Percentage value is null");
    }
}
