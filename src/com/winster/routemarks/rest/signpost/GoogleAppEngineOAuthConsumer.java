package com.winster.routemarks.rest.signpost;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

import com.google.appengine.api.urlfetch.HTTPRequest;

public class GoogleAppEngineOAuthConsumer extends AbstractOAuthConsumer {

	public GoogleAppEngineOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected HttpRequest wrap(Object request) {
		if (!(request instanceof HTTPRequest))
            throw new IllegalArgumentException(
                    "This consumer expects requests of type MultipartPostMethod");
        else
            return new HttpRequestAdapter((HTTPRequest) request);
	}



}
