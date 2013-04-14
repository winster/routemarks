package com.winster.routemarks.rest.helper;

import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.client.vo.MarkData;

import java.io.BufferedReader;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

/**
 * Helper class for all GeoLocation activities
 * @author root
 *
 */
public class LocationHelper {
	
	private static final Logger log = Logger.getLogger(LocationHelper.class.getName());

	/**
	 * Parse Geo Address from request
	 * @param request
	 * @return
	 * @throws ServletException 
	 */
	public static GeocodeAddressComponents[] retriveGeoAddressFromRequest(HttpServletRequest request) throws ServletException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		    	jb.append(line);
		    log.info("Request Data>>>"+jb.toString());
		} catch (Exception e) { 
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	  
		GeocodeAddressComponents[] addressComponents = new Gson().fromJson(jb.toString(), GeocodeAddressComponents[].class);
		
		return addressComponents;
	}
	
	/**
	 * Parse marker info from request. Also populate user details from session into mark object
	 * @param request
	 * @return
	 * @throws ServletException 
	 */
	public static MarkData retriveMarkFromRequest(HttpServletRequest request) throws ServletException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		    	jb.append(line);
		} catch (Exception e) { 
			log.severe(e.getMessage());
			throw new ServletException(e);
		}	  
		MarkData mark = new Gson().fromJson(jb.toString(), MarkData.class);		
		return mark;
	}
}
