package com.moni.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AdminOauth2RestClient {
	
	private static final Logger logger = Logger.getLogger(AdminOauth2RestClient.class.getName());
	
	static String clientId = "my-client-with-registered-redirect";
	
	// Get a client token
	private static final String END_POINT = "http://localhost:8080/platform/oauth/clients/{client}/tokens";
	
	private static final String ACCESS_TOKEN_URI = "http://localhost:8080/platform/oauth/token";
	
	public static void main(String[] args) throws Exception{
		// call oauth2 secured API resource
		callAPI() ;
	}		

	// fire a secured call
		static void callAPI() 
		{
						
			try {	
				
				ResponseEntity<String> entity = 
						trustedClientRestTemplate()
								.getForEntity(getTokenServiceEndPoint(clientId), String.class);
								//.exchange(END_POINT
								//, HttpMethod.GET
								//, getHttpRequest()
								//, String.class);        			
					  
				// inspecting the headers
				HttpHeaders headers = entity.getHeaders();				
				Map<String, String> headersMap = headers.toSingleValueMap();
				Set<String> keys = headersMap.keySet();
				for (String headerName: keys) {
					System.out.println(headerName + " = " + headersMap.get(headerName));
				}
				
				
		        String photos = entity.getBody();	       
		        	        
		        System.out.println("The request response status:   " + entity.getStatusCode());
		        System.out.println("photos:    " + photos);
		        
			} catch (HttpClientErrorException ex) {
			      if (HttpStatus.UNAUTHORIZED == ex.getStatusCode()) {
			    	  System.out.println("Unauthorized call to " + END_POINT); 
			    			 
			      }
			}
		}
		
		public static String getTokenServiceEndPoint(String _clientId) {
			String url =  new String(END_POINT).replace("{client}", _clientId);
			logger.info("Calling url : " + url);
			return url;
		}
		
		// builds http header with authorization
		// authorization in the form of: "login:password"
		static HttpHeaders getHeaders() 
		{
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); 
		    return headers;
		}
		
		static HttpEntity<String> getHttpRequest() 
		{
			// construct headers with login and password
			HttpHeaders headers = getHeaders();
			
			// request body to send the expected
			// requested parameter "name"
			String requestBody_JSON = "{format: \"json\"}";
			
			// HTTP request
			HttpEntity<String> requestEntity = new HttpEntity<String> (
					requestBody_JSON, headers);
							
			return requestEntity;
		}
	
	public static OAuth2RestTemplate trustedClientRestTemplate() 
	{
		return new OAuth2RestTemplate(trusted(), new DefaultOAuth2ClientContext());
	}

	public static ClientCredentialsResourceDetails trusted() 
	{
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setId("dmApp/trusted");
        details.setClientId(clientId);
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        details.setScope(Arrays.asList("read", "trust"));
        details.setGrantType("client_credentials");
        //details.setClientSecret("somesecret");
        return details;
	}	

}
