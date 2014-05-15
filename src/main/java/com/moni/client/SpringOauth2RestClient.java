package com.moni.client;

import java.net.URI;
import java.util.Arrays;

import com.moni.provider.model.Greeting;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

public class SpringOauth2RestClient {

	private static final String END_POINT = "http://localhost:8080/platform/api/greeting";
	private static final String ACCESS_TOKEN_URI = "http://localhost:8080/platform/oauth/token";
	
	public static void main(String[] args) {
		// call oauth2 secured API resource
		getTrustedMessage();
	}
	
	public static void getTrustedMessage() 
	{
		Greeting greeting = trustedClientRestTemplate().getForObject(URI.create(END_POINT), Greeting.class);
		
        System.out.println("Greeting ID:    " + greeting.getId());
        System.out.println("Greeting Content:   " + greeting.getContent());  
	}
	
	public static OAuth2RestTemplate trustedClientRestTemplate() 
	{
		return new OAuth2RestTemplate(trusted(), new DefaultOAuth2ClientContext());
	}

	public static ClientCredentialsResourceDetails trusted() 
	{
		ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
		details.setId("dmApp/trusted");
		details.setClientId("my-client-with-registered-redirect");
		details.setAccessTokenUri(ACCESS_TOKEN_URI);
		details.setScope(Arrays.asList("read", "trust"));
		details.setGrantType("client_credentials");	
		//details.setClientSecret("somesecret");
		return details;
	}

}
