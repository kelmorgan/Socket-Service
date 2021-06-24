package com.newgen.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

//--------------------------------------------------------------------------------------------------
//********** Communicating Class I/O *****************
public class doComms implements Runnable, Constants {
    private Socket server;
    private static final Logger logger = Logger.getLogger("consoleLogger");

    doComms(Socket server) {
        this.server = server;
    }

    private void writeData(final String input) throws IOException {
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(this.server.getOutputStream());
            final byte[] buffer = new byte[1000000];
            System.out.println("in write" + input.getBytes("UTF-16LE").length);
            if (input.length() > 0) {
                final InputStream in = new ByteArrayInputStream(input.getBytes("UTF-16LE"));
                System.out.println("intostlength" + in);
                StringBuilder str = new StringBuilder();
                int len;
                while ((len = in.read(buffer)) > 0) {
                    str.append(new String(buffer, "UTF-16LE"));
                    dataOutputStream.write(buffer, 0, len);
                }
                System.out.println("--------------writeUTF called--------------------");
                dataOutputStream.close();
            }
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    private void writeDataNew( String output){
        try {
            final DataOutputStream out = new DataOutputStream(this.server.getOutputStream());
            byte[] dataArray = output.getBytes("UTF-8");
            out.writeInt(dataArray.length);
            out.write(dataArray);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String readDataNew (DataInputStream in){
        String data =  "";
        try {
            int dataLength = in.readInt();
            System.out.println("data received length: "+ dataLength);
            byte [] dataBuffer = new byte[dataLength];
            in.readFully(dataBuffer);
            data = new String(dataBuffer,"UTF-8");
            System.out.println("Received data: "+ data);
            return data;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    private String readData(DataInputStream xmlDataInputStream) throws IOException {

        String receivedData = "";
        try {
            byte[] readBuffer = new byte[1000000];
            int num = xmlDataInputStream.read(readBuffer);
            System.out.println("Read DATA num: "+ num);
            if (num > 0) {
                byte[] arrayBytes = new byte[num];
                System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                receivedData = new String(arrayBytes, "UTF-16LE");

                System.out.println("Received message :" + receivedData);
            } else {
                notify();
            }
        } catch (IOException se) {
            se.printStackTrace();
        }
        return receivedData;
    }

    public void run() {
        String input = "";
        XMLParser xmlParser;
        String outputResponseXml = "";
        String varBody = "";
        System.out.println("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());
        logger.info("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());

        String header = "";
        String noValue = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>NO Staff Details are present</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
        String errorHeader = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Error in Fetching Saff Details !</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

        try {

            ConnectToexternalWebService CTEW = new ConnectToexternalWebService();
            ConnectToFinacle connecttoFinacle = new ConnectToFinacle();
            DataInputStream in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            input = readData(in);
            logger.info("input==" + input);
            String[] arg = input.split("~");
            String varServiceName = arg[0];
            String inputXML = arg[1];

            logger.info("inputXML==" + inputXML);
            logger.info("callName==" + varServiceName);
            Properties urlFile = new Properties();
            urlFile.load(new FileInputStream("config.properties"));

            switch (varServiceName){
                case TOKEN_VALIDATION:{
                    logger.info("Welcome To tokenvalidation");
                    String result = "";
                    try {
                        result = CTEW.callService(inputXML, varServiceName);
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = e.getMessage();
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case REMOVE_LIEN:{
                    logger.info("welcome to REMOVE LIEN SERVICE");
                    String result = "";
                     String urlString = urlFile.getProperty("REMOVELIENURL");
                    logger.info("urlString ::" + urlString);

                    try {
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, REMOVE_LIEN_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);

                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case CP_POSTING: {
                    logger.info("welcome to Postng to Finacle on CP");
                    String result = "";
                    String urlString = urlFile.getProperty("CPPOSTURL");
                    logger.info("urlString ::" + urlString);

                    try {
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_POSTING_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);

                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
               case FETCH_LIEN: {
                   logger.info("Welcome To FETCH LIEN api call");
                   String result = "";
                   try {

                       String urlString = urlFile.getProperty("FETCHLIENURL");
                       logger.info("urlString ::" + urlString);

                       outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FETCH_LIEN_APPCODE);
                       logger.info("Response Final==" + outputResponseXml);
                       result = outputResponseXml;
                       logger.info("result:::--" + result);
                       System.out.println("result:::--" + result);
                       writeData(result);
                   } catch (Exception e) {
                       result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                       logger.info("result:::--" + result);
                       System.out.println("result:::--" + result);
                       writeData(result);
                   }
                    break;
                }
                case OLD_CURRENT_ACCOUNT:{
                    logger.info("Welcome To FETCH CURRENT ACCT api call");
                    String result;
                    try {
                       String urlString = urlFile.getProperty("OLDCURRENTACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, CURRENT_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case OLD_SAVINGS_ACCOUNT:{
                    logger.info("Welcome To FETCH SAVINGS ACCT api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("OLDSAVINGACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, SAVINGS_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case OLD_SPECIAL_ACCOUNT:{
                    logger.info("Welcome To FETCH SPECIAL api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("OLDSPECIALACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, SPECIAL_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case CURRENT_ACCOUNT:{
                    logger.info("Welcome To FETCH CURRENT ACCT api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("CURRENTACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, CURRENT_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case SAVINGS_ACCOUNT:{
                    logger.info("Welcome To FETCH SAVINGS ACCT api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("SAVINGACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, SAVINGS_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case SPECIAL_ACCOUNT: {
                    logger.info("Welcome To FETCH SPECIAL api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("SPECIALACCOUNTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, SPECIAL_ACCT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case SEARCH_TRANSACTION:{
                    logger.info("Welcome To Search Transaction api call");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("SEARCHTRANSURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_TRANSACTION_SEARCH_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case STAFF_LIMIT: {
                    logger.info("Welcome To Get User Limit");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("CIGETUSERLIMIT");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, STAFF_LIMIT_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case BVN_REQUEST: {
                    logger.info("Welcome To bvnrequest");
                    String result;
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = e.getMessage();
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case POSTING:{
                    logger.info("welcome to Postng to Finacle on CI");
                    String result;
                    String urlString;
                    try {
                        urlString = urlFile.getProperty("CIPOSTSERVICEURL");
                        logger.info("urlString ::" + urlString);
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_POSTING_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                    break;
                }
                case DC_STAFF_DETAILS:{
                    logger.info("Welcome To FetchStaffDetails");
                    String result;
                    String StaffID = arg[2];
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String DEPARTMENT = xmlParser.getValueOf("ns0:DEPARTMENT");
                        logger.info("DEPARTMENT:::" + DEPARTMENT);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);
                        String GRADE = xmlParser.getValueOf("ns0:GRADE");
                        logger.info("GRADE:::" + GRADE);
                        String phone_num = xmlParser.getValueOf("ns0:phone_num");
                        logger.info("phone_num:::" + phone_num);
                        String EMAIL_ADDRESS = xmlParser.getValueOf("ns0:EMAIL_ADDRESS");
                        logger.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                        String REPORTING_MANAGER = xmlParser.getValueOf("ns0:REPORTING_MANAGER");
                        logger.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                        String REPORTING_MANAGER_NO = xmlParser.getValueOf("ns0:REPORTING_MANAGER_NO");
                        logger.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);

                        if (("".equalsIgnoreCase(FULL_NAME)) && ("".equalsIgnoreCase(DEPARTMENT)) && ("".equalsIgnoreCase(COST_CENT)) && ("".equalsIgnoreCase(GRADE)) && ("".equalsIgnoreCase(phone_num)) && ("".equalsIgnoreCase(EMAIL_ADDRESS)) && ("".equalsIgnoreCase(REPORTING_MANAGER)) && ("".equalsIgnoreCase(REPORTING_MANAGER_NO))) {
                            result = "SUCCESS~" + "<root>" + noValue + "<StaffDetails><SanctionDate></SanctionDate><Department>" + DEPARTMENT + "</Department><Email>" + EMAIL_ADDRESS + "</Email><Address></Address><StaffID>" + StaffID + "</StaffID><PhoneNo>" + phone_num + "</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>" + GRADE + "</Grade><CaseOutCome></CaseOutCome><StaffName>" + FULL_NAME + "</StaffName><ReportingManager>" + REPORTING_MANAGER + "</ReportingManager></StaffDetails></root>";
                            logger.info("result:::--" + result);
                            System.out.println("result:::--" + result);
                            writeData(result);
                        } else {
                            header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion>"
                                    + "<RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId>"
                                    + "<RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo>"
                                    + "<ReturnCode>0000</ReturnCode><ReturnDesc>Staff Details Fetch Successfully</ReturnDesc>"
                                    + "<MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1>"
                                    + "<Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                            result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>" + DEPARTMENT + "</Department><Email>" + EMAIL_ADDRESS + "</Email><Address></Address><StaffID>" + StaffID + "</StaffID><PhoneNo>" + phone_num + "</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>" + GRADE + "</Grade><CaseOutCome></CaseOutCome><StaffName>" + FULL_NAME + "</StaffName><ReportingManager>" + REPORTING_MANAGER + "</ReportingManager></StaffDetails></root>";
                            logger.info("result:::--" + result);
                            System.out.println("result:::--" + result);
                            writeData(result);
                        }

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case IRAW_POSTING:{
                    logger.info("welcome to Postng to Finacle on IRAW");
                    String result;
                    String urlString;
                    try {
                        urlString = urlFile.getProperty("CIPOSTSERVICEURL");
                        logger.info("urlString ::" + urlString);
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_POSTING_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case IRAW_OFFICE_ACCOUNT_DETAILS:{
                    logger.info("Welcome To fetchIrawOfficeAcctDetails");
                    String result;
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" +
                                "<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID>\r\n" +
                                " </root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case IRAW_STAFF_DETAILS:{
                    logger.info("Welcome To fetchIrawstaffDetails");
                    String result = "";
                    String StaffID = arg[2];

                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String DEPARTMENT = xmlParser.getValueOf("ns0:DEPARTMENT");
                        logger.info("DEPARTMENT:::" + DEPARTMENT);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);
                        String GRADE = xmlParser.getValueOf("ns0:GRADE");
                        logger.info("GRADE:::" + GRADE);
                        String phone_num = xmlParser.getValueOf("ns0:phone_num");
                        logger.info("phone_num:::" + phone_num);
                        String EMAIL_ADDRESS = xmlParser.getValueOf("ns0:EMAIL_ADDRESS");
                        logger.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                        String REPORTING_MANAGER = xmlParser.getValueOf("ns0:REPORTING_MANAGER");
                        logger.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                        String REPORTING_MANAGER_NO = xmlParser.getValueOf("ns0:REPORTING_MANAGER_NO");
                        logger.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                        header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        result = "SUCCESS~" + "<root>" + header + "<SanctionDate></SanctionDate><Department>" + DEPARTMENT + "</Department><Email>" + EMAIL_ADDRESS + "</Email><Address></Address><StaffID>" + StaffID + "</StaffID><PhoneNo>" + phone_num + "</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>" + GRADE + "</Grade><CaseOutCome></CaseOutCome><StaffName>" + FULL_NAME + "</StaffName><ReportingManager>" + REPORTING_MANAGER + "</ReportingManager><COST_CENT>" + COST_CENT + "</COST_CENT></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case IRAW_ACCOUNT_DETAILS:{
                    logger.info("Welcome To fetchIrawAccountDetails");
                    String result = "";

                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("fetchByAcctNumberResult");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String addressType = xmlParser.getValueOf("addressType");
                        logger.info("addressType:::" + addressType);
                        String CustomerName = xmlParser.getValueOf("CustomerName");
                        logger.info("CustomerName:::" + CustomerName);
                        String SolID = xmlParser.getValueOf("SolID");
                        logger.info("SolID:::" + SolID);
                        String BranchName = xmlParser.getValueOf("BranchName");
                        logger.info("BranchName:::" + BranchName);
                        String AcctType = xmlParser.getValueOf("AcctType");
                        logger.info("AcctType:::" + AcctType);
                        String AccountCurrency = xmlParser.getValueOf("AccountCurrency");
                        logger.info("AccountCurrency:::" + AccountCurrency);
                        String ResponseHeader = xmlParser.getValueOf("ResponseHeader");
                        logger.info("ResponseHeader:::" + ResponseHeader);
                        String Balance = xmlParser.getValueOf("Balance");
                        logger.info("Balance:::" + Balance);
                        String Schm_type = xmlParser.getValueOf("Schm_type");
                        logger.info("Schm_type:::" + Schm_type);
                        String CustomerID = xmlParser.getValueOf("CustomerID");
                        logger.info("CustomerID:::" + CustomerID);


                        header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        result = "SUCCESS~" + "<root>" + header + "\r\n" +
                                "<addressType>" + addressType + "</addressType>\r\n" +
                                "<CustomerName>" + CustomerName + "</CustomerName>\r\n" +
                                "<SolID>" + SolID + "</SolID>\r\n" +
                                "<BranchName>" + BranchName + "</BranchName>\r\n" +
                                "<AcctType>" + AcctType + "</AcctType>\r\n" +
                                "<AccountCurrency>" + AccountCurrency + "</AccountCurrency>\r\n" +
                                "<ResponseHeader>" + ResponseHeader + "</ResponseHeader>\r\n" +
                                "<Phone></Phone>\r\n" +
                                "<AddressLine1/>\r\n" +
                                "<AddressLine2/>\r\n" +
                                "<CityCode/>\r\n" +
                                "<StateCode/>\r\n" +
                                "<CountryCode/>\r\n" +
                                "<Balance>" + Balance + "</Balance>\r\n" +
                                "<Schm_type>" + Schm_type + "</Schm_type>\r\n" +
                                "<Email/>\r\n" +
                                "<CustomerID>" + CustomerID + "</CustomerID>\r\n" +
                                "</root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" +
                                "<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID>\r\n" +
                                " </root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
               case IRAW_INCOME_CHARGE_ACCOUNT_DETAILS:{
                   logger.info("Welcome To fetchIrawIncomechargeAccountDetails");
                   String result = "";

                   try {
                       outputResponseXml = CTEW.callService(inputXML, varServiceName);
                       logger.info("Response Final==" + outputResponseXml);

                       xmlParser = new XMLParser(outputResponseXml);
                       varBody = xmlParser.getValueOf("fetchByAcctNumberResult");
                       xmlParser = new XMLParser(varBody);
                       logger.info("Status of varBody::::" + varBody);
                       String addressType = xmlParser.getValueOf("addressType");
                       logger.info("addressType:::" + addressType);
                       String CustomerName = xmlParser.getValueOf("CustomerName");
                       logger.info("CustomerName:::" + CustomerName);
                       String SolID = xmlParser.getValueOf("SolID");
                       logger.info("SolID:::" + SolID);
                       String BranchName = xmlParser.getValueOf("BranchName");
                       logger.info("BranchName:::" + BranchName);
                       String AcctType = xmlParser.getValueOf("AcctType");
                       logger.info("AcctType:::" + AcctType);
                       String AccountCurrency = xmlParser.getValueOf("AccountCurrency");
                       logger.info("AccountCurrency:::" + AccountCurrency);
                       String ResponseHeader = xmlParser.getValueOf("ResponseHeader");
                       logger.info("ResponseHeader:::" + ResponseHeader);
                       String Balance = xmlParser.getValueOf("Balance");
                       logger.info("Balance:::" + Balance);
                       String Schm_type = xmlParser.getValueOf("Schm_type");
                       logger.info("Schm_type:::" + Schm_type);
                       String CustomerID = xmlParser.getValueOf("CustomerID");
                       logger.info("CustomerID:::" + CustomerID);


                       header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                               + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                               + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                               + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                               + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                               + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                       result = "SUCCESS~" + "<root>" + header + "<fetchByAcctNumberResult>\r\n" +
                               "<addressType>" + addressType + "</addressType>\r\n" +
                               "<CustomerName>" + CustomerName + "</CustomerName>\r\n" +
                               "<SolID>" + SolID + "</SolID>\r\n" +
                               "<BranchName>" + BranchName + "</BranchName>\r\n" +
                               "<AcctType>" + AcctType + "</AcctType>\r\n" +
                               "<AccountCurrency>" + AccountCurrency + "</AccountCurrency>\r\n" +
                               "<ResponseHeader>" + ResponseHeader + "</ResponseHeader>\r\n" +
                               "<Phone></phone>\r\n" +
                               "<AddressLine1/>\r\n" +
                               "<AddressLine2/>\r\n" +
                               "<CityCode/>\r\n" +
                               "<StateCode/>\r\n" +
                               "<CountryCode/>\r\n" +
                               "<Balance>" + Balance + "</Balance>\r\n" +
                               "<Schm_type>" + Schm_type + "</Schm_type>\r\n" +
                               "<Email/>\r\n" +
                               "<CustomerID>" + CustomerID + "</CustomerID>\r\n" +
                               " </fetchByAcctNumberResult></root>";
                       logger.info("result:::--" + result);
                       System.out.println("result:::--" + result);
                       writeData(result);

                   } catch (Exception e) {
                       result = "FAILED~" + "<root>" + errorHeader + " <fetchByAcctNumberResult><addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" +
                               "<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID></fetchByAcctNumberResult>\r\n" +
                               " </root>";
                       logger.info("result:::--" + result);
                       System.out.println("result:::--" + result);
                       writeData(result);
                   }
                }
                break;
                case IRAW_POST_FINACLE_INCOME_ACCOUNT:{
                    logger.info("Welcome To IrawPostFinacleIncomeAccountChargeableList");
                    String result = "";

                    try {
                        String urlString = urlFile.getProperty("IRAWPOSTFINACLEServiceURL");
                        logger.info("urlString ::" + urlString);
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_POSTING_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("XferTrnAddRs");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String TrnDt = xmlParser.getValueOf("TrnDt");
                        logger.info("TrnDt:::" + TrnDt);
                        String TrnId = xmlParser.getValueOf("TrnId");
                        logger.info("TrnId:::" + TrnId);

                        String varBody2 = xmlParser.getValueOf("HostTransaction");
                        xmlParser = new XMLParser(varBody2);
                        logger.info("Status of varBody2::::" + varBody2);
                        String status = xmlParser.getValueOf("Status");
                        logger.info("Status:::" + status);

                        header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        result = "SUCCESS~" + "<root>" + header + "<Status>" + status + "</Status> <TrnDt>" + TrnDt + "</TrnDt><TrnId>" + TrnId + "</TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case IRAW_POST_FINACLE_DEBIT_TO_ONE_ACCOUNT:{
                    logger.info("Welcome To IrawPostFinacleDebitToOneAccount");
                    String result;
                    String urlString;

                    try {
                        urlString = urlFile.getProperty("IRAWPOSTFINACLEServiceURL");
                        logger.info("urlString ::" + urlString);
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString, FINACLE_POSTING_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("XferTrnAddRs");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String TrnDt = xmlParser.getValueOf("TrnDt");
                        logger.info("TrnDt:::" + TrnDt);
                        String TrnId = xmlParser.getValueOf("TrnId");
                        logger.info("TrnId:::" + TrnId);

                        String varBody2 = xmlParser.getValueOf("HostTransaction");
                        xmlParser = new XMLParser(varBody2);
                        logger.info("Status of varBody2::::" + varBody2);
                        String status = xmlParser.getValueOf("Status");
                        logger.info("Status:::" + status);

                        header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                + "<ReturnDesc>Details Fetched Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        result = "SUCCESS~" + "<root>" + header + "<Status>" + status + "</Status> <TrnDt>" + TrnDt + "</TrnDt><TrnId>" + TrnId + "</TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case LMT_ENHANCEMENT_STAFF_DETAILS_1:
                case LMT_ENHANCEMENT_STAFF_DETAILS_2:
                case LMT_ENHANCEMENT_STAFF_DETAILS_3:
                {
                    logger.info("Welcome To LIMIT_ENHANCEMENT  Process custom webservice");


                    String result = "";
                    String requestId = arg[2];
                    logger.info("requestId ::" + requestId);
                    logger.info("inputXML ::" + inputXML);
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);
                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);

                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);

                        if (LMT_ENHANCEMENT_STAFF_DETAILS_1.equalsIgnoreCase(varServiceName)) {
                            if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase("")) {
                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                            } else {

                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>Request Initiator Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                            }
                            result = "SUCCESS~" + "<root>" + header + "<REQUESTINITIATORNAME>" + FULL_NAME + "</REQUESTINITIATORNAME><SOLINITIATOR>" + COST_CENT + "</SOLINITIATOR></root>";
                        } else if (LMT_ENHANCEMENT_STAFF_DETAILS_2.equalsIgnoreCase(varServiceName)) {
                            if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase("")) {
                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                            } else {
                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>Request Verifier Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                            }
                            result = "SUCCESS~" + "<root>" + header + "<REQUESTVERIFIERNAME>" + FULL_NAME + "</REQUESTVERIFIERNAME><sol_id_verifier>" + COST_CENT + "</sol_id_verifier></root>";
                        } else if (LMT_ENHANCEMENT_STAFF_DETAILS_3.equalsIgnoreCase(varServiceName)) {
                            if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase("")) {
                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                            } else {
                                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                        + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                        + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                        + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                        + "<ReturnDesc>verifier2Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                        + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                            }
                            result = "SUCCESS~" + "<root>" + header + "<verifier2Name>" + FULL_NAME + "</verifier2Name><verifier2Sol_ID>" + COST_CENT + "</verifier2Sol_ID></root>";
                        }
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><REQUESTINITIATORNAME></REQUESTINITIATORNAME><sol_id_initiator></sol_id_initiator></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }

                }
                break;
                case MEMO_STAFF_DETAILS:{
                    logger.info("Welcome To FetchMemoStaffDetails");
                    String result;
                    String StaffID = arg[2];
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String DEPARTMENT = xmlParser.getValueOf("ns0:DEPARTMENT");
                        logger.info("DEPARTMENT:::" + DEPARTMENT);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);
                        String GRADE = xmlParser.getValueOf("ns0:GRADE");
                        logger.info("GRADE:::" + GRADE);
                        String phone_num = xmlParser.getValueOf("ns0:phone_num");
                        logger.info("phone_num:::" + phone_num);
                        String EMAIL_ADDRESS = xmlParser.getValueOf("ns0:EMAIL_ADDRESS");
                        logger.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                        String REPORTING_MANAGER = xmlParser.getValueOf("ns0:REPORTING_MANAGER");
                        logger.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                        String REPORTING_MANAGER_NO = xmlParser.getValueOf("ns0:REPORTING_MANAGER_NO");
                        logger.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);


                        if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase("")) {
                            header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                    + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                    + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                    + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                    + "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                    + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                        } else {

                            header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                    + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                    + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                    + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                    + "<ReturnDesc>Staff Details Fetched Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                    + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        }
                        result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>" + DEPARTMENT + "</Department><Email>" + EMAIL_ADDRESS + "</Email><Address></Address><StaffID>" + StaffID + "</StaffID><PhoneNo>" + phone_num + "</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>" + GRADE + "</Grade><CaseOutCome></CaseOutCome><StaffName>" + FULL_NAME + "</StaffName><ReportingManager>" + REPORTING_MANAGER + "</ReportingManager></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case TEMP_STAFF_DETAILS:{
                    logger.info("Welcome To service executive appraisal");
                    String result;


                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:dbReferenceOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);
                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String DEPARTMENT = xmlParser.getValueOf("ns0:DEPT");
                        logger.info("DEPARTMENT:::" + DEPARTMENT);
                        String SOL_ID = xmlParser.getValueOf("ns0:COST_CENTER");
                        logger.info("SOL_ID:::" + SOL_ID);
                        String EMPLOYEE_NUMBER = xmlParser.getValueOf("ns0:EMPLOYEE_NUMBER");
                        logger.info("EMPLOYEE_NUMBER:::" + EMPLOYEE_NUMBER);
                        String JOB_NAME = xmlParser.getValueOf("ns0:JOB_NAME");
                        logger.info("JOB_NAME:::" + JOB_NAME);
                        String LOCATIONS = xmlParser.getValueOf("ns0:LOCATIONS");
                        logger.info("LOCATIONS:::" + LOCATIONS);
                        String REPORTING_MANAGER_NAME = xmlParser.getValueOf("ns0:SUP_FULL_NAME");
                        logger.info("REPORTING_MANAGER_NAME:::" + REPORTING_MANAGER_NAME);
                        String REPORTING_MANAGER_USERID = xmlParser.getValueOf("ns0:SUP_EMP_NUMBER");
                        logger.info("REPORTING_MANAGER_USERID:::" + REPORTING_MANAGER_USERID);
                        String REPORTING_MANAGER_JOBROLE = xmlParser.getValueOf("ns0:SUP_JOB");
                        logger.info("REPORTING_MANAGER_JOBROLE:::" + REPORTING_MANAGER_JOBROLE);
                        header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                        result = "SUCCESS~" + "<root>" + header + "<Department>" + DEPARTMENT + "</Department><Solid>" + SOL_ID + "</Solid><StaffID>" + EMPLOYEE_NUMBER + "</StaffID><JobName>" + JOB_NAME + "</JobName><Location>" + LOCATIONS + "</Location><StaffCategory></StaffCategory><ReportingMgrName>" + REPORTING_MANAGER_NAME + "</ReportingMgrName><StaffName>" + FULL_NAME + "</StaffName><ReportingMgrUserID>" + REPORTING_MANAGER_USERID + "</ReportingMgrUserID><ReportingMgrJobRole>" + REPORTING_MANAGER_JOBROLE + "</ReportingMgrJobRole></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<Department></Department><Solid></Solid><Address></Address><StaffID></StaffID><JobName></JobName><Location></Location><StaffCategory></StaffCategory><ReportingMgrName></ReportingMgrName><StaffName></StaffName><ReportingMgrUserID></ReportingMgrUserID><ReportingMgrJobRole></ReportingMgrJobRole></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case AVR_SEARCH:{
                    logger.info("Welcome To AVRSearch");
                    String result;
                    try {
                        outputResponseXml = CTEW.callService(inputXML, varServiceName);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = e.getMessage();
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case UPDATE_REMOVE_FINACLE_FLAG:{
                    logger.info("Welcome TO UPDATE/REMOVE FINACLE FLAG");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("ARFINACLEAPIURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, UPDATE_REMOVE_FINACLE_FLAG_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case VERIFY_CUSTOMER:{
                    logger.info("Welcome TO VERIFY CUSTOMER REQUEST");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("ARFINACLEAPIURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, VERIFY_CUST_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case UPDATE_RETAIL_RISK_SCORE:{
                    logger.info("Welcome TO UPDATE Retail customer risk score");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("ARFINACLEAPIURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, UPDATE_CORP_RISK_SCORE_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case UPDATE_CORP_RISK_SCORE:{
                    logger.info("Welcome TO UPDATE Retail customer risk score");
                    String result;
                    try {
                        String urlString;
                        urlString = urlFile.getProperty("ARFINACLETESTURL");
                        logger.info("urlString ::" + urlString);

                        outputResponseXml = connecttoFinacle.callServiceFI(inputXML, urlString, UPDATE_CORP_RISK_SCORE_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case PLACE_LIEN:{
                    logger.info("welcome to Add Lien API");
                    String result;
                    String urlString;
                    try {
                        urlString = urlFile.getProperty("placeLien");
                        logger.info("urlString ::" + urlString);
                        outputResponseXml = connecttoFinacle.callService(inputXML, urlString,PLACE_LIEN_APPCODE);
                        logger.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<ErrorDetail>\r\n" +
                                "<ErrorCode>008</ErrorCode>\r\n" +
                                "<ErrorDesc>Lien Not Placed Succesfully</ErrorDesc>\r\n" +
                                "<ErrorSource>acctLienMaintCrit.acctNum.foracid</ErrorSource>\r\n" +
                                "<ErrorType>BE</ErrorType>\r\n" +
                                "</ErrorDetail>\r\n" +
                                "</FIBusinessException>\r\n" +
                                "</Error>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(result);
                    }
                }
                break;
                case CI_STAFF_DETAILS:{
                    logger.info("Welcome To FetchCIStaffDetails");
                    String result = "";
                    String StaffID = arg[2];
                    try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        logger.info("Response Final==" + outputResponseXml);

                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        logger.info("before parser varBody:::: " + varBody);
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody:::: " + varBody);
                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String DEPARTMENT = xmlParser.getValueOf("ns0:DEPARTMENT");
                        logger.info("DEPARTMENT:::" + DEPARTMENT);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);
                        String GRADE = xmlParser.getValueOf("ns0:GRADE");
                        logger.info("GRADE:::" + GRADE);
                        String phone_num = xmlParser.getValueOf("ns0:phone_num");
                        logger.info("phone_num:::" + phone_num);
                        String EMAIL_ADDRESS = xmlParser.getValueOf("ns0:EMAIL_ADDRESS");
                        logger.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                        String REPORTING_MANAGER = xmlParser.getValueOf("ns0:REPORTING_MANAGER");
                        logger.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                        String REPORTING_MANAGER_NO = xmlParser.getValueOf("ns0:REPORTING_MANAGER_NO");
                        logger.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                        String jobTitle = xmlParser.getValueOf("ns0:JOB");
                        logger.info("jobTitle:::" + jobTitle);

                        if(FULL_NAME.equals("")){

                            header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                    + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                    + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                    + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                    + "<ReturnDesc>Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                    + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                        }
                        else{
                            header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                                    + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                                    + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                                    + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                                    + "<ReturnDesc>Sucessful</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                                    + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                        }
                        result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager><COST_CENT>"+COST_CENT+"</COST_CENT><JOB>"+jobTitle+"</JOB></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData( result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager><COST_CENT></COST_CENT><JOB></JOB></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData( result);
                    }
                }
                break;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(new DataOutputStream(server.getOutputStream()));
            } catch (Exception e) {
                System.out.println("Data Output Stream initialization error");
                NG_Socket_Service.err = "Data Output Stream initialization error" + e;
                e.printStackTrace();
            }
            System.out.println("Catch 19 IOException on socket listen: " + ioe);
            NG_Socket_Service.err = "IOException on socket listen: " + ioe;
            input = NG_Socket_Service.err;
            try {
                writeData(input);
            } catch (Exception e) {
                NG_Socket_Service.err = "Write Data Exception " + e;
            }
            logger.info("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(doComms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (server != null) {
                    System.out.println("====Closing local Cient Socket=======" + server.getRemoteSocketAddress());
                    logger.info("====Closing local Cient Socket=======" + server.getRemoteSocketAddress());
                    server.close();
                    server = null;
                    System.out.println("====Successfuly Closed=======");
                    logger.info("====Successfuly Closed=======");

                }
            } catch (Exception e) {
                System.out.println("Exception " + e);
                logger.info("Exception " + e);
                NG_Socket_Service.err = e.toString();
                //input="<Status>FAILURE</Status>"+ N.err;
                input = NG_Socket_Service.err;
            }
        }
    }

}
