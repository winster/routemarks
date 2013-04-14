package com.winster.routemarks.data.helper;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.winster.routemarks.client.vo.CommunityMessage;
import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.client.vo.TSRMessage;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.GeoAddressConstants;
import com.winster.routemarks.data.entity.Mark;
import com.winster.routemarks.data.entity.UserCriteria;
import com.winster.routemarks.util.Converter;
import com.winster.routemarks.util.GoogleUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;
/**
 * Helper class for all community related activities
 * @author root
 *
 */
public class CommunityHelper {
	
	private static final Logger log = Logger.getLogger(CommunityHelper.class.getName());


	/**
	 * Helper method for sending back to client the recently updated marks.  
	 * @param token
	 * @param id
	 * @param addressComponents
	 * @return
	 * @throws ServletException
	 */
	public static String sendChannelMessageAfterLoad(String token, String id, int limit, int offset,
								Map<String,String> input) throws Exception {
		CommunityMessage messageVO = getMarkList(input, limit, offset);
		messageVO.setToken(token);
		messageVO.setLoadType(ApplicationConstants.COMMUNITY_LOAD_TYPE_RELOAD.getValue());
		String message = new Gson().toJson(messageVO, CommunityMessage.class);
		return message;
	}

	/**
	 * Helper method for sending back to client, recently updated marks for given/new criteria 
	 * @param clientId
	 * @param input
	 * @return
	 * @throws ServletException
	 */
	public static String sendChannelMessageForNewCriteria(String clientId, int limit, int offset, 
			Map<String,String> input, String loadType) throws Exception {
		
		String message = null;
		try{
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			CommunityMessage messageVO = getMarkList(input, limit, offset);
			messageVO.setLoadType(loadType);
			message = new Gson().toJson(messageVO, CommunityMessage.class);
			channelService.sendMessage(new ChannelMessage(clientId, message));
		}catch(Exception e) {
			throw new ServletException(e);
		}
		return message;
	}
	
	/**
	 * A message will be broadcasted to all eligible community listeners whenever a new mark is created.
	 * For example, when a mark is created in Kazhakkuttam-Trivandrum-Kerala-India, following listeners will get an update.
	 * 1. Those who listen to  Kazhakkuttam-Trivandrum-Kerala-India
	 * 2. Those who listen to Trivandrum-Kerala-India
	 * 3. Those who listen to Kerala-India
	 * 4. Those who listen to India
	 * @param token
	 * @param mark
	 * @throws ServletException 
	 */
	public static void broadcastCommunityMessageForNewMark(Mark mark) throws ServletException{
		try{
			TSRMessage message = Converter.convertMarkToMessage(mark);
			List<String> clientIds = fetchClientIds(mark);
			if(!clientIds.isEmpty()) {
				sendCommunityUpdateForNewMark(message, clientIds);
			}
		} catch(Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}
	
	/**
	 * Send community update to all the given clients
	 * @param token
	 * @param message
	 */
	public static void sendCommunityUpdateForNewMark(TSRMessage message, List<String> clientIds){
		log.info("inside updateCommunity!!!");
		CommunityMessage cm = new CommunityMessage();
		TSRMessage[] msgary = new TSRMessage[1];
		msgary[0] = message;
		cm.setUpdates(msgary);
		//cm.setToken("");
		cm.setLoadType(ApplicationConstants.COMMUNITY_LOAD_TYPE_PREPEND.getValue());
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String messageStr = new Gson().toJson(cm, CommunityMessage.class);
		//List<String> tokens = UserHelper.listOnlineUserTokens(null);
		for(String clientId : clientIds) {
			channelService.sendMessage(new ChannelMessage(clientId, messageStr));
		}
	}
	
	/**
	 * Find out all eligible clients for the given mark
	 * @param mark
	 * @return
	 */
	private static List<String> fetchClientIds(Mark mark) {
		String filter1 = mark.getCountryCode();
		String filter2 = mark.getAddressLevel1Code();
		String filter3 = mark.getAddressLevel2Code();
		String filter4 = mark.getLocalityCode();
		
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add(filter1+"_null_null_null");
		filterCriteria.add(filter1+"_"+filter2+"_null_null");
		filterCriteria.add(filter1+"_"+filter2+"_"+filter3+"_null");
		filterCriteria.add(filter1+"_"+filter2+"_"+filter3+"_"+filter4);
		
		List<UserCriteria> criteriaList = new ArrayList<UserCriteria>();
		Date date = new Date();
		long timeInMillis = date.getTime() - (24*60*60*1000);
		criteriaList.addAll(ofy().load().type(UserCriteria.class).filter("lastUpdatedTimeInMillis > ", timeInMillis).filter("criteria in ", filterCriteria).list());
		List<String> clientIds = new ArrayList<String>();
		for(UserCriteria criteria : criteriaList) {
			clientIds.add(criteria.getClientId());
		}
		return clientIds;
	}

	/**
	 * Helper method for creating a clientId
	 * @param addressComponents
	 * @param count
	 * @return
	 */
	@Deprecated
	public static String createClientId(GeocodeAddressComponents[] addressComponents, long count){
		String clientId = null;
		clientId = GoogleUtil.INSTANCE.getAddress(addressComponents, GeoAddressConstants.LOCALITYCODE)+"~"+count;
		return clientId;
	}
	
	/**
	 * Helper method to fetch fusion data. It is appeared to be a heavy call. Need to sort it out.	
	 * @param inputParams
	 * @return
	 * @throws Exception 
	 */
	private static CommunityMessage getMarkList(Map<String,String> inputParams, int limit, int offset) throws Exception{
		CommunityMessage messageVO = new CommunityMessage();
		messageVO.setUpdates(MarkHelper.retrieveMarkList(inputParams, limit, offset));
		return messageVO;
	}	
}
