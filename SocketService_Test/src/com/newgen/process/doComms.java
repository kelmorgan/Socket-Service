package com.newgen.process;

import com.newgen.main.NG_Socket_Service;
import com.newgen.makeCall.ConnectToFinacle;
import com.newgen.makeCall.ConnectToExternalWebService;
import com.newgen.util.Constants;
import com.newgen.util.LoadProp;
import com.newgen.util.XMLParser;
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

    public doComms(Socket server) {
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
        String input;
        XMLParser xmlParser;
        String outputResponseXml;
        String varBody;
        System.out.println("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());
        logger.info("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());

        String header;
        String noValue = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>NO Staff Details are present</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
        String errorHeader = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Error in Fetching Saff Details !</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

        try {

            ConnectToExternalWebService CTEW = new ConnectToExternalWebService();
            DataInputStream in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            input = readData(in);
            logger.info("input==" + input);
            String[] inputs = input.split("~");
            String serviceName = inputs[0];
            String request = inputs[1];

            logger.info("request==" + request);
            logger.info("callName==" + serviceName);

            switch (serviceName){
                case LEGACY_ACCOUNT:{
                    logger.info("Welcome To Legacy account details service");
                    sendSoapRequest(request,LoadProp.fetchLegacyAccountUrl,LoadProp.fetchLegacyAccountAction);
                    break;
                }
                case STAFF_DETAILS: {
                    logger.info("Welcome To staff details service");
                    sendSoapRequest(request,LoadProp.fetchStaffDetailsUrl,empty);
                    break;
                }
                case FREEZE_ACCOUNT:{
                        logger.info("Welcome To Freeze account service");
                    sendFinacleBpmRequest(request, LoadProp.finaclePreProdBpmUrl,LoadProp.freezeAccountAppCode);
                    break;
                }
                case TOKEN_VALIDATION:{
                    logger.info("Welcome To token validation");
                    sendSoapRequest(request, LoadProp.tokenAuthenticationUrl,LoadProp.tokenAuthenticationAction);
                    break;
                }
                case REMOVE_LIEN:{
                    logger.info("welcome to REMOVE LIEN SERVICE");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.removeLienAppCode);
                    break;
                }
                case CP_POSTING: {
                    logger.info("welcome to Postng to Finacle on CP");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.postAppCode);
                    break;
                }
               case FETCH_LIEN: {
                   logger.info("Welcome To FETCH LIEN api call");
                   sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.fetchLienAppCode);
                    break;
                }

                case CURRENT_ACCOUNT:{
                    logger.info("Welcome To FETCH CURRENT ACCT api call");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.fetchOdaAcctAppCode);
                    break;
                }
                case SAVINGS_ACCOUNT:{
                    logger.info("Welcome To FETCH SAVINGS ACCT api call");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.fetchSbaAcctAppCode);
                    break;
                }
                case SPECIAL_ACCOUNT: {
                    logger.info("Welcome To FETCH SPECIAL api call");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.fetchCaaAcctAppCode);
                    break;
                }
                case SEARCH_TRANSACTION:{
                    logger.info("Welcome To Search Transaction api call");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.searchTransactionAppCode);
                    break;
                }
                case STAFF_LIMIT: {
                    logger.info("Welcome To Get User Limit");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl, LoadProp.fetchStaffTransactionLimitAppCode);
                    break;
                }
                case BVN_REQUEST: {
                    logger.info("Welcome To bvnrequest");
                    sendSoapRequest(request,LoadProp.fetchAccountBvnUrl,LoadProp.bvnAction);
                    break;
                }
                case POSTING:{
                    logger.info("welcome to Finacle Posting");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.postAppCode);
                    break;
                }
                case DC_STAFF_DETAILS:{
                    logger.info("Welcome To FetchStaffDetails");
                    String result;
                    String StaffID = inputs[2];
                    try {
                       // outputResponseXml = CTEW.callService(request, serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchStaffDetailsUrl,empty);
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
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.postAppCode);
                    break;
                }
                case IRAW_OFFICE_ACCOUNT_DETAILS:{
                    logger.info("Welcome To fetchIrawOfficeAcctDetails");
                    sendSoapRequest(request,LoadProp.fetchInternalAccountUrl,empty);
                    break;
                }

                case IRAW_STAFF_DETAILS:{
                    logger.info("Welcome To fetchIrawstaffDetails");
                    String result = "";
                    String StaffID = inputs[2];

                    try {
                        //outputResponseXml = CTEW.callService(request, serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchStaffDetailsUrl,empty);
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
                case LMT_ENHANCEMENT_STAFF_DETAILS_1:
                case LMT_ENHANCEMENT_STAFF_DETAILS_2:
                case LMT_ENHANCEMENT_STAFF_DETAILS_3:
                {
                    logger.info("Welcome To LIMIT_ENHANCEMENT  Process custom webservice");


                    String result = "";
                    String requestId = inputs[2];
                    logger.info("requestId ::" + requestId);
                    logger.info("request ::" + request);
                    try {
                        //outputResponseXml = CTEW.callService(request, serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchStaffDetailsUrl,empty);
                        logger.info("Response Final==" + outputResponseXml);
                        xmlParser = new XMLParser(outputResponseXml);
                        varBody = xmlParser.getValueOf("ns0:EBSTSTOutput");
                        xmlParser = new XMLParser(varBody);
                        logger.info("Status of varBody::::" + varBody);

                        String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME");
                        logger.info("FULL_NAME:::" + FULL_NAME);
                        String COST_CENT = xmlParser.getValueOf("ns0:COST_CENT");
                        logger.info("COST_CENT:::" + COST_CENT);

                        if (LMT_ENHANCEMENT_STAFF_DETAILS_1.equalsIgnoreCase(serviceName)) {
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
                        } else if (LMT_ENHANCEMENT_STAFF_DETAILS_2.equalsIgnoreCase(serviceName)) {
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
                        } else if (LMT_ENHANCEMENT_STAFF_DETAILS_3.equalsIgnoreCase(serviceName)) {
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
                    String StaffID = inputs[2];
                    try {
                       // outputResponseXml = CTEW.callService(request, serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchStaffDetailsUrl,empty);
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
                       // outputResponseXml = CTEW.callService(request, serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchNonCorStaffDetailsUrl,empty);
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
                    sendSoapRequest(request,LoadProp.avrSearchUrl,empty);
                    break;
                }
                case UPDATE_REMOVE_FINACLE_FLAG:{
                    logger.info("Welcome TO UPDATE/REMOVE FINACLE FLAG");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.updateRemoveFinacleFlagAppCode);
                    break;
                }
                case VERIFY_CUSTOMER:{
                    logger.info("Welcome TO VERIFY CUSTOMER REQUEST");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.verifyCustomerAppCode);
                    break;
                }
                case UPDATE_RETAIL_RISK_SCORE:{
                    logger.info("Welcome TO UPDATE Retail customer risk score");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.updateRetailRiskScoreAppCode);
                    break;
                }
                case UPDATE_CORP_RISK_SCORE:{
                    logger.info("Welcome TO UPDATE Retail customer risk score");
                    sendFinacleFiRequest(request,LoadProp.finaclePreProdFiUrl,LoadProp.updateCorpRiskScoreAppCode);
                    break;
                }
                case PLACE_LIEN:{
                    logger.info("welcome to Place Lien API");
                    sendFinacleBpmRequest(request,LoadProp.finaclePreProdBpmUrl,LoadProp.placeLienAppCode);
                    break;
                }
                case CI_STAFF_DETAILS:{
                    logger.info("Welcome To FetchCIStaffDetails");
                    String result = "";
                    String StaffID = inputs[2];
                    try {
                        //outputResponseXml = CTEW.callService(request,serviceName);
                        outputResponseXml = CTEW.callService(request,LoadProp.fetchStaffDetailsUrl,empty);
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
                        result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager><ReportingManagerNo>"+REPORTING_MANAGER_NO+"</ReportingManagerNo><COST_CENT>"+COST_CENT+"</COST_CENT><JOB>"+jobTitle+"</JOB></StaffDetails></root>";
                        logger.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData( result);

                    } catch (Exception e) {
                        result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager><ReportingManagerNo></ReportingManagerNo><COST_CENT></COST_CENT><JOB></JOB></StaffDetails></root>";
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
                com.newgen.main.NG_Socket_Service.err = "Data Output Stream initialization error" + e;
                e.printStackTrace();
            }
            System.out.println("Catch 19 IOException on socket listen: " + ioe);
            com.newgen.main.NG_Socket_Service.err = "IOException on socket listen: " + ioe;
            input = com.newgen.main.NG_Socket_Service.err;
            try {
                writeData(input);
            } catch (Exception e) {
                com.newgen.main.NG_Socket_Service.err = "Write Data Exception " + e;
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
                com.newgen.main.NG_Socket_Service.err = e.toString();
                //input="<Status>FAILURE</Status>"+ N.err;
                input = NG_Socket_Service.err;
            }
        }
    }

    private void sendFinacleBpmRequest(String request, String endpoint, String appCode) throws IOException {
        try {
            String  output = new ConnectToFinacle().callService(request,endpoint,appCode);
            logger.info("Response Final==" + output);
            System.out.println("result:::--" + output);
            writeData(output);
        } catch (Exception e) {
            String result = "FAILED~" + "<root><Status>Failed</Status><ErrorMsg>Error occurred in SocketService contact Admin</ErrorMsg></root>";
            logger.info("result:::--" + result);
            System.out.println("result:::--" + result);
            writeData(result);
        }
    }

    private void sendFinacleFiRequest (String request,String endpoint,String appCode) throws IOException {
        try {
            String  output = new ConnectToFinacle().callServiceFI(request, endpoint,appCode);
            logger.info("Response Final==" + output);
            System.out.println("result:::--" + output);
            writeData(output);
        } catch (Exception e) {
            String result = "FAILED~" + "<root><Status>Failed</Status><ErrorMsg>Error occurred in SocketService contact Admin</ErrorMsg></root>";
            logger.info("result:::--" + result);
            System.out.println("result:::--" + result);
            writeData(result);
        }
    }
    private void sendSoapRequest(String request, String endpoint, String soapAction) throws IOException {

        try {
            String output = new ConnectToExternalWebService().callService(request, endpoint,soapAction);
            logger.info("result:::--" + output);
            System.out.println("result:::--" + output);
            writeData(output);
        } catch (Exception e) {
            String result = "FAILED~" + "<root><Status>Failed</Status><ErrorMsg>Error occurred in SocketService contact Admin</ErrorMsg></root>";
            logger.info("result:::--" + result);
            System.out.println("result:::--" + result);
            writeData(result);
        }
    }
}
