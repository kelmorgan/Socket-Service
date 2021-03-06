package com.newgen.util;

import com.newgen.webserviceclient.NGWebServiceClient;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ConnectToexternalWebService {
    
    private static Logger loggern = Logger.getLogger("consoleLogger");
    final Logger integratioAOLogger = Logger.getLogger("mLogger");
    String EndPointurl = "";
    
    public String callService(String requestXML,String varServiceName ) throws Exception 
	{
            loggern.info("inside call service method");
            String responseXML="";
            responseXML=sendRequest(requestXML,varServiceName); 
            return responseXML;
	}
    
    private String sendRequest(String SOAP_inxml,String ServiceName) 
	{
            String responseXML="";
            try
		{ 
                Properties p = new Properties();
                p.load(new FileInputStream("config.properties"));   
                if(ServiceName.equalsIgnoreCase("FetchStaffDetails"))
                {
                	 EndPointurl = p.getProperty("DFCUWebSerciceURL");
                }
                if(ServiceName.equalsIgnoreCase("fetchIrawXferTrnAdd"))
                {
                	 EndPointurl = p.getProperty("IRAWpostfinacle");
                }
                if(ServiceName.equalsIgnoreCase("fetchIrawstaffDetails"))
                {
                	 EndPointurl = p.getProperty("DFCUWebSerciceURL");
                }
                if ("Fetch_limit_Enh_Details_INITIATORID".equalsIgnoreCase(ServiceName)||
                		"Fetch_limit_Enh_Details_VERIFIERID".equalsIgnoreCase(ServiceName)||
                		"Fetch_limit_Enh_Details_verifier2ID".equalsIgnoreCase(ServiceName)) {
                	 	EndPointurl = p.getProperty("DFCUWebSerciceURL");
                }
                if(ServiceName.equalsIgnoreCase("FetchCIStaffDetails"))
                {
                	 EndPointurl = p.getProperty("DFCUWebSerciceURL");
                }
                if(ServiceName.equalsIgnoreCase("CI"))
                {
                	 EndPointurl = p.getProperty("CIpostfinacle");
                }
                if(ServiceName.equalsIgnoreCase("FetchMemoStaffDetails"))
                {
                	 EndPointurl = p.getProperty("DFCUWebSerciceURL");
                }
                if(ServiceName.equalsIgnoreCase("fetchIrawAccountDetails"))
                {
                	 EndPointurl = p.getProperty("IRAWAccountServiceURL");
                }
                if(ServiceName.equalsIgnoreCase("fetchIrawIncomechargeAccountDetails"))
                {
                	 EndPointurl = p.getProperty("IRAWAccountServiceURL");
                }
                if(ServiceName.equalsIgnoreCase("IrawPostFinacleDebitToOneAccount"))
                {
                	 EndPointurl = p.getProperty("IRAWPOSTFINACLEServiceURL");
                }
                if(ServiceName.equalsIgnoreCase("BVNREQUEST"))
                {
                    EndPointurl = p.getProperty("ASBVNENQUIRY");
                }
                if (ServiceName.equalsIgnoreCase("CIGETUSERLIMIT"))
                {
                    EndPointurl = p.getProperty("CIGETUSERLIMIT");
                }
                if (ServiceName.equalsIgnoreCase("fetchSEStaffDetails"))
                {
                    EndPointurl = p.getProperty("SEFETCHSTAFF");
                }
                if (ServiceName.equalsIgnoreCase("AVRSearch"))
                {
                    EndPointurl = p.getProperty("AVRSearch");
                }
                
              
                
                loggern.info("EndPointurl" + EndPointurl);
                //EndPointurl = "http://testsmsapi.dfcugroup.com/FinwebserviceApp/Webservice.asmx";
                String SOAPResponse_xml = NGWebServiceClient.ExecuteWs(SOAP_inxml, EndPointurl);
                loggern.info("SOAPResponse_xml" + SOAPResponse_xml);
                responseXML=SOAPResponse_xml;
                return responseXML;    
                }
            catch(Exception e)
		{
			responseXML= setErrorOPMessage("ERROR","Could get the response");          
			e.printStackTrace();
                        return responseXML; 
		}
            
        }
    
    	public String setErrorOPMessage(String errorMessage,String errorMsgDesc)
	{
                String errorOutput ="";
                try
                {
		errorOutput = "<WebServiceErrorMessage>"+
				"<ns0:Status>ERRORINHANDLING</ns0:Status>"+
				"<ns0:Message>"+errorMessage+"</ns0:Message>"+
				"<ns0:Desc>"+errorMsgDesc+"</ns0:Desc>"+
				"</WebServiceErrorMessage>";
                return errorOutput;
                }
                catch(Exception e)
		{
		errorOutput= setErrorOPMessage("ERROR","Could get the response");          
		e.printStackTrace();
                return errorOutput;
                }
	}
    
}
