package com.ingeniorx.butterscotch.java.upstreamconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BenifitDetailsDto {
    @JsonProperty("product")
    Product product;
}

class Product {
    @JsonProperty("version")
    String version;
    @JsonProperty("sourceProductConfigSystem")
    String sourceProductConfigSystem;
    @JsonProperty("keys")
    Keys keys;
    @JsonProperty("productInfo")
    ProductInfo productInfo;
}

class Keys {
    @JsonProperty("contractCode")
    String contractCode;
    @JsonProperty("effectiveDate")
    String effectiveDate;
}


class ProductInfo {
    @JsonProperty("planAttributes")
    PlanAttributes planAttributes;
    @JsonProperty("productAttributes")
    ProductAttributes productAttributes;
    @JsonProperty("networks")
    Networks networks[];
    @JsonProperty("healthAccountInfo")
    HealthAccountInfo healthAccountInfo;
    @JsonProperty("pharmacyBenefitNetworks")
    PharmacyBenefitNetworks pharmacyBenefitNetworks[];
    @JsonProperty("contractCodes")
    ContractCodes contractCodes[];
    @JsonProperty("pharmacyPlan")
    PharmacyPlan pharmacyPlan;
}


class PlanAttributes {
    @JsonProperty("market Segment")
    String marketSegment;

}

class ProductAttributes {
    @JsonProperty("fundingTypeDescription")
    String fundingTypeDescription;
    @JsonProperty("hraPayment")
    String hraPayment;
    @JsonProperty("stateCode")
    String stateCode;
    @JsonProperty("networkType")
    String networkType;
    @JsonProperty("masterBrand")
    String masterBrand;
    @JsonProperty("exchange Indicator")
    String exchangeIndicator;
}

class HealthAccountInfo {
    @JsonProperty("cdhVendorName")
    String cdhVendorName;
    @JsonProperty("cdhProductType")
    String cdhProductType;
}

class PharmacyPlan {
    @JsonProperty("preventiveRxPlusInfo")
    PreventiveRxPlusInfo preventiveRxPlusInfo;
    @JsonProperty("pharmacyPlanAttributes")
    PharmacyPlanAttributes pharmacyPlanAttributes;
    @JsonProperty("pharmacyPlanInfo")
    PharmacyPlanInfo pharmacyPlanInfo;
}

class PharmacyBenefitNetworks {
    @JsonProperty("networkLevel")
    NetworkLevel networkLevel;
}

class ContractCodes {
    @JsonProperty("administrationCodeType")
    String administrationCodeType;
}

class Networks {
    @JsonProperty("outOfPocketFamily")
    String outOfPocketFamily;
    @JsonProperty("accumulatorDedOopOptionId")
    String accumulatorDedOopOptionId;
    @JsonProperty("deductibleSingle")
    String deductibleSingle;
    @JsonProperty("outOfPocketSingle")
    String outOfPocketSingle;
    @JsonProperty("deductibleFamily")
    String deductibleFamily;
    @JsonProperty("networkName")
    String networkName;

}

class PreventiveRxPlusInfo{
    @JsonProperty("costShareTreatment")
    String costShareTreatment;
    @JsonProperty("preventiveList")
    String preventiveList;
    @JsonProperty("oonCostShareTreatment")
    String oonCostShareTreatment;
    @JsonProperty("applyToMedicalOrRxDeductibleFlag")
    String applyToMedicalOrRxDeductibleFlag;
}



class PharmacyPlanAttributes {
    @JsonProperty("rx0opCombined With Med")
    String rx0opCombinedWithMed;
    @JsonProperty("rxDedSingle")
    String rxDedSingle;
    @JsonProperty("rxDedFamily")
    String rxDedFamily;
    @JsonProperty("rx0onDedFamily")
    String rx0onDedFamily;
    @JsonProperty("isInnAndOonCombined")
    String isInnAndOonCombined;
    @JsonProperty("enterpriseRxPlanType")
    String enterpriseRxPlanType;
    @JsonProperty("rx0onDedCombinedWithMed")
    String rx0onDedCombinedWithMed;
    @JsonProperty("rx0onDedSingle")
    String rx0onDedSingle;
}

class PharmacyPlanInfo {
    @JsonProperty("rx0opCombinedWithMed")
    String rx0opCombinedWithMed;
    @JsonProperty("preferredGeneric")
    String preferredGeneric;
    @JsonProperty("homeDeliveryOption")
    String homeDeliveryOption;
    @JsonProperty("formulary")
    String formulary;
}
class NetworkLevel {
    @JsonProperty("tiers") Tiers tiers [];
}



class Tiers {
    @JsonProperty("tierGreaterOf")
    String tierGreater0f;
    @JsonProperty("deductibleTreatment")
    String deductibleTreatment;
    @JsonProperty("homeDelivery")
    HomeDelivery homeDelivery;
    @JsonProperty("retail")
    Retail retail;
}


class HomeDelivery {
    @JsonProperty("tierHdCopay")
    String tierHdCopay;
    @JsonProperty("tierHdMultiplier")
    String tierHdMultiplier;
    @JsonProperty("tierHdDayLimitNonSpecialty")
    String tierHdDayLimitNonSpecialty;
    @JsonProperty("tierHdPerScriptMax")
    String tierHdPerScriptMax;
    @JsonProperty("tierHdCoinsurance")
    String tierHdCoinsurance;
}
class Retail {
    @JsonProperty("tierCoinsurance")
    String tierCoinsurance;
    @JsonProperty("tierThirtyDaySupplyCopay")
    String tierThirtyDaySupplyCopay;
    @JsonProperty("tierRetailDayLimit")
    String tierRetailDayLimit;
    @JsonProperty("tierRetailperScriptMax")
    String tierRetailperScriptMax;
    @JsonProperty("tierRetailNinetyDayMultiplier")
    String tierRetailNinetyDayMultiplier;
    @JsonProperty("tierRetailNinetyDayCopayAmount")
    String tierRetailNinetyDayCopayAmount;
}
























