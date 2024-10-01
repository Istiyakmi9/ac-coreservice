package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.InventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryDetail, Long> {
    @Query(value = "select i.* from inventory_detail i where i.userId = :userId", nativeQuery = true)
    List<InventoryDetail> getInventoryByUserId(@Param("userId") Long userId);
}
