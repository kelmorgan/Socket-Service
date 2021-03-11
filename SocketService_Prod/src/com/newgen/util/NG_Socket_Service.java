package com.newgen.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class NG_Socket_Service {
//check in
	//check miranda
    private static int port, maxConnections = 10;
    public static String RunMode, s, err;
    private static Logger logger = Logger.getLogger("consoleLogger");
    static {
        PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "Config" + File.separator + "log4j_WebServiceWrapper.properties");
    }

    public static void main(String[] args) {
        port = 4445;
        int k = 0;
        ServerSocket listener = null;
        Socket server = null;

        //******* Establish connection *************
        try {
            listener = new ServerSocket(port);


            while (true) {
                k = k + 1;
                logger.info("Waiting for Request Count--" + k);
                System.out.println("Waiting for Request Count--" + k);
                server = listener.accept();
                doComms conn_c = new doComms(server);
                Thread t = new Thread(conn_c);
                t.start();
            }


        } catch (IOException ioe) {
            logger.info("IOException on socket listen:11111111 " + ioe);
            System.out.println("Catch  3 IOException on socket listen:11111111 " + ioe);
            ioe.printStackTrace();
        } finally {
            try {
                if (listener != null) {
                    listener.close();
                    listener = null;
                    System.out.println("Closing Listener");
                    logger.info("Closing Listener");
                }
                if (server != null) {
                    server.close();
                    server = null;
                    System.out.println("Closing Server Socket");
                    logger.info("Closing Server Socket");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Exception " + e.toString());
                System.out.println("Exception " + e.toString());
            }
        }
    }
}
//--------------------------------------------------------------------------------------------------
//********** Communicating Class I/O *****************
class doComms implements Runnable {
    private Socket server;
    private String input;
    private static Logger loggern = Logger.getLogger("consoleLogger");
    Properties property = new Properties();
    static String XMLFileName = null;
    static String XSDFileName = null;
    static String XSDFileNameArray[] = null;
    static int flag = 0;
    NG_Socket_Service N = new NG_Socket_Service();

    doComms(Socket server) {
        this.server = server;
    }

    public void writeData(final DataOutputStream xmlDataOutputStream, final String input) throws IOException {
        try {
            final DataOutputStream dout = new DataOutputStream(this.server.getOutputStream());
            final byte[] buffer = new byte[1000000];
            final String return_message = input;
            System.out.println("in write" + return_message.getBytes("UTF-16LE").length);
            if (return_message != null && return_message.length() > 0) {
                final InputStream in = new ByteArrayInputStream(return_message.getBytes("UTF-16LE"));
                System.out.println("intostlength" + in .toString());
                String str = "";
                int len;
                while ((len = in .read(buffer)) > 0) {
                    str = String.valueOf(str) + new String(buffer, "UTF-16LE");
                    dout.write(buffer, 0, len);
                }
                System.out.println("--------------writeUTF called--------------------");
                dout.close();
            }
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }
    
    public void writeDataCRB(DataOutputStream xmlDataOutputStream,String input) throws IOException

    {
        {

             try {
                DataOutputStream dout=new DataOutputStream(server.getOutputStream());  
                String return_message = input;
                 if (return_message != null && return_message.length() > 0) {
                     {
                                dout.write(return_message.getBytes("UTF-16LE"));

                                dout.flush();

                     }         

                 };

                 

             } catch (IOException i) {

                 i.printStackTrace();         

             } catch (Exception ie) {

                 ie.printStackTrace();

             } 

         }

    }

    public String readData(DataInputStream xmlDataInputStream) throws IOException {

        String recvedMessage = "";
        try {
            byte[] readBuffer = new byte[1000000];
            int num = xmlDataInputStream.read(readBuffer);
            if (num > 0) {
                byte[] arrayBytes = new byte[num];
                System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                recvedMessage = new String(arrayBytes, "UTF-16LE");

                System.out.println("Received message :" + recvedMessage);
            } else {

                notify();
            };

        } catch (SocketException se) {
            se.printStackTrace();

        } catch (IOException i) {
            i.printStackTrace();
        }
        return recvedMessage;
    }

    //******* Get a particaular tag value from input xml *****************
    public String getvaluebytag(String xml, String TagName) {
        String retval = "";
        try {} catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception " + ex.toString());
            N.err = ex.toString();
            return "";
        }
        return retval;
    }

    //********** Convert string to upper case ****************
    public String toUpperCase(String str, int begin, int end) throws StringIndexOutOfBoundsException {
        String returnStr = "";
        try {
            int count = str.length();
            char strChar[] = new char[count];
            str.getChars(0, count, strChar, 0);
            while (count-- > 0) {
                strChar[count] = Character.toUpperCase(strChar[count]);
            }
            returnStr = new String(strChar);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Exception " + e.toString());
            N.err = e.toString();
            loggern.info("Exception " + e.toString());
        }
        return returnStr;
    }

    //*********** Read a file (batch mode) ***************
    public String readConfig(String fileName) {
        String str = "";
        try {
            loggern.info("XML FileName and Path Read = " + fileName);
            System.out.println("XML FileName and Path Read = " + fileName);
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String Record = "";
            while ((Record = br.readLine()) != null) {
                str = str + Record;
            }
            System.out.println("String Converted from XML = " + str);
            loggern.info("String Converted from XML = " + str);
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found exception in Read Function");
            loggern.info("File not found exception in Read Function");
            N.err = e.toString();
        } catch (Exception e) {
            e.printStackTrace();
            loggern.info("Exception in Read Function = " + e);
            System.out.println("Exception in Read Function = " + e);
            N.err = e.toString();
        }

        return str;

    }

