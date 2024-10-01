package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSequenceRepository extends JpaRepository<AccountSequence, Integer> {

}
