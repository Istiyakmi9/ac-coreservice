package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.InvestmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentTypeRepository extends JpaRepository<InvestmentType, Integer> {
}
