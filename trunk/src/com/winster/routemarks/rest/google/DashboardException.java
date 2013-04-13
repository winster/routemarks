package com.winster.routemarks.rest.google;

import org.apache.http.client.HttpResponseException;

import javax.servlet.http.HttpServletResponse;

/**
 * Exception to wrap an arbitrary exception as a HttpResponseException.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class DashboardException extends HttpResponseException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public DashboardException(int statusCode, String s) {
    super(statusCode, s);
  }

  public DashboardException(Exception ex) {
    super(getStatusFromException(ex), getMessageFromException(ex));
  }

  private static String getMessageFromException(Exception ex) {
    if (ex instanceof com.google.api.client.http.HttpResponseException) {
      com.google.api.client.http.HttpResponseException hrex =
          (com.google.api.client.http.HttpResponseException) ex;
      return "The server encountered an exception: " + hrex.getStatusMessage();
    }
    return "The server encountered an exception: " + ex.getMessage();
  }

  private static int getStatusFromException(Exception ex) {
    if (ex instanceof com.google.api.client.http.HttpResponseException) {
      com.google.api.client.http.HttpResponseException hrex =
          (com.google.api.client.http.HttpResponseException) ex;
      return hrex.getStatusCode();
    }
    return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  }
}
