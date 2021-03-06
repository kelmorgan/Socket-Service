package com.newgen.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class ConnectToFinacle {
    
private static final boolean debug = true; //added 4Feb2017
	
	private static Logger loggern = Logger.getLogger("consoleLogger");
	Map<String, String> paramMap = new HashMap<String, String>(); 
	final Logger integratioAOLogger = Logger.getLogger("mLogger");
	
	 public String callServiceFI(String requestLOS,String urlString, String appCode) throws Exception 
		{
		 loggern.info("inside call service method");
		StringBuffer strFilePath = null;               
		strFilePath = new StringBuffer(1000);
		String userDir = System.getProperty("user.dir");

		loggern.info("user dir : "+userDir);

		Map<String, String> intPropMap=new HashMap<String, String>();
		String requestXML=requestLOS;
		loggern.info("Request XML : " + requestXML);

  String GetResponseXML=sendRequestFI(urlString,requestXML,appCode); 
		return GetResponseXML;
		}
        
	 public String callService(String requestLOS,String urlString, String appCode) throws Exception 
		{
		 loggern.info("inside call service method");
		StringBuffer strFilePath = null;               
		strFilePath = new StringBuffer(1000);
		String userDir = System.getProperty("user.dir");

		loggern.info("user dir : "+userDir);

		Map<String, String> intPropMap=new HashMap<String, String>();
		String requestXML=requestLOS;
		loggern.info("Request XML : " + requestXML);

     String GetResponseXML=sendRequest(urlString,requestXML,appCode); 
		return GetResponseXML;
		}
	 public String callPlaceLienService(String requestLOS,String urlString) throws Exception 
		{
		 loggern.info("inside call place lien service method");
		StringBuffer strFilePath = null;               
		strFilePath = new StringBuffer(1000);
		String userDir = System.getProperty("user.dir");

		loggern.info("user dir : "+userDir);

		Map<String, String> intPropMap=new HashMap<String, String>();
		String requestXML=requestLOS;
		loggern.info("Request place lien XML : " + requestXML);

  String GetResponseXML=sendPlaceLienRequest(urlString,requestXML); 
		return GetResponseXML;
		}
	 
	 public String callRemoveLienService(String requestLOS,String urlString) throws Exception 
		{
		 loggern.info("inside call remove lien service method");
		StringBuffer strFilePath = null;               
		strFilePath = new StringBuffer(1000);
		String userDir = System.getProperty("user.dir");

		loggern.info("user dir : "+userDir);

		Map<String, String> intPropMap=new HashMap<String, String>();
		String requestXML=requestLOS;
		loggern.info("Request remove lien XML : " + requestXML);

String GetResponseXML=sendRemoveLienRequest(urlString,requestXML); 
		return GetResponseXML;
		}

	 private String sendRequestFI(String urlString,String ipXML,String appCode) throws FileNotFoundException, IOException 
		{       
			String responseXML="";            
			String outputString="";

			URLConnection connection=null;
			HttpsURLConnection con=null;
			ByteArrayOutputStream bout=null;
			InputStreamReader isr =null;
			BufferedReader in =null;
			OutputStream outO =null;
	                OutputStreamWriter out=null;
	        String getResponse ="";
	        int responseCode =0;
	        
	        try
	        {
	                       
	                       loggern.info("urlString : " + urlString);
	                       loggern.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?appCode=" + URLEncoder.encode(appCode, "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8"); 
				URL url = new URL(getResponse);
				//URL url = new URL(args[0]);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       loggern.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       loggern.info(decodedString);
	                       loggern.info(decodedString.toString());
	                       responseXML+=decodedString.toString();
	                       loggern.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML= setErrorOPMessage("CONNECTTIMEOUT","Could not Connect to Finacle Server");
				outputString="";           
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML= setErrorOPMessage("READTIMEOUT","Could not read the response");
				outputString="";           
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML= setErrorOPMessage("EXCEPTIONINCONNECTION",e.getMessage());
				outputString="";           
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
	    	loggern.info("This is the finally response XML: "+ responseXML);
			return responseXML;
		}
	 private String sendRequest(String urlString,String ipXML,String appCode) throws FileNotFoundException, IOException 
		{       
			String responseXML="";            
			String outputString="";

			URLConnection connection=null;
			HttpsURLConnection con=null;
			ByteArrayOutputStream bout=null;
			InputStreamReader isr =null;
			BufferedReader in =null;
			OutputStream outO =null;
	                OutputStreamWriter out=null;
	        String getResponse ="";
	        int responseCode =0;
	        
	        try
	        {
	                       
	                       loggern.info("urlString : " + urlString);
	                       loggern.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?AppCode=" + URLEncoder.encode(appCode, "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8"); 
				URL url = new URL(getResponse);
				//URL url = new URL(args[0]);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       loggern.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       loggern.info(decodedString);
	                       loggern.info(decodedString.toString());
	                       responseXML+=decodedString.toString();
	                       loggern.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML= setErrorOPMessage("CONNECTTIMEOUT","Could not Connect to Finacle Server");
				outputString="";           
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML= setErrorOPMessage("READTIMEOUT","Could not read the response");
				outputString="";           
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML= setErrorOPMessage("EXCEPTIONINCONNECTION",e.getMessage());
				outputString="";           
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
	    	loggern.info("This is the finally response XML: "+ responseXML);
			return responseXML;
		}
	 private String sendPlaceLienRequest(String urlString,String ipXML) throws FileNotFoundException, IOException 
		{       
			String responseXML="";            
			String outputString="";

			URLConnection connection=null;
			HttpsURLConnection con=null;
			ByteArrayOutputStream bout=null;
			InputStreamReader isr =null;
			BufferedReader in =null;
			OutputStream outO =null;
	                OutputStreamWriter out=null;
	        String getResponse ="";
	        int responseCode =0;
	        
	        try
	        {
	                       
	                       loggern.info("urlString : " + urlString);
	                       loggern.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?AppCode=" + URLEncoder.encode("LLAcctLienAdd", "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8"); 
				URL url = new URL(getResponse);
				//URL url = new URL(args[0]);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       loggern.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       loggern.info(decodedString);
	                       loggern.info(decodedString.toString());
	                       responseXML+=decodedString.toString();
	                       loggern.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML= setErrorOPMessage("CONNECTTIMEOUT","Could not Connect to Finacle Server");
				outputString="";           
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML= setErrorOPMessage("READTIMEOUT","Could not read the response");
				outputString="";           
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML= setErrorOPMessage("EXCEPTIONINCONNECTION",e.getMessage());
				outputString="";           
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
	    	loggern.info("This is the finally response XML: "+ responseXML);
			return responseXML;
		}
	 private String sendRemoveLienRequest(String urlString,String ipXML) throws FileNotFoundException, IOException 
		{       
			String responseXML="";            
			String outputString="";

			URLConnection connection=null;
			HttpsURLConnection con=null;
			ByteArrayOutputStream bout=null;
			InputStreamReader isr =null;
			BufferedReader in =null;
			OutputStream outO =null;
	                OutputStreamWriter out=null;
	        String getResponse ="";
	        int responseCode =0;
	        
	        try
	        {
	                       
	                       loggern.info("urlString : " + urlString);
	                       loggern.info("SOAPAction : " + ipXML);
				       
				getResponse = urlString+"?AppCode=" + URLEncoder.encode("LRmvLienMod", "UTF-8") + "&FIData="+ URLEncoder.encode(ipXML, "UTF-8"); 
				URL url = new URL(getResponse);
				//URL url = new URL(args[0]);
	                        connection = url.openConnection();
	                        connection.setDoOutput(true);

	                        out = new OutputStreamWriter(connection.getOutputStream());
	                       loggern.info("string=" + getResponse);
	                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                        String decodedString;
	                        while ((decodedString = in.readLine()) != null) {
	                       loggern.info(decodedString);
	                       loggern.info(decodedString.toString());
	                       responseXML+=decodedString.toString();
	                       loggern.info("responseXML : " + responseXML);
	                        }
	                }
			catch(ConnectException e)
			{
				responseXML= setErrorOPMessage("CONNECTTIMEOUT","Could not Connect to Finacle Server");
				outputString="";           
				e.printStackTrace();
			}
			catch(SocketTimeoutException e)
			{
				responseXML= setErrorOPMessage("READTIMEOUT","Could not read the response");
				outputString="";           
				e.printStackTrace();
			}catch(Exception e)
			{
				responseXML= setErrorOPMessage("EXCEPTIONINCONNECTION",e.getMessage());
				outputString="";           
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
	    	loggern.info("This is the finally response XML: "+ responseXML);
			return responseXML;
		}
        /*******************************************************************************************
	 * Method "getProperty" to get the value of parameters from properties file "DMS.properties"
	 ******************************************************************************************/
	public static String setErrorOPMessage(String errorMessage,String errorMsgDesc)
	{
		String errorOutput = "<WebServiceErrorMessage>"+
				"<ns0:Status>ERRORINHANDLING</ns0:Status>"+
				"<ns0:Message>"+errorMessage+"</ns0:Message>"+
				"<ns0:Desc>"+errorMsgDesc+"</ns0:Desc>"+
				"</WebServiceErrorMessage>";

		return errorOutput;
	}

	private static Map<String, String> readIntgProperty(String sectionName,String intgPrpFilePath) throws IOException
	{
		Map<String, String> operationMap=new HashMap<String, String>();

		Properties p = new Properties();

		p.load(new FileInputStream(intgPrpFilePath));
		operationMap.put("CABINETNAME", p.getProperty("CABINETNAME"));
		operationMap.put("JTSIP", p.getProperty("JTSIP"));
		operationMap.put("JTSPORT", p.getProperty("JTSPORT"));
		operationMap.put("ENDPOINTURL", p.getProperty("ENDPOINTURL"));
		operationMap.put("DUMMYRESPONSE", p.getProperty("DUMMYRESPONSE"));
		operationMap.put("TRUSTSTOREPASSWORD", p.getProperty("TRUSTSTOREPASSWORD"));
		operationMap.put("KEYSTOREPASSWORD", p.getProperty("KEYSTOREPASSWORD"));//Added 4Feb2017
		operationMap.put("ALIAS", p.getProperty("ALIAS"));//Added 4Feb2017
		operationMap.put("CONNECTTIMEOUT", p.getProperty("CONNECTTIMEOUT"));
		operationMap.put("READTIMEOUT", p.getProperty("READTIMEOUT"));
		operationMap.put("OPNAME", p.getProperty("OPNAME"));


		String intgPropetiesArray [] ={"ENDPOINTURL","WSIP","ACTIONNAME","UID","UPWD","OPNAME","DUMMYRESPONSE","CONNECTTIMEOUT","READTIMEOUT"};

		RandomAccessFile raf;
		String INI=intgPrpFilePath;

		try
		{
			raf= new RandomAccessFile(INI,"rw");
			for(String line = raf.readLine(); line != null; line = raf.readLine())
			{
				if(line==null||line.equalsIgnoreCase(""))
					continue;
				line = line.trim();
				if(line.charAt(0) != '[' || line.charAt(line.length() - 1) != ']' || !line.substring(1, line.length() - 1).equalsIgnoreCase(sectionName))
					continue;
				for(line = raf.readLine(); line != null; line = raf.readLine())
				{
					if(line==null||line.equalsIgnoreCase(""))
						continue;
					line = line.trim();
					if(line.charAt(0) == '[')
					{
						break;
					}
					int i = line.indexOf('=');
					String temp="";
					temp = line.substring(0, i);
					for(int j=0;j<intgPropetiesArray.length;j++)
					{
						if(!temp.equalsIgnoreCase(intgPropetiesArray[j]))
						{
							continue;
						}
						else
						{
							operationMap.put(intgPropetiesArray[j],line.substring(i+1));
						}
					}
				}
				break;
			}
			raf.close();

			for(int i=0;i<intgPropetiesArray.length;i++)
			{
				if(operationMap.get(intgPropetiesArray[i])==null)
				{
					operationMap.put(intgPropetiesArray[i],"");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return operationMap;
	}

}
