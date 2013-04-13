package com.winster.routemarks.data.fusion;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.FusiontablesScopes;

public enum FusionTableFactory {
	INSTANCE;
	
	/** E-mail address of the service account. */
	private final String SERVICE_ACCOUNT_EMAIL = "284218108178-0ekd6f78v3831k1267ll3b7j0699mls1@developer.gserviceaccount.com";

	/** Global instance of the HTTP transport. */
	private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	private final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private Fusiontables fusiontables;
	
	public void initialize(HttpServletRequest request){		
		try {
			if(fusiontables==null) {
				fusiontables = new Fusiontables.Builder(
			            HTTP_TRANSPORT, JSON_FACTORY, createCredential(request)).setApplicationName(
			            "Google-FusionTablesSample/1.0").build();
			} else {
				System.out.println("<<<<FT is already initialized>>>>");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("<<<<Exception in FTFactory>>>>");
			e.printStackTrace();
		}
	}
	
	private Credential createCredential(HttpServletRequest request) throws GeneralSecurityException, IOException{
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
            .setServiceAccountScopes(FusiontablesScopes.FUSIONTABLES)
            .setServiceAccountPrivateKeyFromP12File(new File(request.getSession().getServletContext().getRealPath("/WEB-INF/key.p12")))
            //.setServiceAccountUser("wtjose@gmail.com")
            .build();
		return credential;
	}
		
	public Fusiontables getFTInstance(){
		return fusiontables;
	}
	
	public Fusiontables clearFTInstance(){
		return fusiontables;
	}	
	
	public String getServiceAccountEmail(){
		return SERVICE_ACCOUNT_EMAIL;
	}
	
	public HttpTransport getHttpTrasport(){
		return HTTP_TRANSPORT;
	}
	
	public JsonFactory getJsonFactory(){
		return JSON_FACTORY;
	}
}