    //*************** Validate XML **************
    public void XmlValidator() {


        //XML file name that needs to be validated.
        XMLFileName = getvaluebytag(input, "xmlFilename");

        //Split the filename separated by comma in case multiple XSD filenames occured.
        XSDFileNameArray = XSDFileName.split(",");

        System.out.println("XMLFileName  = " + XMLFileName);
        loggern.info("XMLFileName  = " + XMLFileName);

        for (int i = 0; i < XSDFileNameArray.length; i++) {
            System.out.println("XSDFileName" + i + " = " + XSDFileNameArray[i]);
            loggern.info("XSDFileName" + i + " = " + XSDFileNameArray[i]);
        }

    }
    String entity_type = "";
    public void run() {
        input = "";
        XMLParser xmlParser;
        XMLParser xmlParser2;
        String outputResponseXml = "";
        String varBody = "";
        System.out.println("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());
        loggern.info("==========Start Listening for local Port==========:" + server.getRemoteSocketAddress());

        String header = "";
        String noValue = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>NO Staff Details are present</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
        String errorHeader = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Error in Fetching Saff Details !</ReturnDesc><MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
        try {

            ConnectToexternalWebService CTEW = new ConnectToexternalWebService();
            ConnectToFinacle connecttoFinacle= new ConnectToFinacle();
            DataInputStream in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            DataOutputStream out = new DataOutputStream(new DataOutputStream(server.getOutputStream()));
            //String sessionID = comm.connectToLOS();
            
            input = readData( in );
            loggern.info("input==" + input);
            String arg[] = input.split("~");
            String varServiceName = arg[0];
            String inputXML = arg[1];

            loggern.info("inputXML==" + inputXML);
            loggern.info("callName==" + varServiceName);
            
            
            /*ACCOUNT RECREATION  Process*/   /*SEND ADDRESS FOR SEACH*/
			
 		   if ("AVRSearch".equalsIgnoreCase(varServiceName)) {
            loggern.info("Welcome To AVRSearch");
            String result = "";
            try {
                outputResponseXml = CTEW.callService(inputXML,varServiceName);
                loggern.info("Response Final==" + outputResponseXml);
                    result = outputResponseXml;
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                   /* header="<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion>"
                            + "<RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId>"
                            + "<RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo>"
                            + "<ReturnCode>0000</ReturnCode><ReturnDesc>Staff Details Fetch Successfully</ReturnDesc>"
                            + "<MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1>"
                            + "<Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";*/
            } catch (Exception e) {
                result = e.getMessage();
                loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);
            }
        }
 		//UPDATE OR REMOVE FIN FLAG   
 		   if ("UpdateOrRemoveFinacleFlg".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome TO UPDATE/REMOVE FINACLE FLAG");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("ARFINACLEAPIURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"REMEDIATION");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
 		   
 		  //AR Verify customer REquest
		   if ("VerifyCustomerRequest".equalsIgnoreCase(varServiceName)) {
               loggern.info("Welcome TO VERIFY CUSTOMER REQUEST");
               String result = "";
               try {
               	 Properties urlFile = new Properties();
                    urlFile.load(new FileInputStream("config.properties"));
                    String urlString="";
                    urlString = urlFile.getProperty("ARFINACLEAPIURL");
                    loggern.info("urlString ::"+urlString);
                    
                   outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"VERIFY_CUST");
                   loggern.info("Response Final==" + outputResponseXml);
                       result = outputResponseXml;
                       loggern.info("result:::--" + result);
                       System.out.println("result:::--" + result);
                       writeData(out, result);
               } catch (Exception e) {
                   result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                   loggern.info("result:::--" + result);
                   System.out.println("result:::--" + result);
                   writeData(out, result);
               }
           }
		   
 		   //update retail customer
 		   if ("UpdateRetailCustRiskScore".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome TO UPDATE Retail customer risk score");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("ARFINACLEAPIURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"CPC_FI");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
 		   
 		 //update Corp customer
 		   if ("UpdateCorpCustRiskScore".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome TO UPDATE Retail customer risk score");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("ARFINACLETESTURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"CORPCIF_MODIFY");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
 		   //end AR apis
 		   
 		   
            /*Get Customer Account Details*/
            if ("CURRENTACCOUNT".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FETCH CURRENT ACCT api call");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("CURRENTACCOUNTURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"ODAACC_INQ");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            if ("SAVINGACCOUNT".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FETCH SAVINGS ACCT api call");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("SAVINGACCOUNTURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"SBACC_INQ");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            if ("SPECIALACCOUNT".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FETCH SPECIAL api call");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("SPECIALACCOUNTURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"CAAcctInq");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            
            
            /*CI search Transaction*/
            if ("CISEARCHTRANSACTION".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To Search Transaction api call");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("SEARCHTRANSURL");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"FI_tranSearch");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured check socket service</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            
            /*CI Process*/
            if ("CIGETUSERLIMIT".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To Get User Limit");
                String result = "";
                try {
                	 Properties urlFile = new Properties();
                     urlFile.load(new FileInputStream("config.properties"));
                     String urlString="";
                     urlString = urlFile.getProperty("CIGETUSERLIMIT");
                     loggern.info("urlString ::"+urlString);
                     
                    outputResponseXml = connecttoFinacle.callServiceFI(inputXML,urlString,"GETUSERLIMIT");
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root><Failed>Exception Occured</Failed></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            
            /*AS BVN Calls*/
            if ("BVNREQUEST".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To bvnrequest");
                String result = "";
                try {
                    outputResponseXml = CTEW.callService(inputXML,varServiceName);
                    loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                        loggern.info("result:::--" + result);
                        System.out.println("result:::--" + result);
                        writeData(out, result);
                       /* header="<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion>"
                                + "<RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId>"
                                + "<RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo>"
                                + "<ReturnCode>0000</ReturnCode><ReturnDesc>Staff Details Fetch Successfully</ReturnDesc>"
                                + "<MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1>"
                                + "<Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";*/
                } catch (Exception e) {
                    result = e.getMessage();
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            
            /*CI Posting Calls*/
            if("postRequestToFinacle".equalsIgnoreCase(varServiceName)){
                loggern.info("welcome to Postng to Finacle on CI");
                String result = "";
                Properties urlFile = new Properties();
                urlFile.load(new FileInputStream("config.properties"));
                String urlString="";
                urlString = urlFile.getProperty("CIPOSTSERVICEURL");
                loggern.info("urlString ::"+urlString);

                try {
                    outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"XferTrnAdd");
                    loggern.info("Response Final==" + outputResponseXml);

                    /*header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                            + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                            + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                            + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                            + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                            + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";*/

                    result = outputResponseXml;
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }


            /*Dc Process*/
            if ("FetchStaffDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FetchStaffDetails");
                String result = "";
                String StaffID = arg[2];
                //String StaffCategory = arg[3];

                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("tns:EBSTSTOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    //String EMPLOYEE_NUMBER = xmlParser.getValueOf("tns:EMPLOYEE_NUMBER").toString();
                    String FULL_NAME = xmlParser.getValueOf("tns:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String DEPARTMENT = xmlParser.getValueOf("tns:DEPARTMENT").toString();
                    loggern.info("DEPARTMENT:::" + DEPARTMENT);
                    String COST_CENT = xmlParser.getValueOf("tns:COST_CENT").toString();
                    loggern.info("COST_CENT:::" + COST_CENT);
                    String GRADE = xmlParser.getValueOf("tns:GRADE").toString();
                    loggern.info("GRADE:::" + GRADE);
                    String phone_num = xmlParser.getValueOf("tns:phone_num").toString();
                    loggern.info("phone_num:::" + phone_num);
                    String EMAIL_ADDRESS = xmlParser.getValueOf("tns:EMAIL_ADDRESS").toString();
                    loggern.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                    String REPORTING_MANAGER = xmlParser.getValueOf("tns:REPORTING_MANAGER").toString();
                    loggern.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                    String REPORTING_MANAGER_NO = xmlParser.getValueOf("tns:REPORTING_MANAGER_NO").toString();
                    loggern.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                    
                    if(("".equalsIgnoreCase(FULL_NAME))&&("".equalsIgnoreCase(DEPARTMENT))&&("".equalsIgnoreCase(COST_CENT))&&("".equalsIgnoreCase(GRADE))&&("".equalsIgnoreCase(phone_num))&&("".equalsIgnoreCase(EMAIL_ADDRESS))&&("".equalsIgnoreCase(REPORTING_MANAGER))&&("".equalsIgnoreCase(REPORTING_MANAGER_NO)))
                    {
                    result = "SUCCESS~" + "<root>" + noValue + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                    }
                    else
                    {
                    header="<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion>"
                    		+ "<RequestorChannelId>DFCU</RequestorChannelId><RequestorUserId>DFCUUSER</RequestorUserId>"
                    		+ "<RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo>"
                    		+ "<ReturnCode>0000</ReturnCode><ReturnDesc>Staff Details Fetch Successfully</ReturnDesc>"
                    		+ "<MessageId>LOS153984246089589</MessageId><Extra1>DFCU||SHELL.JOHN</Extra1>"
                    		+ "<Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                    result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);	
                    }

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }
            
            

            /*IRAW  Process*/   /*POST FINACLE IRAW --- updated by meeo*/
            //testing svn
           
            if("postIrawRequestToFinacle".equalsIgnoreCase(varServiceName)){
                loggern.info("welcome to Postng to Finacle on IRAW");
                String result = "";
                Properties urlFile = new Properties();
                urlFile.load(new FileInputStream("config.properties"));
                String urlString="";
                urlString = urlFile.getProperty("CIPOSTSERVICEURL");
                loggern.info("urlString ::"+urlString);

                try {
                    outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"XferTrnAdd");
                    loggern.info("Response Final==" + outputResponseXml);

                    /*header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                            + "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                            + "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                            + "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                            + "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                            + "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";*/

                    result = outputResponseXml;
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
            }
            /*IRAW  Process*/   /*FETCH  IRAW ACCOUNT DETAILS*/
            if ("fetchIrawOfficeAcctDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To fetchIrawOfficeAcctDetails");
                String result = "";      
               
                
                
                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);
                        result = outputResponseXml;
                   
                   /* 
                    header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                    result = "SUCCESS~" + "<root>" + header + "\r\n" + 
                    		"<addressType>"+addressType+"</addressType>\r\n" + 
                    		"<CustomerName>"+CustomerName+"</CustomerName>\r\n" + 
                    		"<SolID>"+SolID+"</SolID>\r\n" + 
                    		"<BranchName>"+BranchName+"</BranchName>\r\n" + 
                    		"<AcctType>"+AcctType+"</AcctType>\r\n" + 
                    		"<AccountCurrency>"+AccountCurrency+"</AccountCurrency>\r\n" + 
                    		"<ResponseHeader>"+ResponseHeader+"</ResponseHeader>\r\n" + 
                    		"<Phone></Phone>\r\n" + 
                    		"<AddressLine1/>\r\n" + 
                    		"<AddressLine2/>\r\n" + 
                    		"<CityCode/>\r\n" + 
                    		"<StateCode/>\r\n" + 
                    		"<CountryCode/>\r\n" + 
                    		"<Balance>"+Balance+"</Balance>\r\n" + 
                    		"<Schm_type>"+Schm_type+"</Schm_type>\r\n" + 
                    		"<Email/>\r\n" + 
                    		"<CustomerID>"+CustomerID+"</CustomerID>\r\n" + 
                    		"</root>";*/
                       loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" + 
                    		"<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID>\r\n" + 
                    		" </root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }

            
            /*IRAW  Process*/  /*FETCH STAFF DETAILS*/
            if ("fetchIrawstaffDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To fetchIrawstaffDetails");
                String result = "";
                String StaffID = arg[2];
                //String StaffCategory = arg[3];

                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("tns:EBSTSTOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    //String EMPLOYEE_NUMBER = xmlParser.getValueOf("tns:EMPLOYEE_NUMBER").toString();
                    String FULL_NAME = xmlParser.getValueOf("tns:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String DEPARTMENT = xmlParser.getValueOf("tns:DEPARTMENT").toString();
                    loggern.info("DEPARTMENT:::" + DEPARTMENT);
                    String COST_CENT = xmlParser.getValueOf("tns:COST_CENT").toString();
                    loggern.info("COST_CENT:::" + COST_CENT);
                    String GRADE = xmlParser.getValueOf("tns:GRADE").toString();
                    loggern.info("GRADE:::" + GRADE);
                    String phone_num = xmlParser.getValueOf("tns:phone_num").toString();
                    loggern.info("phone_num:::" + phone_num);
                    String EMAIL_ADDRESS = xmlParser.getValueOf("tns:EMAIL_ADDRESS").toString();
                    loggern.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                    String REPORTING_MANAGER = xmlParser.getValueOf("tns:REPORTING_MANAGER").toString();
                    loggern.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                    String REPORTING_MANAGER_NO = xmlParser.getValueOf("tns:REPORTING_MANAGER_NO").toString();
                    loggern.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                    header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                    result = "SUCCESS~" + "<root>" + header + "<SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager><COST_CENT>"+COST_CENT+"</COST_CENT></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }
                      
            
            
            /*IRAW  Process*/   /*FETCH  IRAW ACCOUNT DETAILS*/
            if ("fetchIrawAccountDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To fetchIrawAccountDetails");
                String result = "";      
               
                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("fetchByAcctNumberResult");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    String addressType = xmlParser.getValueOf("addressType").toString();
                    loggern.info("addressType:::" + addressType);
                    String CustomerName = xmlParser.getValueOf("CustomerName").toString();
                    loggern.info("CustomerName:::" + CustomerName);
                    String SolID = xmlParser.getValueOf("SolID").toString();
                    loggern.info("SolID:::" + SolID);
                    String BranchName = xmlParser.getValueOf("BranchName").toString();
                    loggern.info("BranchName:::" + BranchName);
                    String AcctType = xmlParser.getValueOf("AcctType").toString();
                    loggern.info("AcctType:::" + AcctType);
                    String AccountCurrency = xmlParser.getValueOf("AccountCurrency").toString();
                    loggern.info("AccountCurrency:::" + AccountCurrency);
                    String ResponseHeader = xmlParser.getValueOf("ResponseHeader").toString();
                    loggern.info("ResponseHeader:::" + ResponseHeader);
                    String Balance = xmlParser.getValueOf("Balance").toString();
                    loggern.info("Balance:::" + Balance);
                    String Schm_type = xmlParser.getValueOf("Schm_type").toString();
                    loggern.info("Schm_type:::" + Schm_type);
                    String CustomerID = xmlParser.getValueOf("CustomerID").toString();
                    loggern.info("CustomerID:::" + CustomerID);
                   
                    
                    header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                    result = "SUCCESS~" + "<root>" + header + "\r\n" + 
                    		"<addressType>"+addressType+"</addressType>\r\n" + 
                    		"<CustomerName>"+CustomerName+"</CustomerName>\r\n" + 
                    		"<SolID>"+SolID+"</SolID>\r\n" + 
                    		"<BranchName>"+BranchName+"</BranchName>\r\n" + 
                    		"<AcctType>"+AcctType+"</AcctType>\r\n" + 
                    		"<AccountCurrency>"+AccountCurrency+"</AccountCurrency>\r\n" + 
                    		"<ResponseHeader>"+ResponseHeader+"</ResponseHeader>\r\n" + 
                    		"<Phone></Phone>\r\n" + 
                    		"<AddressLine1/>\r\n" + 
                    		"<AddressLine2/>\r\n" + 
                    		"<CityCode/>\r\n" + 
                    		"<StateCode/>\r\n" + 
                    		"<CountryCode/>\r\n" + 
                    		"<Balance>"+Balance+"</Balance>\r\n" + 
                    		"<Schm_type>"+Schm_type+"</Schm_type>\r\n" + 
                    		"<Email/>\r\n" + 
                    		"<CustomerID>"+CustomerID+"</CustomerID>\r\n" + 
                    		"</root>";
                       loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" + 
                    		"<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID>\r\n" + 
                    		" </root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }
            
            /*IRAW  Process*/   /*FETCH  IRAW INCOME CHARGE ACCOUNT DETAILS*/
            if ("fetchIrawIncomechargeAccountDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To fetchIrawIncomechargeAccountDetails");
                String result = "";
              
                try {
                    outputResponseXml = CTEW.callService(inputXML,varServiceName);
                    loggern.info("Response Final==" + outputResponseXml);

                xmlParser = new XMLParser(outputResponseXml);
                varBody = xmlParser.getValueOf("fetchByAcctNumberResult");
                xmlParser = new XMLParser(varBody);
                loggern.info("Status of varBody::::" + varBody);
                String addressType = xmlParser.getValueOf("addressType").toString();
                loggern.info("addressType:::" + addressType);
                String CustomerName = xmlParser.getValueOf("CustomerName").toString();
                loggern.info("CustomerName:::" + CustomerName);
                String SolID = xmlParser.getValueOf("SolID").toString();
                loggern.info("SolID:::" + SolID);
                String BranchName = xmlParser.getValueOf("BranchName").toString();
                loggern.info("BranchName:::" + BranchName);
                String AcctType = xmlParser.getValueOf("AcctType").toString();
                loggern.info("AcctType:::" + AcctType);
                String AccountCurrency = xmlParser.getValueOf("AccountCurrency").toString();
                loggern.info("AccountCurrency:::" + AccountCurrency);
                String ResponseHeader = xmlParser.getValueOf("ResponseHeader").toString();
                loggern.info("ResponseHeader:::" + ResponseHeader);
                String Balance = xmlParser.getValueOf("Balance").toString();
                loggern.info("Balance:::" + Balance);
                String Schm_type = xmlParser.getValueOf("Schm_type").toString();
                loggern.info("Schm_type:::" + Schm_type);
                String CustomerID = xmlParser.getValueOf("CustomerID").toString();
                loggern.info("CustomerID:::" + CustomerID);
               
                
                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
            			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
            			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
            			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
            			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
            			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                result = "SUCCESS~" + "<root>" + header + "<fetchByAcctNumberResult>\r\n" + 
                		"<addressType>"+addressType+"</addressType>\r\n" + 
                		"<CustomerName>"+CustomerName+"</CustomerName>\r\n" + 
                		"<SolID>"+SolID+"</SolID>\r\n" + 
                		"<BranchName>"+BranchName+"</BranchName>\r\n" + 
                		"<AcctType>"+AcctType+"</AcctType>\r\n" + 
                		"<AccountCurrency>"+AccountCurrency+"</AccountCurrency>\r\n" + 
                		"<ResponseHeader>"+ResponseHeader+"</ResponseHeader>\r\n" + 
                		"<Phone></phone>\r\n" + 
                		"<AddressLine1/>\r\n" + 
                		"<AddressLine2/>\r\n" + 
                		"<CityCode/>\r\n" + 
                		"<StateCode/>\r\n" + 
                		"<CountryCode/>\r\n" + 
                		"<Balance>"+Balance+"</Balance>\r\n" + 
                		"<Schm_type>"+Schm_type+"</Schm_type>\r\n" + 
                		"<Email/>\r\n" + 
                		"<CustomerID>"+CustomerID+"</CustomerID>\r\n" + 
                		" </fetchByAcctNumberResult></root>";
                   loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);

            } catch (Exception e) {
            	result = "FAILED~" + "<root>" + errorHeader + " <fetchByAcctNumberResult><addressType></addressType><CustomerName></CustomerName><SolID></SolID><BranchName> </BranchName><AcctType></AcctType><AccountCurrency></AccountCurrency><ResponseHeader></ResponseHeader>\r\n" + 
                		"<CountryCode/><Balance></Balance><Schm_type></Schm_type><Email/><CustomerID></CustomerID></fetchByAcctNumberResult>\r\n" + 
                		" </root>";
                loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);
            }
                
            }
           
            
            /*IRAW  Process*/   /*POST FINACLE IRAW INCOME CHARGE ACCOUNT DETAILS*/
            if ("IrawPostFinacleIncomeAccountChargeableList".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To IrawPostFinacleIncomeAccountChargeableList");
                String result = "";
                Properties p = new Properties();
                p.load(new FileInputStream("config.properties")); 
                String urlString="";
                urlString = p.getProperty("IRAWPOSTFINACLEServiceURL");
                loggern.info("urlString ::"+urlString);
                try {
                	outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"XferTrnAdd");
                	
                   /* outputResponseXml = CTEW.callService(inputXML,varServiceName);*/
                    loggern.info("Response Final==" + outputResponseXml);

                xmlParser = new XMLParser(outputResponseXml);
                varBody = xmlParser.getValueOf("XferTrnAddRs");
                xmlParser = new XMLParser(varBody);
                loggern.info("Status of varBody::::" + varBody);
                String TrnDt = xmlParser.getValueOf("TrnDt").toString();
                loggern.info("TrnDt:::" + TrnDt);
                String TrnId = xmlParser.getValueOf("TrnId").toString();
                loggern.info("TrnId:::" + TrnId); 
                
                String varBody2 = xmlParser.getValueOf("HostTransaction");
                xmlParser = new XMLParser(varBody2);
                loggern.info("Status of varBody2::::" + varBody2);
                String status = xmlParser.getValueOf("Status").toString();
                loggern.info("Status:::" + status);
               
                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
            			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
            			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
            			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
            			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
            			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                result = "SUCCESS~" + "<root>" + header + "<Status>"+status+"</Status> <TrnDt>"+TrnDt+"</TrnDt><TrnId>"+TrnId+"</TrnId></root>";
                   loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);

            } catch (Exception e) {
            	result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);
            }
                
            }
           
            
            /*IRAW  Process*/   /*POST FINACLE */ /*IRAW DEBIT TO ONE ACCOUNT*/
            if ("IrawPostFinacleDebitToOneAccount".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To IrawPostFinacleDebitToOneAccount");
                String result = "";
                Properties p = new Properties();
                p.load(new FileInputStream("config.properties")); 
                String urlString="";
                urlString = p.getProperty("IRAWPOSTFINACLEServiceURL");
                loggern.info("urlString ::"+urlString);
                try {
                	outputResponseXml = connecttoFinacle.callService(inputXML,urlString,"XferTrnAdd");
                	
                    /* outputResponseXml = CTEW.callService(inputXML,varServiceName);*/
                    loggern.info("Response Final==" + outputResponseXml);

                xmlParser = new XMLParser(outputResponseXml);
                varBody = xmlParser.getValueOf("XferTrnAddRs");
                xmlParser = new XMLParser(varBody);
                loggern.info("Status of varBody::::" + varBody);
                String TrnDt = xmlParser.getValueOf("TrnDt").toString();
                loggern.info("TrnDt:::" + TrnDt);
                String TrnId = xmlParser.getValueOf("TrnId").toString();
                loggern.info("TrnId:::" + TrnId); 
                
                String varBody2 = xmlParser.getValueOf("HostTransaction");
                xmlParser = new XMLParser(varBody2);
                loggern.info("Status of varBody2::::" + varBody2);
                String status = xmlParser.getValueOf("Status").toString();
                loggern.info("Status:::" + status);
               
                header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
            			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
            			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
            			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
            			+ "<ReturnDesc>Details Fetched Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
            			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                result = "SUCCESS~" + "<root>" + header + "<Status>"+status+"</Status> <TrnDt>"+TrnDt+"</TrnDt><TrnId>"+TrnId+"</TrnId></root>";
                   loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);

            } catch (Exception e) {
            	result = "FAILED~" + "<root>" + errorHeader + "<Status></Status><TrnDt></TrnDt><TrnId></TrnId></root>";
                loggern.info("result:::--" + result);
                System.out.println("result:::--" + result);
                writeData(out, result);
            }
                
            }
           


            
            /*LIMIT_ENHANCEMENT  Process*/ 
            
            if ("Fetch_limit_Enh_Details_INITIATORID".equalsIgnoreCase(varServiceName)||
            		"Fetch_limit_Enh_Details_VERIFIERID".equalsIgnoreCase(varServiceName)||
            		"Fetch_limit_Enh_Details_verifier2ID".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To LIMIT_ENHANCEMENT  Process custom webservice");
                
            
                String result = "";
                String requestId = arg[2];
                loggern.info("requestId ::"+requestId);
                loggern.info("inputXML ::"+inputXML);
                try {
                    outputResponseXml = CTEW.callService(inputXML,varServiceName);
                    loggern.info("Response Final==" + outputResponseXml);
                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("tns:EBSTSTOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    
                    String FULL_NAME = xmlParser.getValueOf("tns:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String COST_CENT = xmlParser.getValueOf("tns:COST_CENT").toString();
                    loggern.info("COST_CENT:::" + COST_CENT);
                    
                    if ("Fetch_limit_Enh_Details_INITIATORID".equalsIgnoreCase(varServiceName))
                    {
                    	if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase(""))
                     	{
                     		header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                         			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                         			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                         			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                         			+ "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                         			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
 									}
                     	else{
                    	
                    	header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                    			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                    			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                    			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                    			+ "<ReturnDesc>Request Initiator Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                    			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                    	
                     	}
                    	 result = "SUCCESS~" + "<root>" + header + "<REQUESTINITIATORNAME>"+FULL_NAME+"</REQUESTINITIATORNAME><SOLINITIATOR>"+COST_CENT+"</SOLINITIATOR></root>";
                    }
                    else if("Fetch_limit_Enh_Details_VERIFIERID".equalsIgnoreCase(varServiceName))
                    {
                    	if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase(""))
                     	{
                     		header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                         			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                         			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                         			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                         			+ "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                         			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
 									}
                     	else{
                    	header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                    			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                    			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                    			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                    			+ "<ReturnDesc>Request Verifier Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                    			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                    	
                    }
                    	result = "SUCCESS~" + "<root>" + header + "<REQUESTVERIFIERNAME>"+FULL_NAME+"</REQUESTVERIFIERNAME><sol_id_verifier>"+COST_CENT+"</sol_id_verifier></root>";
                    }
                    else if("Fetch_limit_Enh_Details_verifier2ID".equalsIgnoreCase(varServiceName))
                    {
                    	if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase(""))
                     	{
                     		header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                         			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                         			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                         			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                         			+ "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                         			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
 									}
                     	else{
                    	header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                    			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                    			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                    			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                    			+ "<ReturnDesc>verifier2Name and Sol Id Verifier Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                    			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                    	
                    }
                    	 result = "SUCCESS~" + "<root>" + header + "<verifier2Name>"+FULL_NAME+"</verifier2Name><verifier2Sol_ID>"+COST_CENT+"</verifier2Sol_ID></root>";
                    }
                    
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                   

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><REQUESTINITIATORNAME></REQUESTINITIATORNAME><sol_id_initiator></sol_id_initiator></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }
            
            /*CI  Process*/     /*FETCH STAFF DETAILS*/
            if ("FetchCIStaffDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FetchCIStaffDetails");
                String result = "";
                String StaffID = arg[2];
               // String StaffCategory = arg[3];
                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("tns:EBSTSTOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    //String EMPLOYEE_NUMBER = xmlParser.getValueOf("tns:EMPLOYEE_NUMBER").toString();
                    String FULL_NAME = xmlParser.getValueOf("tns:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String DEPARTMENT = xmlParser.getValueOf("tns:DEPARTMENT").toString();
                    loggern.info("DEPARTMENT:::" + DEPARTMENT);
                    String COST_CENT = xmlParser.getValueOf("tns:COST_CENT").toString();
                    loggern.info("COST_CENT:::" + COST_CENT);
                    String GRADE = xmlParser.getValueOf("tns:GRADE").toString();
                    loggern.info("GRADE:::" + GRADE);
                    String phone_num = xmlParser.getValueOf("tns:phone_num").toString();
                    loggern.info("phone_num:::" + phone_num);
                    String EMAIL_ADDRESS = xmlParser.getValueOf("tns:EMAIL_ADDRESS").toString();
                    loggern.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                    String REPORTING_MANAGER = xmlParser.getValueOf("tns:REPORTING_MANAGER").toString();
                    loggern.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                    String REPORTING_MANAGER_NO = xmlParser.getValueOf("tns:REPORTING_MANAGER_NO").toString();
                    loggern.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                    
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
                    result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager><COST_CENT>"+COST_CENT+"</COST_CENT></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }

           
            /*MEMO  Process*/     /*FETCH STAFF DETAILS*/
            if ("FetchMemoStaffDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To FetchMemoStaffDetails");
                String result = "";
                String StaffID = arg[2];
               // String StaffCategory = arg[3];
                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("tns:EBSTSTOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    //String EMPLOYEE_NUMBER = xmlParser.getValueOf("tns:EMPLOYEE_NUMBER").toString();
                    String FULL_NAME = xmlParser.getValueOf("tns:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String DEPARTMENT = xmlParser.getValueOf("tns:DEPARTMENT").toString();
                    loggern.info("DEPARTMENT:::" + DEPARTMENT);
                    String COST_CENT = xmlParser.getValueOf("tns:COST_CENT").toString();
                    loggern.info("COST_CENT:::" + COST_CENT);
                    String GRADE = xmlParser.getValueOf("tns:GRADE").toString();
                    loggern.info("GRADE:::" + GRADE);
                    String phone_num = xmlParser.getValueOf("tns:phone_num").toString();
                    loggern.info("phone_num:::" + phone_num);
                    String EMAIL_ADDRESS = xmlParser.getValueOf("tns:EMAIL_ADDRESS").toString();
                    loggern.info("EMAIL_ADDRESS:::" + EMAIL_ADDRESS);
                    String REPORTING_MANAGER = xmlParser.getValueOf("tns:REPORTING_MANAGER").toString();
                    loggern.info("REPORTING_MANAGER:::" + REPORTING_MANAGER);
                    String REPORTING_MANAGER_NO = xmlParser.getValueOf("tns:REPORTING_MANAGER_NO").toString();
                    loggern.info("REPORTING_MANAGER_NO:::" + REPORTING_MANAGER_NO);
                    
                   
                    if (FULL_NAME.equalsIgnoreCase("") && COST_CENT.equalsIgnoreCase(""))
                 	{
                 		header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                     			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                     			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                     			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                     			+ "<ReturnDesc>Invalid Staff ID/Staff does not Exist</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                     			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
									}
                 	else{
                	
                	header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                			+ "<ReturnDesc>Staff Details Fetched Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";
                	
                 	}
                    result = "SUCCESS~" + "<root>" + header + "<StaffDetails><SanctionDate></SanctionDate><Department>"+DEPARTMENT+"</Department><Email>"+EMAIL_ADDRESS+"</Email><Address></Address><StaffID>"+StaffID+"</StaffID><PhoneNo>"+phone_num+"</PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade>"+GRADE+"</Grade><CaseOutCome></CaseOutCome><StaffName>"+FULL_NAME+"</StaffName><ReportingManager>"+REPORTING_MANAGER+"</ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<StaffDetails><SanctionDate></SanctionDate><Department></Department><Email></Email><Address></Address><StaffID></StaffID><PhoneNo></PhoneNo><Branch></Branch><StaffCategory></StaffCategory><Grade></Grade><CaseOutCome></CaseOutCome><StaffName></StaffName><ReportingManager></ReportingManager></StaffDetails></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }
            
            if ("fetchSEStaffDetails".equalsIgnoreCase(varServiceName)) {
                loggern.info("Welcome To service executive appraisal");
                String result = "";
                String StaffID = arg[2];


                try {
                        outputResponseXml = CTEW.callService(inputXML,varServiceName);
                        loggern.info("Response Final==" + outputResponseXml);

                    xmlParser = new XMLParser(outputResponseXml);
                    varBody = xmlParser.getValueOf("ns0:dbReferenceOutput");
                    xmlParser = new XMLParser(varBody);
                    loggern.info("Status of varBody::::" + varBody);
                    String FULL_NAME = xmlParser.getValueOf("ns0:FULL_NAME").toString();
                    loggern.info("FULL_NAME:::" + FULL_NAME);
                    String DEPARTMENT = xmlParser.getValueOf("ns0:DEPT").toString();
                    loggern.info("DEPARTMENT:::" + DEPARTMENT);
                    String SOL_ID = xmlParser.getValueOf("ns0:COST_CENTER").toString();
                    loggern.info("SOL_ID:::" + SOL_ID);
                    String EMPLOYEE_NUMBER = xmlParser.getValueOf("ns0:EMPLOYEE_NUMBER").toString();
                    loggern.info("EMPLOYEE_NUMBER:::" + EMPLOYEE_NUMBER);
                    String JOB_NAME = xmlParser.getValueOf("ns0:JOB_NAME").toString();
                    loggern.info("JOB_NAME:::" + JOB_NAME);
                    String LOCATIONS = xmlParser.getValueOf("ns0:LOCATIONS").toString();
                    loggern.info("LOCATIONS:::" + LOCATIONS);
                    String REPORTING_MANAGER_NAME = xmlParser.getValueOf("ns0:SUP_FULL_NAME").toString();
                    loggern.info("REPORTING_MANAGER_NAME:::" + REPORTING_MANAGER_NAME);
                    String REPORTING_MANAGER_USERID = xmlParser.getValueOf("ns0:SUP_EMP_NUMBER").toString();
                    loggern.info("REPORTING_MANAGER_USERID:::" + REPORTING_MANAGER_USERID);
                    String REPORTING_MANAGER_JOBROLE = xmlParser.getValueOf("ns0:SUP_JOB").toString();
                    loggern.info("REPORTING_MANAGER_JOBROLE:::" + REPORTING_MANAGER_JOBROLE);
                    header = "<EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat>"
                			+ "<MsgVersion>0001</MsgVersion><RequestorChannelId>DFCU</RequestorChannelId>"
                			+ "<RequestorUserId>DFCUUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage>"
                			+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode>"
                			+ "<ReturnDesc>Details Fetch Successfully</ReturnDesc><MessageId>LOS153984246089589</MessageId>"
                			+ "<Extra1>DFCU||SHELL.JOHN</Extra1><Extra2>2018-10-18T10:01:02.490+04:00</Extra2></EE_EAI_HEADER>";

                    result = "SUCCESS~" + "<root>" + header + "<Department>"+DEPARTMENT+"</Department><Solid>"+SOL_ID+"</Solid><StaffID>"+EMPLOYEE_NUMBER+"</StaffID><JobName>"+JOB_NAME+"</JobName><Location>"+LOCATIONS+"</Location><StaffCategory></StaffCategory><ReportingMgrName>"+REPORTING_MANAGER_NAME+"</ReportingMgrName><StaffName>"+FULL_NAME+"</StaffName><ReportingMgrUserID>"+REPORTING_MANAGER_USERID+"</ReportingMgrUserID><ReportingMgrJobRole>"+REPORTING_MANAGER_JOBROLE+"</ReportingMgrJobRole></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);

                } catch (Exception e) {
                    result = "FAILED~" + "<root>" + errorHeader + "<Department></Department><Solid></Solid><Address></Address><StaffID></StaffID><JobName></JobName><Location></Location><StaffCategory></StaffCategory><ReportingMgrName></ReportingMgrName><StaffName></StaffName><ReportingMgrUserID></ReportingMgrUserID><ReportingMgrJobRole></ReportingMgrJobRole></root>";
                    loggern.info("result:::--" + result);
                    System.out.println("result:::--" + result);
                    writeData(out, result);
                }
                
            }

            
        } catch (IOException ioe) {
            ioe.printStackTrace();
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(new DataOutputStream(server.getOutputStream()));
            } catch (Exception e) {
                System.out.println("Data Output Stream initialization error");
                N.err = "Data Output Stream initialization error" + e.toString();
                e.printStackTrace();
            }
            System.out.println("Catch 19 IOException on socket listen: " + ioe);
            N.err = "IOException on socket listen: " + ioe;
            input = N.err;
            try {
                writeData(out, input);
            } catch (Exception e) {
                N.err = "Write Data Exception " + e.toString();
            }
            loggern.info("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(doComms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (server != null) {
                    System.out.println("====Closing local Cient Socket=======" + server.getRemoteSocketAddress());
                    loggern.info("====Closing local Cient Socket=======" + server.getRemoteSocketAddress());
                    server.close();
                    server = null;
                    System.out.println("====Successfuly Closed=======");
                    loggern.info("====Successfuly Closed=======");

                }
            } catch (Exception e) {
                System.out.println("Exception " + e.toString());
                loggern.info("Exception " + e.toString());
                N.err = e.toString();
                //input="<Status>FAILURE</Status>"+ N.err;
                input = N.err;
            }
        }
    }

}

//----------------------------------------------------------------------------------------------------------------