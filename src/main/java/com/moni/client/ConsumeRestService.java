package com.moni.client;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import com.moni.provider.model.Greeting;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;


public class ConsumeRestService {
	
	private static final Logger logger = Logger.getLogger(ConsumeRestService.class.getName());
	
	private static final String LOGIN = "user";
	private static final String PASSWORD = "password";
	private static final String END_POINT = "http://localhost:8080/platform/api/greeting";
	
	public static void main(String[] args) 
	{
		// Un-authorize call
		//callWithoutSecurity();
		
		// authorize call
		callWithSecurity();
	}
	
	// fire a secured call
	static void callWithSecurity() 
	{
		RestTemplate restTemplate = new RestTemplate();
		
		try {	
			
			ResponseEntity<Greeting> entity = 
					restTemplate.exchange(END_POINT
							, HttpMethod.GET
							, getHttpRequest(LOGIN, PASSWORD)
							, Greeting.class);
				        
	        Greeting greeting = entity.getBody();	       
	        	        
	        System.out.println("The request response status:   " + entity.getStatusCode());
	        System.out.println("Greeting ID:    " + greeting.getId());
	        System.out.println("Greeting Content:   " + greeting.getContent());  
		} catch (HttpClientErrorException ex) {
		      if (HttpStatus.UNAUTHORIZED == ex.getStatusCode()) {
		    	  System.out.println("Unauthorized call to " + END_POINT 
		    			  + "\nWrong login and/or password (\"" + LOGIN + "\" / \"" + PASSWORD + "\")");
		      }
		}
	}
	
	// fire an unsecured call
	static void callWithoutSecurity() 
	{
		RestTemplate restTemplate = new RestTemplate();
		Greeting greeting = restTemplate.getForObject(
	        		END_POINT	        	
	        		, Greeting.class);
	        
	    System.out.println("Greeting ID:    " + greeting.getId());
	    System.out.println("Greeting Content:   " + greeting.getContent());   
	}
	
	// builds http header with authorization
	// authorization in the form of: "login:password"
	static HttpHeaders getHeaders(String auth) 
	{
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

	    byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
	    String authHeader = "Basic " + new String( encodedAuth );
	    
	    logger.info(">>> Header auth: " + authHeader);
	    
	    headers.add("Authorization", authHeader);    

	    return headers;
	}
	
	static HttpEntity<String> getHttpRequest(String login, String password) 
	{
		// construct headers with login and password
		HttpHeaders headers = getHeaders(login+":"+password);
		
		// request body to send the expected
		// requested parameter "name"
		String requestBody_JSON = "{name: \"Haytham\"}";
		
		// HTTP request
		HttpEntity<String> requestEntity = new HttpEntity<String> (
				requestBody_JSON, headers);
						
		return requestEntity;
	}

}
