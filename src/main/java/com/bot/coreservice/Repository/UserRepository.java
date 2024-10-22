package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true, value = " select u.* from users u where u.mobileNumber = :mobileNumber or u.aadharNumber = :aadharNumber ")
    User getUserByEmailOrMobile(@Param("mobileNumber") String mobileNumber, @Param("aadharNumber") String aadharNumber );
}
