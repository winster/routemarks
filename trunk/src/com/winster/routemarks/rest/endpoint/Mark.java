package com.winster.routemarks.rest.endpoint;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.MarkData;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.NumeralConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.data.fusion.FusionTableFactory;
import com.winster.routemarks.data.helper.CommunityHelper;
import com.winster.routemarks.data.helper.MarkHelper;
import com.winster.routemarks.data.helper.UserHelper;
import com.winster.routemarks.rest.helper.AccountHelper;
import com.winster.routemarks.rest.helper.ActivityHelper;
import com.winster.routemarks.rest.helper.LocationHelper;
import com.winster.routemarks.rest.helper.MarkClientHelper;
import com.winster.routemarks.rest.helper.RequestHelper;
import com.winster.routemarks.rest.helper.SocialAuthHelper;
import com.winster.routemarks.util.SocialUtil;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface which handles all Mark operations
 * @author root
 *
 */
public class Mark extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Mark.class.getName());

	/**
	 * All POST requests are served here
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();	
		if(path.endsWith("report")) {
			resp.sendRedirect("/report");
		}
	}
	/**
	 * All POST requests are served here
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			String path = req.getPathInfo();		
			FusionTableFactory.INSTANCE.initialize(req);
			MarkData markData = LocationHelper.retriveMarkFromRequest(req);
			String id = (String)RequestHelper.getSessionAttribute(req, UserConstants.ATTR_ID.getValue());
			AccountDetails userDetails  = (AccountDetails) req.getSession().getAttribute(UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
			if(path.endsWith("insert")) {
				log.info("Mark values>>>"+markData.toString());
				com.winster.routemarks.data.entity.Mark mark = insertData(markData, req, id, userDetails); 
				if(mark!=null){
					CommunityHelper.broadcastCommunityMessageForNewMark(mark);
					boolean flag =false; 
					if(userDetails!=null) {						
						if(SocialUtil.isSocialConnected(id)) {
							flag = SocialAuthHelper.updateSocialStatus(req, mark);
						}
					}
					resp.getWriter().append("success"+flag);
					resp.setStatus(HttpServletResponse.SC_OK);
				} else{
					resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
					resp.getWriter().append("fail");
					FusionTableFactory.INSTANCE.clearFTInstance();
				}
			} else {
				log.info("Not a request for insert >> "+req.getRequestURI());
			}
		}catch(Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}

	/**
	 * Inserts a row in the newly created table for user. 
	 * @param mark
	 * @param req
	 * @param id
	 * @param userDetails
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private static com.winster.routemarks.data.entity.Mark insertData( MarkData markData, HttpServletRequest req, String id, AccountDetails userDetails) throws ServletException, IOException {
		try {
			if(userDetails ==null){
				AccountHelper.updateMarkCountForVisitor(req);
			} else {
				userDetails.setTotalMarkCount(userDetails.getTotalMarkCount()+1);
			}
			MarkClientHelper.touchMarkData(req, markData);
			String activityId = ActivityHelper.getActivityId(req, id, userDetails);
			//Now touch the entities
			com.winster.routemarks.data.entity.Mark mark = MarkHelper.insertMark(markData);
			UserHelper.createUserActivity(activityId,
											mark.getId(),
											ApplicationConstants.ACTIVITY_TYPE_MARK.getValue(), 
											mark.toString(),
											NumeralConstants.POINTS_MARK.getValue());
			if(userDetails !=null){
				UserHelper.updateUserActivityMaster(mark.getId(), 
												ApplicationConstants.CHILD_ENTITY_TYPE_MARK.getValue(), 
												NumeralConstants.POINTS_MARK.getValue(), 
												activityId);
			}
			return mark;
			
	    } catch (Exception e) {
	    	log.severe(e.getMessage());
	    	throw new ServletException(e);
	    }
	}
}