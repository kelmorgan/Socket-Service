package com.newgen.makeCall;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;


public class ConnectToFinacle {

//added 4Feb2017

	private static final Logger logger = Logger.getLogger("consoleLogger");

	public String callServiceFI(String requestLOS,String urlString, String appCode) throws Exception
		{
			logger.info("Request XML : " + requestLOS);
			return sendRequestFI(urlString, requestLOS,appCode);
		}
        
	 public String callService(String requestLOS,String urlString, String appCode) throws Exception 
		{
			logger.info("Request XML : " + requestLOS);
			return sendRequest(urlString, requestLOS,appCode);
		}

	private String sendRequestFI(String urlString,String ipXML,String appCode) throws FileNotFoundException, IOException
		{       
			StringBuilder responseXML= new StringBuilder();

			URLConnection connection;
			BufferedReader in =null;
			OutputStreamWriter out=null;
	        String getResponse ="";

			try
	        {
	                       
	                       logger.info("urlString : " + urlString);
	                       logger.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?appCode=" + URLEncoder.encode(appCode, "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8");
				System.out.println("Final requestXml "+ getResponse);
				URL url = new URL(getResponse);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       logger.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       responseXML.append(decodedString);
	                       logger.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("CONNECTTIMEOUT", "Could not Connect to Finacle Server"));
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("READTIMEOUT", "Could not read the response"));
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("EXCEPTIONINCONNECTION", e.getMessage()));
				e.printStackTrace();
			}
			finally
			{
				try
				{
					in.close();
					out.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	    	logger.info("This is the finally response XML: "+ responseXML);
			return responseXML.toString();
		}
	 private String sendRequest(String urlString,String ipXML,String appCode) throws FileNotFoundException, IOException 
		{       
			StringBuilder responseXML= new StringBuilder();

			URLConnection connection=null;
			BufferedReader in =null;
			OutputStreamWriter out=null;
	        String getResponse ="";

			try
	        {
	                       
	                       logger.info("urlString : " + urlString);
	                       logger.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?AppCode=" + URLEncoder.encode(appCode, "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8");
				System.out.println("Final request "+ getResponse);
				URL url = new URL(getResponse);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       logger.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       responseXML.append(decodedString);
	                       logger.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("CONNECTTIMEOUT", "Could not Connect to Finacle Server"));
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("READTIMEOUT", "Could not read the response"));
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML = new StringBuilder(setErrorOPMessage("EXCEPTIONINCONNECTION", e.getMessage()));
				e.printStackTrace();
			}
			finally
			{
				try
				{
					/* con.disconnect(); */
					/* bout.close(); */
					/* isr.close(); */
					in.close();
					out.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	    	logger.info("This is the finally response XML: "+ responseXML);
			return responseXML.toString();
		}

	/*******************************************************************************************
	 * Method "getProperty" to get the value of parameters from properties file "DMS.properties"
	 ******************************************************************************************/
	public static String setErrorOPMessage(String errorMessage,String errorMsgDesc)
	{
		return "<WebServiceErrorMessage>"+
				"<ns0:Status>ERRORINHANDLING</ns0:Status>"+
				"<ns0:Message>"+errorMessage+"</ns0:Message>"+
				"<ns0:Desc>"+errorMsgDesc+"</ns0:Desc>"+
				"</WebServiceErrorMessage>";
	}
}
