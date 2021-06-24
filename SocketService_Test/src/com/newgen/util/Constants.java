package com.newgen.util;

public interface Constants {

    //Service Names
    String TOKEN_VALIDATION = "TOKENVALIDATION";
    String REMOVE_LIEN = "REMOVELIEN";
    String CP_POSTING ="postRequestToFinacleCp";
    String FETCH_LIEN = "FETCHLIEN";
    String OLD_CURRENT_ACCOUNT = "OLDCURRENTACCOUNT";
    String OLD_SAVINGS_ACCOUNT = "OLDSAVINGACCOUNT";
    String OLD_SPECIAL_ACCOUNT = "OLDSPECIALACCOUNT";
    String CURRENT_ACCOUNT = "CURRENTACCOUNT";
    String SAVINGS_ACCOUNT = "SAVINGACCOUNT";
    String SPECIAL_ACCOUNT = "SPECIALACCOUNT";
    String SEARCH_TRANSACTION = "CISEARCHTRANSACTION";
    String STAFF_LIMIT = "CIGETUSERLIMIT";
    String BVN_REQUEST = "BVNREQUEST";
    String POSTING = "postRequestToFinacle";
    String DC_STAFF_DETAILS = "FetchStaffDetails";
    String IRAW_POSTING = "postIrawRequestToFinacle";
    String IRAW_OFFICE_ACCOUNT_DETAILS = "fetchIrawOfficeAcctDetails";
    String IRAW_STAFF_DETAILS = "fetchIrawstaffDetails";
    String IRAW_ACCOUNT_DETAILS = "fetchIrawAccountDetails";
    String IRAW_INCOME_CHARGE_ACCOUNT_DETAILS = "fetchIrawIncomechargeAccountDetails";
    String IRAW_POST_FINACLE_INCOME_ACCOUNT= "IrawPostFinacleIncomeAccountChargeableList";
    String IRAW_POST_FINACLE_DEBIT_TO_ONE_ACCOUNT = "IrawPostFinacleDebitToOneAccount";
    String LMT_ENHANCEMENT_STAFF_DETAILS_1 ="Fetch_limit_Enh_Details_INITIATORID";
    String LMT_ENHANCEMENT_STAFF_DETAILS_2 ="Fetch_limit_Enh_Details_VERIFIERID";
    String LMT_ENHANCEMENT_STAFF_DETAILS_3 ="Fetch_limit_Enh_Details_verifier2ID";
    String MEMO_STAFF_DETAILS = "FetchMemoStaffDetails";
    String TEMP_STAFF_DETAILS = "fetchSEStaffDetails";
    String AVR_SEARCH = "AVRSearch";
    String UPDATE_REMOVE_FINACLE_FLAG ="UpdateOrRemoveFinacleFlg";
    String VERIFY_CUSTOMER = "VerifyCustomerRequest";
    String UPDATE_RETAIL_RISK_SCORE = "UpdateRetailCustRiskScore";
    String UPDATE_CORP_RISK_SCORE = "UpdateCorpCustRiskScore";
    String PLACE_LIEN = "placeLien";
    String CI_STAFF_DETAILS = "FetchCIStaffDetails";

    //AppCode

    String FINACLE_POSTING_APPCODE = "XferTrnAdd";
    String REMOVE_LIEN_APPCODE = "LRmvLienMod";
    String FETCH_LIEN_APPCODE = "LLEAcctLienInq";
    String CURRENT_ACCT_APPCODE = "ODAACC_INQ";
    String SAVINGS_ACCT_APPCODE = "SBACC_INQ";
    String SPECIAL_ACCT_APPCODE = "CAAcctInq";
    String FINACLE_TRANSACTION_SEARCH_APPCODE = "FI_tranSearch";
    String STAFF_LIMIT_APPCODE = "GETUSERLIMIT";
    String UPDATE_REMOVE_FINACLE_FLAG_APPCODE = "REMEDIATION";
    String VERIFY_CUST_APPCODE = "VERIFY_CUST";
    String UPDATE_RETAIL_RISK_SCORE_APPCODE = "CPC_FI";
    String UPDATE_CORP_RISK_SCORE_APPCODE = "CORPCIF_MODIFY";
    String PLACE_LIEN_APPCODE = "LLAcctLienAdd";
}
