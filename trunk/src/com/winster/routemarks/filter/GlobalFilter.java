package com.winster.routemarks.filter;

import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.util.CookieUtil;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalFilter implements Filter {

    /**
     * Default constructor. 
     */
    public GlobalFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		if(CookieUtil.getCookieValue(req, ApplicationConstants.TOKEN_TSR.getValue())==null) {
			String cookie = UUID.randomUUID().toString();
			CookieUtil.setCookie(res, ApplicationConstants.TOKEN_TSR.getValue(), cookie, 3600);
			CookieUtil.setCookie(res, ApplicationConstants.TOKEN_FIRST_VISIT.getValue(), "true",3600);
		}
		
		chain.doFilter(request, response);
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
