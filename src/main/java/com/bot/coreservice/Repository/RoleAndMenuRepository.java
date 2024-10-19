package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleAndMenuRepository extends JpaRepository<AccessLevel, Long> {
}
