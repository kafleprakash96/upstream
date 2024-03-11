ToDo:
Uncomment

Line 10 (LgcBenefitDetailsRepository)
@Query(value = " SELECT * FROM LGC_BENEFIT_DETAILS WHERE json_equal (BENEFIT_DETAILS, :benefitDetails)", nativeQuery = true)

Line 163, 164(LGCFileServiceImpl.java)

// LgcBenefitDetails benefitDetail = lgcBenefitDetailsRepository
//                    .findByBenefitDetails(om.writeValueAsString(benefitDetials));


Line 29 (BenefitDto)
private BenefitVersionDto benefitVersion;