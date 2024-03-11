package com.ingeniorx.butterscotch.java.upstreamconsumer.repository;

import com.ingeniorx.butterscotch.java.upstreamconsumer.model.LgcBenefitDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LgcBenefitDetailsRepository extends JpaRepository<LgcBenefitDetails,Long> {

//    @Query(value = " SELECT * FROM LGC_BENEFIT_DETAILS WHERE json_equal (BENEFIT_DETAILS, :benefitDetails)", nativeQuery = true)
    LgcBenefitDetails findByBenefitDetails(@Param("benefitDetails") String benefitDetails);

    LgcBenefitDetails findByFileName(String fileName);
}
