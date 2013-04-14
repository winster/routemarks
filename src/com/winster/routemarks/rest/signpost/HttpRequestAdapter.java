package com.winster.routemarks.rest.signpost;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;

public class HttpRequestAdapter implements HttpRequest {

    private HTTPRequest request;

    public HttpRequestAdapter(HTTPRequest request) {
        this.request = request;
    }
    
	@Override
	public Map<String, String> getAllHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		List<HTTPHeader> headerList = request.getHeaders();
		String value = null;
		for(HTTPHeader header : headerList) {
			if("Content-Type".equals(header.getName())){
				value = header.getValue();
			}
		}
        return (value == null) ? "" : value;
	}

	@Override
	public String getHeader(String arg0) {
		List<HTTPHeader> headerList = request.getHeaders();
		String value = null;
		if(arg0!=null) {
			for(HTTPHeader header : headerList) {
				if(arg0.equals(header.getName())){
					value = header.getValue();
				}
			}
		}		
        return (value == null) ? "" : value;
	}

	@Override
	public InputStream getMessagePayload() throws IOException {
		return new ByteArrayInputStream(request.getPayload());
	}

	@Override
	public String getMethod() {
		return request.getMethod().name();
	}

	@Override
	public String getRequestUrl() {
		return request.getURL().toString();
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		request.setHeader(new HTTPHeader(arg0, arg1));
	}

	@Override
	public void setRequestUrl(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object unwrap() {
		// TODO Auto-generated method stub
		return null;
	}

}
