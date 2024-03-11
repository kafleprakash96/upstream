package com.ingeniorx.butterscotch.java.upstreamconsumer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//import com.ingeniorx.butterscotch.java.upstream.consumer.dto.ContractCodeDto;
//import com.ingeniorx.butterscotch.java.upstream.consumer.dto.RemediationQueueDto;
//import com.ingeniorx.butterscotch.java.upstream.consumer.exceptions.IntegrationAWSException;
//import com.ingeniorx.butterscotch.java.upstream.consumer.model.LgcBenefitDetails;
//import com.ingeniorx.butterscotch.java.upstream.consumer.model.RemediationQueue;
//import com.ingeniorx.butterscotch.java.upstream.consumer.repository.LgcBenefitDetailsRepository;
//import com.ingeniorx.butterscotch.java.upstream.consumer.repository.RemediationQueueRepository;
//import com.ingeniorx.butterscotch.java.upstream.consumer.util.CommonUtil;
import com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.*;
import com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit.BenefitApi;
import com.ingeniorx.butterscotch.java.upstreamconsumer.dto.BenifitDetailsDto;
import com.ingeniorx.butterscotch.java.upstreamconsumer.dto.RemediationQueueDto;
import com.ingeniorx.butterscotch.java.upstreamconsumer.model.LgcBenefitDetails;
import com.ingeniorx.butterscotch.java.upstreamconsumer.model.RemediationQueue;
import com.ingeniorx.butterscotch.java.upstreamconsumer.repository.LgcBenefitDetailsRepository;
import com.ingeniorx.butterscotch.java.upstreamconsumer.service.LGCFileService;
import com.ingeniorx.butterscotch.java.upstreamconsumer.repository.RemediationQueueRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


//import com.ingeniorx.butterscotch.filestatus.dto.FileStatusDto;
//import com.ingeniorx.butterscotch.lib.client.dto.DynamicLayerDto;
//import com.ingeniorx.butterscotch.lib.client.client.DynamicLayerApi;
//import com.ingeniorx.butterscotch.filestatus.client.FileStatusApi;
//import com.ingeniorx.butterscotch.lib.benefit.client.BenefitApi;
//import com.ingeniorx.butterscotch.lib.benefit.dto.BenefitDto;


