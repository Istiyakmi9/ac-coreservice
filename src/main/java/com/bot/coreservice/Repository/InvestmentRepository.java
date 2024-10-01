package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.InvestmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentDetail, Long> {
    @Query(value = "select i.* from investment_detail i where i.userId = :userId", nativeQuery = true)
    List<InvestmentDetail> getInvestmentByUserId(@Param("userId") Long userId);
}
