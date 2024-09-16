package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.InventoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryDetail, Long> {
}