import io.micrometer.core.instrument.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class LGCFileServiceImpl implements LGCFileService {

    @Value("${contract.effective.date}")
    private String contractEffDate;

    @Value("${cloud.aws.s3.integration.lgc.error.folder}")
    private String lgcErrorFolder;

    @Value("${cloud.aws.s3.integration.bucket}")
    private String integrationBucketName;

    @Value("${cloud.aws.s3.integration.lgc.success.folder}")
    private String lgcSuccessFolder;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private DynamicLayerApi dynamicLayerApi;

    @Autowired
    private FileStatusApi fileStatusApi;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RemediationQueueRepository remediationQueueRepository;

    @Autowired
    private LgcBenefitDetailsRepository lgcBenefitDetailsRepository;

    @Autowired
    private BenefitApi benefitApi;

    @Value("${client.contractCode.endpoint}")
    private String integrationEndPoint;

    @Autowired
    @Qualifier("integration_restTemplate")
    private RestTemplate restTemplate;

    private static final String PRODUCT = "product";
    private static final String PRODUCT_INFO = "productInfo";

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String CONTRACT_CODES = "contractCodes";

    private static final String EFFECTIVE_DATE = "effectiveDate";

    private static final String FUNDING_TYPE_DESCRIPTION="fundingTypeDescription";
    private static final String NETWORK_TYPE="networkType";
    private static final String MASTER_BRAND="masterBrand";
    private static final String CDH_PRODUCT_TYPE="cdhProductType";
    private static final String FORMULARY="formulary";
    public static final String MARKET_SEGMENT = "marketSegment";
    private static final String CONFIG_PLATFORM_CODE_VALUE="SPIDER";
    private static final String CONFIG_PLATFORM_CODE="configPlatformCode";
    private static final String PRODUCT_ATTRIBUTES="productAttributes";
    public static final String KEYS ="keys";
    public static final String QUOTELINE_ITEM_ID = "quoteLineItemID";

    private static final String SOURCE_TYPE="SPIDER";

    @Override
    public boolean readFiles(String lgcContent,String fileName,String token) throws IOException{
        StringBuilder dynamicLayerErrorDescription=new StringBuilder();
        FileStatusType fileStatusType = lgcValidation(lgcContent, fileName,token,dynamicLayerErrorDescription);
        if(fileStatusType != null) {
            setFileStatus(fileStatusType.getType(), fileName, token,dynamicLayerErrorDescription);
            uploadFileToS3Bucket(lgcContent, lgcErrorFolder + fileName);
            return true;
        } else {
            saveRemediationQueue(lgcContent,fileName);
            LocalDate contractEffectiveDate = null;
            Long benefitPlanIdVersion ;
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            om.configure(SerializationFeature.INDENT_OUTPUT, true);
            BenifitDetailsDto benefitDetials = om.readValue(lgcContent, BenifitDetailsDto.class);
            JSONObject json = new JSONObject(lgcContent);
            JSONObject product = json.getJSONObject("product");
            JSONObject keys = product.getJSONObject("keys");
            /* IBP-27115 : getting effective date from Contract codes array */
            contractEffectiveDate=getContractCodesEffDate(json);
            String contractCode=keys.getString("contractCode");

//            LgcBenefitDetails benefitDetail = lgcBenefitDetailsRepository
//                    .findByBenefitDetails(om.writeValueAsString(benefitDetials));

            LgcBenefitDetails benefitDetail = lgcBenefitDetailsRepository.findByBenefitDetails("Some String");

            try {
                if (null == benefitDetail) {
                    LgcBenefitDetails lgcBenefitDetails = new LgcBenefitDetails();
                    lgcBenefitDetails.setBenefitDetails(om.writeValueAsString(benefitDetials));
                    lgcBenefitDetails.setFileName(fileName);
                    lgcBenefitDetails = lgcBenefitDetailsRepository.save(lgcBenefitDetails);
                    //ResponseEntity<Void> result=restTemplate.exchange(ingesationEndPoint, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthorizationHeader(token)), Void.class);
                    //uploadFileToS3Bucket(lgcContent, lgcSuccessFolder + fileName);
                } else {
                    LgcBenefitDetails lgcBenefitDetails=lgcBenefitDetailsRepository.findByFileName(fileName);
                    benefitPlanIdVersion=Long.valueOf(lgcBenefitDetails.getBenefitPlanId());
                    BenefitDto benefitDto = benefitApi.getByBenefitVersionId(benefitPlanIdVersion, true, token);
                    BenefitDto benefit=benefitApi.get(benefitDto.getId(), token);
                    ContractCodeDto contractCodeDto = new ContractCodeDto();
                    contractCodeDto.setBenefitPlanEffDate(benefit.getDynamicLayers().get(0).getEffectiveDate());
                    contractCodeDto.setBenefitPlanId(benefitDto.getBenefitPlanId());
                    contractCodeDto.setSourceSystem(SOURCE_TYPE);
                    contractCodeDto.setSourceData(SOURCE_TYPE);
                    contractCodeDto.setContractCode(contractCode);
                    contractCodeDto.setContractEffDate(contractEffectiveDate);
                    contractCodeDto.setBenefitPlanIdVersion(benefitPlanIdVersion);
                    contractCodeDto.setSendDownstream(false);
                    ResponseEntity<ContractCodeDto> response = restTemplate.exchange(
                            integrationEndPoint + "/save", HttpMethod.POST,
                            new HttpEntity<>(contractCodeDto,getAuthorization(token)), ContractCodeDto.class
                    );
                }
            } catch (Exception e) {
                log.error("Exception ", e.getMessage());
            }

            uploadFileToS3Bucket(lgcContent, lgcSuccessFolder + fileName);
        }
        return false;

    }


    @Override
    public void readFile(String lgcSample,String fileName,String token) {
        try{
            log.info(String.format("before read file -> %s", fileName));
            readFiles(lgcSample,fileName, token);
            log.info(String.format("after file being read-> %s", fileName));
        } catch (Exception e) {
            log.error("Error while reading json file as resource file"+e.getMessage());
        }
    }
    public FileStatusType lgcValidation(String lgcContent, String fileName, String token, StringBuilder errorDescription) {
        // Check empty file
        if(StringUtils.isEmpty(lgcContent) || lgcContent.equals("{}")) {
            errorDescription.append("Invalid Empty - LGC File");
            return FileStatusType.INVALID_EMPTY;

        } else {
            try {
                // Check invalid file
                JSONObject baseJson = new JSONObject(lgcContent);

                // Check non pharm
                if(!pharmacyBenefit(baseJson)) {
                    return FileStatusType.INVALID_NON_PHARM;
                }
                if(!isValidConfigPlatformCode(baseJson,errorDescription)) {
                    return FileStatusType.INVALID_FILE;
                }
                // To Check the mandatory fields (IBP-23096)
                if(!checkMandatoryFields(baseJson,errorDescription)) {
                    return FileStatusType.INVALID_FILE;
                }

                if(!checkJsonEffDateWithSepecificDate(baseJson,errorDescription)) {
                    return FileStatusType.INVALID_FILE;
                }

                if(!isValidDynamicLayers(baseJson,token,errorDescription)) {
                    return FileStatusType.MISSING_DYNAMIC_LAYER;
                }
                // To check the quoteline field value
                if (!checkQuotelineField(baseJson, errorDescription)) {
                    return FileStatusType.INVALID_FILE;
                }

            } catch (JSONException e) {
                log.info("File {} has an invalid json format.", fileName);
                errorDescription.append("Corrupted/Invalid - LGC File");
                return FileStatusType.INVALID_FILE;
            } catch(Exception ex) {
                log.error("Exception Occured While LGC File Validation.", ex.getMessage());
                return FileStatusType.INVALID_FILE;
            }
        }
        return null;
    }

    public boolean pharmacyBenefit(JSONObject json) {
        try {
            JSONObject pharmacyPlan = json.getJSONObject("product")
                    .getJSONObject("productInfo").getJSONObject("pharmacyPlan");

            JSONArray pharmacyBenefitNetworks = json.getJSONObject("product")
                    .getJSONObject("productInfo").getJSONArray("pharmacyBenefitNetworks");

            if(pharmacyPlan.length() == 0 || pharmacyBenefitNetworks.length() == 0) {
                return false;
            }
            return true;
        } catch (JSONException e) {
            log.info("Provided JSON does not contain pharmacy.");
            return false;
        }
    }
    private boolean isValidConfigPlatformCode(JSONObject baseJson, StringBuilder dynamicLayerErrorDescription) {
        log.info("Validating ConfigPlatformCode");
        JSONObject productInfoJsonObject = baseJson.getJSONObject(PRODUCT).getJSONObject(PRODUCT_INFO);
        String configPlatformCode=productInfoJsonObject.getJSONObject(PRODUCT_ATTRIBUTES).getString(CONFIG_PLATFORM_CODE);
        if(configPlatformCode.equalsIgnoreCase(CONFIG_PLATFORM_CODE_VALUE)) {
            return true;
        }
        dynamicLayerErrorDescription.append("File received with configplatformcode WPD or SPACE");
        return false;
    }

    // IBP-23096 Added method to check the mandatory fields in LGC JSON.
    private boolean checkMandatoryFields(JSONObject json, StringBuilder errorDescription) {
        try {
            log.info("Started checking the mandatory fields in lgc json");
            List<String> fields = new ArrayList<>(Arrays.asList("marketSegment", "enterprisePlanName",
                    "fundingTypeDescription", "configPlatformCode", "productName", "hraPayment", "stateCode",
                    "configPlatformDescription", "networkType", "planType", "masterBrand", "exchangeIndicator",
                    "cdhVendorName", "cdhProductType", "upfrontDeductible", "splitGenericsOrSpecialty",
                    "preventiveList","costShareTreatment", "oonCostShareTreatment", "rxPlusCoverageFlag",
                    "applyToMedicalOrRxDeductibleFlag", "rxOopCombinedWithMed", "rxDedSingle", "rxDedFamily",
                    "rxOonDedFamily", "isInnAndOonCombined", "enterpriseRxPlanType", "rxOonDedCombinedWithMed",
                    "rxOonDedSingle", "comments", "networkName", "isTwoLevelRx", "preferredGeneric", "planNotes",
                    "homeDeliveryOption", "formulary", "medicalCrossAccumulationDescription"));

            JSONObject productInfoJsonObject = json.getJSONObject("product").getJSONObject("productInfo");
            JSONArray preventiveRxNetworkLevelsArray = new JSONArray();
            if (productInfoJsonObject.length() != 0) {
                JSONObject preventiveRxPlusInfoJsonObject =  productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("preventiveRxPlusInfo");
                if(preventiveRxPlusInfoJsonObject.has("preventiveRxNetworkLevels") && !preventiveRxPlusInfoJsonObject.isNull("preventiveRxNetworkLevels")) {
                    preventiveRxNetworkLevelsArray = preventiveRxPlusInfoJsonObject.getJSONArray("preventiveRxNetworkLevels");
                }

                if (!(json.getJSONObject("product").getJSONObject("keys").has("contractCode"))) {
                    errorDescription.append("Missing Mandatory Fields - ContractCode In LGC File");
                    return false;
                }

                for (String field : fields) {
                    if (!(productInfoJsonObject.getJSONObject("productAttributes").has(field)
                            || productInfoJsonObject.getJSONObject("healthAccountInfo").has(field)
                            || productInfoJsonObject.getJSONObject("planAttributes").has(field)
                            || productInfoJsonObject.getJSONObject("pharmacyPlan").has(field)
                            || productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("pharmacyPlanInfo")
                            .has(field)
                            || productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("preventiveRxPlusInfo")
                            .has(field)
                            || productInfoJsonObject.getJSONObject("pharmacyPlan")
                            .getJSONObject("pharmacyPlanAttributes").has(field)
                            || productInfoJsonObject.getJSONObject("outOfCountryDetails").has(field))) {
                        log.info("field not found in input :: {}", field);
                        errorDescription.append("Missing Mandatory Fields - ").append(field).append(" In LGC File");
                        return false;
                    }
                }

                if(productInfoJsonObject.has("networks") && !productInfoJsonObject.isNull("networks")) {
                    for (Object networkObj : productInfoJsonObject.getJSONArray("networks")) {
                        JSONObject networkJsonObject = ((JSONObject) networkObj);
                        if (!(networkJsonObject.has("outOfPocketFamily") && networkJsonObject.has("accumulatorDedOopOption")
                                && networkJsonObject.has("deductibleSingle") && networkJsonObject.has("outOfPocketSingle")
                                && networkJsonObject.has("deductibleFamily"))) {
                            log.info("outOfPocketFamily/accumulatorDedOopOption/deductibleSingle/outOfPocketSingle/deductibleFamily Fields are not found under networks");
                            errorDescription.append("Missing Mandatory Fields - OopFamily/accumDedOopOption/DedSingle/OopSingle/DedFamily In LGC File");
                            return false;
                        }
                    }
                }

                if(productInfoJsonObject.has("contractCodes") && !productInfoJsonObject.isNull("contractCodes")) {
                    for (Object contractCodeObj : productInfoJsonObject.getJSONArray("contractCodes")) {
                        JSONObject contractCodeJsonObj = ((JSONObject) contractCodeObj);
                        if (!(contractCodeJsonObj.has("contractCode") && contractCodeJsonObj.has("effectiveDate"))) {
                            log.info("contractCode/effectiveDate Fields are not found under contractCodes");
                            errorDescription.append("Missing Mandatory Fields - ContractCodes - ContractCode/EffectiveDate In LGC File");
                            return false;
                        }

                        if(contractCodeJsonObj.has("serviceAreas") && !contractCodeJsonObj.isNull("serviceAreas")) {
                            for (Object serviceAreasObj : contractCodeJsonObj.getJSONArray("serviceAreas")) {
                                JSONObject serviceAreasJsonObj = ((JSONObject) serviceAreasObj);
                                if(serviceAreasJsonObj.has("medicalBenefitNetworkLevels") && !serviceAreasJsonObj.isNull("medicalBenefitNetworkLevels")) {
                                    if(serviceAreasJsonObj.getJSONArray("medicalBenefitNetworkLevels").length() >0) {
                                        JSONObject medicalBenefitNetworkLevelsObj1 = ((JSONObject) serviceAreasJsonObj.getJSONArray("medicalBenefitNetworkLevels").get(0));
                                        if (!(medicalBenefitNetworkLevelsObj1.has("copayAmount")
                                                && medicalBenefitNetworkLevelsObj1.has("coinsurancePercentage"))) {
                                            log.info("copayAmount/coinsurancePercentage Fields are not found under contractCodes/serviceAreas");
                                            errorDescription.append("Missing Mandatory Fields - ServiceAreas - copayAmount/coinsurancePercentage In LGC File");
                                            return false;
                                        }
                                    }

                                }
                            }
                        }

                    }
                }

                if (!productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("preventiveRxPlusInfo")
                        .isNull("preventiveList")) {
                    for (Object preventiveRxNetworkLevelObj : preventiveRxNetworkLevelsArray) {
                        if (!(((JSONObject) preventiveRxNetworkLevelObj).has("copayAmount")
                                && ((JSONObject) preventiveRxNetworkLevelObj).has("coinsurancePercentage"))) {
                            log.info("copayAmount/coinsurancePercentage Fields are not found under preventiveRxNetworkLevelsArray");
                            errorDescription.append("Missing Mandatory Fields - PharmacyPlan - copayAmount/coinsurancePercentage In LGC File");
                            return false;
                        }
                    }
                }

                if(productInfoJsonObject.has("pharmacyBenefitNetworks") && !productInfoJsonObject.isNull("pharmacyBenefitNetworks")) {
                    for (Object pharmacyBenefitNetworkObj : productInfoJsonObject.getJSONArray("pharmacyBenefitNetworks")) {
                        JSONObject pharmacyBenefitNetworkJsonObj = ((JSONObject) pharmacyBenefitNetworkObj);
                        if(pharmacyBenefitNetworkJsonObj.has("networkLevel") && !pharmacyBenefitNetworkJsonObj.isNull("networkLevel")) {
                            JSONObject networkLevelJsonObject = pharmacyBenefitNetworkJsonObj.getJSONObject("networkLevel");
                            if(networkLevelJsonObject.has("tiers") && !networkLevelJsonObject.isNull("tiers")) {
                                JSONArray tiersArray = networkLevelJsonObject.getJSONArray("tiers");
                                if(productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("pharmacyPlanAttributes").has("enterpriseRxPlanType")) {
                                    String rxPlanTypeString = productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("pharmacyPlanAttributes")
                                            .getString("enterpriseRxPlanType");
                                    String[] rxPlanTypeStringArray = rxPlanTypeString.split(" ");
                                    Integer tierValue= Integer.valueOf(rxPlanTypeStringArray[0]);
                                    //IBP-26728: handling split generics scenario for 3 tier
                                    tierValue = rxPlanTypeString.equalsIgnoreCase("3 Tier with Split Generics") ? tierValue+1 : tierValue;
                                    if(tierValue != tiersArray.length()) {
                                        log.info("Tiers not found under pharmacyBenefitNetworks/networkLevel -  total no of tiers::{}, avialble tiers ::{}",tierValue,tiersArray.length());
                                        errorDescription.append("Missing Mandatory Fields - Tier Information pharmacyBenefitNetworks/networkLevel In LGC File");
                                        return false;
                                    }
                                }
                                for (Object tierObj : tiersArray) {
                                    JSONObject tierJsonObject = ((JSONObject) tierObj);
                                    JSONObject retailsJsonObject = tierJsonObject.getJSONObject("retail");
                                    if (tierJsonObject.has("homeDelivery") && !tierJsonObject.isNull("homeDelivery")) {
                                        JSONObject homeDeliveryJsonObject = tierJsonObject.getJSONObject("homeDelivery");
                                        if (!(homeDeliveryJsonObject.has("tierHdCopay")
                                                && homeDeliveryJsonObject.has("tierHdPerScriptMax")
                                                && homeDeliveryJsonObject.has("tierHdCoinsurance"))) {
                                            log.info("homeDelivery-tier information not found under pharmacyBenefitNetworks");
                                            errorDescription.append("Missing Mandatory Fields - HomeDelivery Tier Information In LGC File");
                                            return false;
                                        }
                                    }
                                    if (tierJsonObject.has("retail") && !tierJsonObject.isNull("retail")) {
                                        if (!(retailsJsonObject.has("tierCoinsurance")
                                                && retailsJsonObject.has("tierThirtyDaySupplyCopay")
                                                && retailsJsonObject.has("tierRetailperScriptMax"))){
                                            log.info("retail-tier information not found under pharmacyBenefitNetworks");
                                            errorDescription.append("Missing Mandatory Fields - Retail Tier Information In LGC File");
                                            return false;
                                        }
                                    }

                                    if (!(tierJsonObject.has("deductibleTreatment") && tierJsonObject.has("tierGreaterOf"))) {
                                        log.info("deductibleTreatment/tierGreaterOf not found under pharmacyBenefitNetworks");
                                        errorDescription.append("Missing Mandatory Fields - DeductibleTreatment/TierGreaterOf In LGC File");
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                errorDescription.append("Missing Mandatory Fields - ProductInfo In LGC File");
                return false;
            }
            log.info("Completed checking the mandatory fields in lgc json");
            return true;
        } catch (JSONException e) {
            log.info("Caused Exception while validating LGC Json and exception cause ::{}", e.getMessage());
            return false;
        }
    }
    private boolean checkJsonEffDateWithSepecificDate(JSONObject baseJson, StringBuilder errorDescription) {
        LocalDate contractEffectiveDate = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate contractSpecificDate = LocalDate.parse(contractEffDate, dtf);
        try {
            JSONArray contractCodesArray = baseJson.getJSONObject(PRODUCT).getJSONObject(PRODUCT_INFO)
                    .getJSONArray(CONTRACT_CODES);
            for (Object contractCodeObj : contractCodesArray) {
                String jsonEffectiveDate = ((JSONObject) contractCodeObj).getString(EFFECTIVE_DATE);
                contractEffectiveDate = LocalDate.parse(jsonEffectiveDate);
                if (contractEffectiveDate.isBefore(contractSpecificDate)) {
                    errorDescription.append("Invalid Effective Date - LGC File");
                    return false;
                }
            }
            return true;
        } catch (JSONException e) {
            log.info("Provided JSON does not contain product or productInfo or contractCodes.");
            return false;
        }
    }


    public boolean isValidDynamicLayers(JSONObject baseJson,String token,StringBuilder dynamicLayerErrorDescription) {
        JSONObject productInfoJsonObject = baseJson.getJSONObject(PRODUCT).getJSONObject(PRODUCT_INFO);
        List<String> fields = new ArrayList<>(Arrays.asList(MARKET_SEGMENT,
                FUNDING_TYPE_DESCRIPTION,NETWORK_TYPE, MASTER_BRAND,CDH_PRODUCT_TYPE,FORMULARY));
        if(productInfoJsonObject!=null && productInfoJsonObject.length() != 0) {
            for (String field : fields) {
                if (!(productInfoJsonObject.getJSONObject("productAttributes").has(field)
                        || productInfoJsonObject.getJSONObject("healthAccountInfo").has(field)
                        || productInfoJsonObject.getJSONObject("planAttributes").has(field)
                        || productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("pharmacyPlanInfo")
                        .has(field))) {
                    log.info("field not found in input :: {}", field);
                    return false;
                }
                try {
                    if(field.equals(MASTER_BRAND)){
                        String masterBrand=productInfoJsonObject.getJSONObject("productAttributes").getString(MASTER_BRAND);
                        if(masterBrand !=null && !masterBrand.isEmpty()) {
                            // needs to add available LGC values in this list from mapping doc.
                            List<String> validBusinessEntityNames=List.of("anthembluecrossandblueshield","bcbs","bc","anthembluecrossandanthembluecrosslifeandhealthinsurancecompany","empirebluecross","empirebluecrossblueshield","anthemhealthkeepersinc","anthembluecross","anthembluecrossandblueshieldhealthplus","anthembluecrossblueshieldhealthplus","anthembluecrosshealthplus");
                            String masterBrandWithoutSpace=masterBrand.replaceAll(" ", "");
                            if(!validBusinessEntityNames.contains(masterBrandWithoutSpace.toLowerCase())) {
                                log.info("Missing dynamic layer: categoryName:: {} and value:: {}","MasterBrand(BusinessEntity)",masterBrand);
                                dynamicLayerErrorDescription=dynamicLayerErrorDescription.length()>0?dynamicLayerErrorDescription.append(",").append("MasterBrand(BusinessEntity)").append("-").append(masterBrand):dynamicLayerErrorDescription.append("MasterBrand(BusinessEntity)").append("-").append(masterBrand);
                            }else {
                                isDynamicLayerExist("MasterBrand(BusinessEntity)",convertMasterBrand(masterBrandWithoutSpace,productInfoJsonObject.getJSONObject("productAttributes").getString("stateCode")),token,dynamicLayerErrorDescription);
                            }
                        }else{
                            log.info("Missing dynamic layer: categoryName:: {} and value:: {}","MasterBrand(BusinessEntity)",masterBrand);
                            dynamicLayerErrorDescription=dynamicLayerErrorDescription.length()>0?dynamicLayerErrorDescription.append(",").append("MasterBrand(BusinessEntity)").append("-").append(masterBrand):dynamicLayerErrorDescription.append("MasterBrand(BusinessEntity)").append("-").append(masterBrand);
                        }
                    }else if(field.equals(FORMULARY)) {
                        isDynamicLayerExist("Formulary",convertFormulary(productInfoJsonObject.getJSONObject("pharmacyPlan").getJSONObject("pharmacyPlanInfo").getString(FORMULARY),productInfoJsonObject.getJSONObject("productAttributes").getString("stateCode")),token,dynamicLayerErrorDescription);
                    }else if(field.equals(FUNDING_TYPE_DESCRIPTION)) {
                        isDynamicLayerExist("FundingType",productInfoJsonObject.getJSONObject("productAttributes").getString(FUNDING_TYPE_DESCRIPTION),token,dynamicLayerErrorDescription);
                    }else if(field.equals(NETWORK_TYPE)) {
                        isDynamicLayerExist("ProductType",convertProductType(productInfoJsonObject.getJSONObject("productAttributes").getString(NETWORK_TYPE)),token,dynamicLayerErrorDescription);
                    }else if(field.equals(CDH_PRODUCT_TYPE)) {
                        isDynamicLayerExist("CDHP",convertCdhp(productInfoJsonObject.getJSONObject("healthAccountInfo").isNull(CDH_PRODUCT_TYPE)?null:productInfoJsonObject.getJSONObject("healthAccountInfo").getString(CDH_PRODUCT_TYPE)),token,dynamicLayerErrorDescription);
                    }else if(field.equals(MARKET_SEGMENT)) {
                        isDynamicLayerExist("MarketSegment",convertMarketSegment(productInfoJsonObject.getJSONObject("planAttributes").getString(MARKET_SEGMENT)),token,dynamicLayerErrorDescription);
                    }
                }catch(JSONException e) {
                    log.error("field value not found in input :: {}", field);
                    dynamicLayerErrorDescription=dynamicLayerErrorDescription.length()>0?dynamicLayerErrorDescription.append(",").append(field):dynamicLayerErrorDescription.append(field);
                }
            }
            if(dynamicLayerErrorDescription.length()>0) {
                dynamicLayerErrorDescription.insert(0,"Missing DynamicLayer: ");
                return false;
            }
            return true;
        }
        return false;
    }
    private void isDynamicLayerExist(String categoryName,String dynamicLayerValue,String token,StringBuilder dynamicLayerErrorDescription){
        List<DynamicLayerDto> layerList =dynamicLayerApi.getDynamicLayerByLayerNameOrDescription(dynamicLayerValue,dynamicLayerValue, token);
        if(layerList==null || layerList.isEmpty()) {
            log.info("Missing dynamic layer: categoryName:: {} and value:: {}",categoryName,dynamicLayerValue);
            dynamicLayerErrorDescription=dynamicLayerErrorDescription.length()>0?dynamicLayerErrorDescription.append(",").append(categoryName).append("-").append(dynamicLayerValue):dynamicLayerErrorDescription.append(categoryName).append("-").append(dynamicLayerValue);
        }
    }

    private String convertMasterBrand(String masterBrand,String stateCode){
        if(masterBrand.equalsIgnoreCase("EmpireBlueCrossBlueShield")||masterBrand.equalsIgnoreCase("BCBS")) {
            return "EmpireBCBS";
        }
        if(masterBrand.equalsIgnoreCase("EmpireBlueCrossHealthPlan")||masterBrand.equalsIgnoreCase("AnthemHealthkeepersInc")) {
            return "EmpireBCHP";
        }
        if(masterBrand.equalsIgnoreCase("EmpireBlueCrossBlueShieldHealthPlus")||masterBrand.equalsIgnoreCase("AnthemBlueCrossandAnthemBlueCrossLifeandHealthInsuranceCompany")) {
            return "EmpireBCBSHP";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCrossBlueShield")||masterBrand.equalsIgnoreCase("AnthemBlueCrossandBlueShield")) {
            return "AnthemBCBS";
        }
        if(masterBrand.equalsIgnoreCase("EmpireBlueCross")||masterBrand.equalsIgnoreCase("BC")) {
            return "EmpireBC";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCross") && stateCode.equals("CA")) {
            return "AnthemBCC";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCross") && stateCode.equals("NY")) {
            return "AnthemBC";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCrossBlueShieldHealthPlus") || masterBrand.equalsIgnoreCase("AnthemBlueCrossandBlueShieldHealthPlus")) {
            return "AnthemBCBSHP";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCrossHealthPlus")) {
            return "AnthemBCHP";
        }
        if(masterBrand.equalsIgnoreCase("AnthemBlueCross") && !stateCode.equals("CA") && !stateCode.equals("NY")) {
            return "AnthemBCBS";
        }
        return null;
    }


private String convertFormulary(String formulary,String stateCode) {
        return (formulary==null || formulary.isEmpty()) ? null :
        (formulary.equals("Select") && stateCode.equals("CA"))?"EHB_CA":
        (formulary.equals("Select") && stateCode.equals("CO"))?"EHB_CO":
        (formulary.equals("Select") && stateCode.equals("CT"))?"EHB_CT":
        (formulary.equals("Select") && stateCode.equals("GA"))?"EHB_GA":
        (formulary.equals("Select") && stateCode.equals("IN"))?"EHB_IN":
        (formulary.equals("Select") && stateCode.equals("KY"))?"EHB_KY":
        (formulary.equals("Select") && stateCode.equals("ME"))?"EHB_ME":
        (formulary.equals("Select") && stateCode.equals("MO"))?"EHB_MO":
        (formulary.equals("Select") && stateCode.equals("NH"))?"EHB_NH":
        (formulary.equals("Select") && stateCode.equals("NV"))?"EHB_NV":
        (formulary.equals("Select") && stateCode.equals("NY"))?"EHB_NY":
        (formulary.equals("Select") && stateCode.equals("VA"))?"EHB_VA":
        (formulary.equals("Select") && stateCode.equals("WI"))?"EHB_WI":
        (formulary.equals("National"))?"FM3":
        (formulary.equals("Essential"))?"AESS2751":
        (formulary.equals("Traditional Open"))?"FM3T" :
        formulary.toString().trim();
        }


private String convertMarketSegment(String marketSegment) {
        return (marketSegment==null || marketSegment.isEmpty()) ? null :
        (marketSegment.equalsIgnoreCase("Large Group"))?"Large":
        (marketSegment.equalsIgnoreCase("Large Group Custom"))?"Large":
        (marketSegment.equalsIgnoreCase("Small Group"))?"Small":
        (marketSegment.equalsIgnoreCase("Individual"))?"Individual": marketSegment.toString().trim();
        }
private String convertCdhp(String cdhp) {
        return (cdhp==null)? "Non-CDHP":cdhp.equalsIgnoreCase("HIA Plus") ? "HIAP" :
        cdhp.equalsIgnoreCase("HRA-DF") ? "HRAD" :cdhp.equalsIgnoreCase("HRA") ? "HRA" :
        cdhp.equalsIgnoreCase("H S A") ? "H S A" :cdhp.equalsIgnoreCase("HSA") ? "HSA" :cdhp.equalsIgnoreCase("N")? "Non-CDHP" : cdhp;
        }
private String convertProductType(String productType) {
        return (productType==null || productType.isEmpty()) ? null : productType.equalsIgnoreCase("POS")?"HMO":productType;
        }
public boolean checkQuotelineField(JSONObject json,StringBuilder quotelineErrorDescription) {
        String quoteline = json.getJSONObject(PRODUCT).getJSONObject(KEYS).getString(QUOTELINE_ITEM_ID);
        if(!quoteline.isEmpty() && quoteline.equalsIgnoreCase("NA")) {
        quotelineErrorDescription.append("LGC File received with quote line item NA");
        return false;
        }
        return true;
        }
private void setFileStatus(String status, String fileName, String token,StringBuilder errorDesc) {
        FileStatusDto fileStatusDto = new FileStatusDto();
        fileStatusDto.setClient("Anthem");
        fileStatusDto.setClientApproval(0);
        fileStatusDto.setClientId(1L);
        fileStatusDto.setDateReceived(LocalDateTime.now());
        fileStatusDto.setFileName(fileName);
        fileStatusDto.setInRemediation(0);
        fileStatusDto.setStatus(FileStatusType.valueOf(status));
        fileStatusDto.setSubmittedToPbm(0);
        fileStatusDto.setTotalBenefits(0);
        fileStatusDto.setTransactionId(null);
        fileStatusDto.setErrorDescription(errorDesc.length()>0?errorDesc.toString():null);
        FileStatusDto savedStatus = fileStatusApi.save(fileStatusDto, token);
        log.info("Saved Satus : " + savedStatus.getStatus());
        }

@SneakyThrows
public synchronized void uploadFileToS3Bucket(String jsonString, String fileName) {
        InputStream stream = null;
        try {
        log.info("Publishing report with filename {} to bucket {}", fileName, integrationBucketName);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(jsonString.getBytes(StandardCharsets.UTF_8).length);

        stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));


        PutObjectRequest putObjectRequest = new PutObjectRequest(integrationBucketName, fileName, stream, meta);

        if ("anthem".equals(activeProfile) || "prod-anthem".equals(activeProfile)) {
        putObjectRequest = putObjectRequest.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams());
        }

        PutObjectResult s3Response = s3Client.putObject(putObjectRequest);

        log.info("Upload successful, md5 hash: {}, etag: {}", s3Response.getContentMd5(), s3Response.getETag());

        } catch (Exception e) {
        log.error("Error detected during file upload", e);
        throw new Exception();
        } finally {
        if (stream != null) {
        stream.close();
        }
        }
        }
@Override
public RemediationQueueDto saveRemediationQueue(String lgcContent, String fileName) {
        JSONObject json= null;
        String isCEPFlag = null;
        try {
        json = new JSONObject(lgcContent);
        isCEPFlag = json.getJSONObject("product").getJSONObject("productInfo").getJSONObject("customPlanInfo").getString("CEPFlag");
        } catch (JSONException e) {
        log.info("Provided JSON does not contain CEPFlag.");
        }
        RemediationQueueDto remediationQueueDtoDto = new RemediationQueueDto();
        RemediationQueue remediationQueue = modelMapper.map(remediationQueueDtoDto, RemediationQueue.class);
        remediationQueue.setFilename(fileName);
        remediationQueue.setIs_cep_flag(isCEPFlag);
        RemediationQueueDto savedRemediationQueueDto = modelMapper.map(remediationQueueRepository.save(remediationQueue), RemediationQueueDto.class);
        return savedRemediationQueueDto;
        }

/**
 * IBP-27115
 * @param baseJson
 * @return contractEffectiveDate
 * {@summary - This method is using to get Effective date from Contract codes array}
 */
public LocalDate getContractCodesEffDate(JSONObject baseJson) {
        LocalDate contractEffectiveDate = null;
        try {
        JSONArray contractCodesArray = baseJson.getJSONObject(PRODUCT).getJSONObject(PRODUCT_INFO)
        .getJSONArray(CONTRACT_CODES);
        return LocalDate.parse(contractCodesArray.getJSONObject(0).getString(EFFECTIVE_DATE));
        } catch (JSONException e) {
        log.info("Provided JSON does not contain product or productInfo or contractCodes.");
        return contractEffectiveDate;
        }
        }

    private HttpHeaders getAuthorization(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(token != null){
            headers.set("Authorization",token.startsWith("Bearer") ? token : "Bearer " + token);
        }
        return headers;
    }

        }

