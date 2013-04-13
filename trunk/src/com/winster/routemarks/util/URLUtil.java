/*
 * $Id$
 */

package com.winster.routemarks.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Some basic utilities for manipulating urls.
 *
 * @author Jeff Schnitzer
 */
public class URLUtil
{
	/**
	 * Stupid java doesn't come with a URL builder.
	 */
	public static String buildURL(String base, Map<String, Object> params)
	{
		if (params == null || params.isEmpty())
			return base;
		else
			return base + "?" + buildQueryString(params);
	}

	/**
	 * Create a query string
	 */
	public static String buildQueryString(Map<String, Object> params)
	{
		StringBuilder bld = new StringBuilder();

		boolean afterFirst = false;
		for (Map.Entry<String, Object> entry: params.entrySet())
		{
			if (afterFirst)
				bld.append("&");
			else
				afterFirst = true;

			bld.append(urlEncode(entry.getKey()));
			bld.append("=");
			bld.append(urlEncode(entry.getValue()));
		}

		return bld.toString();
	}

	/**
	 * Parse a query string.
	 */
	public static LinkedHashMap<String, String> parseQueryString(String queryString)
	{
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		
		String[] pairs = queryString.split("&");
		
		for (String pairStr: pairs) {
			String[] pair = pairStr.split("=");
			result.put(urlDecode(pair[0]), urlDecode(pair[1]));
		}

		return result;
	}

	/**
	 * An interface to URLEncoder.encode() that isn't inane
	 */
	public static String urlEncode(Object value)
	{
		try
		{
			return URLEncoder.encode(value.toString(), "utf-8");
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
	}

	/**
	 * An interface to URLDecoder.decode() that isn't inane
	 */
	public static String urlDecode(Object value)
	{
		try
		{
			return URLDecoder.decode(value.toString(), "utf-8");
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
	}

	/**
	 * Generates a url from the request that is good for sending off with redirects
	 */
	public static String getActualUrl(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();

		String queryString = request.getQueryString();
		if (queryString != null)
		{
			url.append("?");
			url.append(queryString);
		}

		return url.toString();
	}
	/*
	public static String MD5(String text)
		    throws NoSuchAlgorithmException, UnsupportedEncodingException  
	{ 
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5 = new byte[64];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5 = md.digest();
        return convertedToHex(md5);
    } 
	
    private static String convertedToHex(byte[] data) 
    { 
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i < data.length; i++) 
        { 
            int halfOfByte = (data[i] >>> 4) & 0x0F;
            int twoHalfBytes = 0;
            
            do 
            { 
                if ((0 <= halfOfByte) && (halfOfByte <= 9)) 
                {
                    buf.append( (char) ('0' + halfOfByte) );
                }
                
                else 
                {
                    buf.append( (char) ('a' + (halfOfByte - 10)) );
                }

                halfOfByte = data[i] & 0x0F;

            } while(twoHalfBytes++ < 1);
        } 
        return buf.toString();
    } */
}