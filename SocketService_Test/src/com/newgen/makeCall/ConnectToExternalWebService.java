package com.newgen.makeCall;

import com.newgen.webserviceclient.NGWebServiceClient;
import org.apache.log4j.Logger;

public class ConnectToExternalWebService {
    
    private static final Logger logger = Logger.getLogger("consoleLogger");

    public String callService(String request,String endpoint, String soapAction ) {
        return sendRequest(request,endpoint,soapAction);
    }
    private String sendRequest(String request,String endpoint , String soapAction)
    {
        try
        {
            return (hasSoapAction(soapAction)) ? NGWebServiceClient.ExecuteWs(request,endpoint,soapAction) : NGWebServiceClient.ExecuteWs(request,endpoint);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return setErrorOPMessage("ERROR","Could get the response");
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
	private boolean hasSoapAction(String action){
        return !(action == null || action.equalsIgnoreCase(""));
    }
    
}
