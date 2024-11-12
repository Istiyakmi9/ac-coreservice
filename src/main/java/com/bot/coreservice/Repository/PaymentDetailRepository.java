package com.bot.coreservice.Repository;

import com.bot.coreservice.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Integer> {
    @Query(nativeQuery = true, value = " select i.* from payment_detail i where i.investmentId = :investmentId and i.investmentCategoryTypeId = :investmentCategoryTypeId")
    List<PaymentDetail> getPaymentDetailByInvId(@Param("investmentId") long investmentId, @Param("investmentCategoryTypeId") int investmentCategoryTypeId);
}
