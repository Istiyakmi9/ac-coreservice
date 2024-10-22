package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.CDProductInvestment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CDProductInvestmentRepository extends JpaRepository<CDProductInvestment, Long> {
    @Query(value = "select i.* from cd_products_investment i where i.userId = :userId", nativeQuery = true)
    List<CDProductInvestment> getCDProductInvestmentByUserId(@Param("userId") Long userId);
}
