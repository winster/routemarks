package com.winster.routemarks.data.helper;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.winster.routemarks.client.vo.ActivityData;
import com.winster.routemarks.client.vo.MarkData;
import com.winster.routemarks.client.vo.TSRMessage;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.DataConstants;
import com.winster.routemarks.constants.GeoAddressConstants;
import com.winster.routemarks.constants.NumeralConstants;
import com.winster.routemarks.data.entity.Mark;
import com.winster.routemarks.data.fusion.FusionTableFactory;
import com.winster.routemarks.data.txn.Transact;
import com.winster.routemarks.util.Converter;
import com.winster.routemarks.util.GoogleUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.google.api.services.fusiontables.Fusiontables.Query.Sql;
import com.googlecode.objectify.TxnType;
import com.googlecode.objectify.cmd.Query;

/**
 * Helper class for all mark entity related activities
 * @author root
 *
 */
public class MarkHelper {

	private static final Logger log = Logger.getLogger(MarkHelper.class.getName());

	/**
	 * Basic mark info should be inserted in a google fusion table. And qualitative data should be inserted inside a datastore entity
	 * @param mark
	 * @throws Exception
	 */
	public static Mark insertMark(MarkData markData) throws Exception {
		try{
			boolean test = false;
			if(!test) {
				insertFusionMark(markData);
			}
			return insertTSRMark(markData);
		} catch (Exception e) {	      
	    	throw new ServletException(e);
	    }
	}
	
	/**
	 * Helper method for inserting a mark into google fusion table.
	 * @param mark
	 * @throws Exception
	 */
	private static void insertFusionMark(MarkData markData) throws Exception {
		try{
			DateFormat date = new SimpleDateFormat("MM/dd/yy");
			String dateStr = date.format(markData.getLastUpdatedDate());
			Sql sql = FusionTableFactory.INSTANCE.getFTInstance().query().sql("INSERT INTO " + DataConstants.MARKER_TABLE_NAME.getValue() + 
							" ('"+DataConstants.MARKER_COLUMN_MARKID.getValue()+"','"+DataConstants.MARKER_COLUMN_TSRID.getValue()+"','"+
							DataConstants.MARKER_COLUMN_GROUPID.getValue()+"','"+DataConstants.MARKER_COLUMN_LOCATION.getValue()+"','"+
							DataConstants.MARKER_COLUMN_DATE.getValue()+"','"+DataConstants.MARKER_COLUMN_CATEGORY.getValue()+"','"+
							DataConstants.MARKER_COLUMN_TRANSPORTATION.getValue()+"','"+DataConstants.MARKER_COLUMN_REASON.getValue()+"','"+
							DataConstants.MARKER_COLUMN_VIDEO.getValue()+"','"+DataConstants.MARKER_COLUMN_IMAGE.getValue()+"','"+
							DataConstants.MARKER_COLUMN_SEVERITY.getValue()+"') " +
							"VALUES ('"+markData.getMarkId()+"','"+markData.getId()+"','"+markData.getGroupId()+"','"+markData.getLocation()+"','"+
							dateStr +"','"+markData.getCategory()+"','"+markData.getTransportation()+"','"+markData.getReason()+"','"+
							markData.getVideoUrl()+"','"+markData.getImageUrl()+"','"+ 
							markData.getSeverity()+"')");
			/*Sqlresponse response = */ sql.execute();
			
		} catch (Exception e) {	      
	    	log.severe(e.getMessage());
	    	throw new ServletException(e);
	    } 
	}
	
	/**
	 * Helper method for inserting a mark into TSR datastore
	 * @param mark
	 * @throws ServletException 
	 */
	private static Mark insertTSRMark(MarkData markData) throws Exception {
		try{
			Mark mark = createMarkObj(markData);
			saveMarkEntity(mark);
			return mark;
		}catch(Exception e) {
			log.severe(e.getMessage());
			throw new Exception(e);
		}
	}

	
	/**
	 * Helper method to find a Mark entity based on primary key :markId
	 * @param id
	 * @return
	 */
	public static Mark fetchMarkEntity(String markId) {
		Mark result = ofy().load().type(Mark.class).id(markId).get();
		return result;		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static long getMarkEntitiesCount(String id) {
		return ofy().load().type(Mark.class).filter("id", id).list().size();	
	}
	/**
	 * Transactionally log in the Person, creating one if necessary
	 */
	@Transact(TxnType.REQUIRED)
	private static void saveMarkEntity(final Mark mark) {
		ofy().save().entity(mark).now();
	}
	
	/**
	 * Updates a mark into fusion table
	 * @param tableId
	 * @param mark
	 * @throws Exception
	 */
	public static void updateMark(ActivityData updateData) throws Exception {
		Mark mark = fetchMarkEntity(updateData.getMarkId());
		if(mark!=null) {
			if(ApplicationConstants.ACTIVITY_TYPE_LIKE.getValue().equals(updateData.getType())){
				mark.setLikeCount(mark.getLikeCount()+1);
			}
			if(ApplicationConstants.ACTIVITY_TYPE_STAR.getValue().equals(updateData.getType())){
				mark.setStarCount(mark.getStarCount()+1);
			}
			if(ApplicationConstants.ACTIVITY_TYPE_DISLIKE.getValue().equals(updateData.getType())){
				mark.setDislikeCount(mark.getDislikeCount()+1);
			}
			saveMarkEntity(mark);
			UserHelper.updateUserActivityMaster(mark.getId(), null, NumeralConstants.POINTS_LIKE.getValue(), null);
		}
	}
	
	/**
	 * Retrieves a list of mark objects based on given input condition
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static TSRMessage[] retrieveMarkList(Map<String,String> input, int limit, int offset) throws Exception{
		String filter1 = input.get("1");
		String filter2 = input.get("2");
		String filter3 = input.get("3");
		String filter4 = input.get("4");
		
		List<TSRMessage> messageList = new ArrayList<TSRMessage>();
		
		
		Query<Mark> markQuery = ofy().load().type(Mark.class).filter("countryCode", filter1);
		if(filter2!=null) {
			markQuery = markQuery.filter("addressLevel1Code", filter2);
		}
		if(filter3!=null) {
			markQuery = markQuery.filter("addressLevel2Code", filter3);
		}
		if(filter2!=null && filter3!=null && filter4!=null) {
			/*Special case for Singapore. 
			In community screen, if Singapore is typed, both CountryCode and LocalityCode are present. 
			And an error is thrown somehow. Need to debug. Till then, this change may work*/
			markQuery = markQuery.filter("localityCode", filter4);
		}
		List<Mark> resultSet = markQuery.limit(limit).offset(offset).order("-lastUpdatedTimeInMillis").list();
		
		if(resultSet!=null) {
			for(Mark mark : resultSet) {
				TSRMessage message = Converter.convertMarkToMessage(mark);
				messageList.add(message);
			}
		}
		return messageList.toArray(new TSRMessage[messageList.size()]);
	}
	
	/**
	 * Helper method for creating Mark entity objects from MarkData
	 * @param markData
	 * @return
	 */
	private static Mark createMarkObj(MarkData markData) {
		Mark mark = new Mark();
		mark.setMarkId(markData.getMarkId());
		mark.setId(markData.getId());
		mark.setUserId(markData.getUserId());
		mark.setGroupId(markData.getGroupId());
		mark.setUserName(markData.getUserName());
		mark.setLocation(markData.getLocation());
		mark.setLastUpdatedTime(markData.getLastUpdatedDate());
		mark.setLastUpdatedTimeInMillis(markData.getLastUpdatedDate().getTime());
		mark.setCategory(markData.getCategory());
		mark.setTransportation(markData.getTransportation());
		mark.setReason(markData.getReason());
		mark.setSeverity(markData.getSeverity());
		mark.setDescription(markData.getDescription());
		mark.setVideoUrl(markData.getVideoUrl());
		mark.setImageUrl(markData.getImageUrl());
		
		mark.setCountryCode(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.COUNTRYCODE));
		mark.setCountry(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.COUNTRY));
		mark.setAddressLevel1Code(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LEVEL1CODE));
		mark.setAddressLevel1(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LEVEL1));
		mark.setAddressLevel2Code(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LEVEL2CODE));
		mark.setAddressLevel2(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LEVEL2));
		mark.setLocalityCode(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LOCALITYCODE));
		mark.setLocality(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.LOCALITY));
		mark.setRoute(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.ROUTE));
		mark.setStreetNumber(GoogleUtil.INSTANCE.getAddress(markData.getAddress(),GeoAddressConstants.STREET_NUMBER));
				
		mark.setLikeCount(0);
		mark.setStarCount(0);
		mark.setDislikeCount(0);
		
		return mark;
	}
	
	/**
	 * Inserts a mark into fusion table
	 * @param tableId
	 * @param mark
	 * @throws Exception
	 */
	/*public static void insertMark(MarkData mark) throws Exception {
		try{
			Sql sql = FusionTableFactory.INSTANCE.getFTInstance().query().sql("INSERT INTO " + DataConstants.MARKER_TABLE_NAME.getValue() + 
				" ('MarkId','Id','UserId','UserName','GroupId','GroupName','EmailId','Location','Date','Nature','Vehicle','Reason','Severity','Description'," +
				"'CountryCode','Country','AddressLevel1Code','AddressLevel1','AddressLevel2Code','AddressLevel2','LocalityCode','Locality','Route'," +
				"'StreetNumber','PostalCode', 'NbrOfLikes', 'NbrOfStars', 'NbrOfDislikes') " +
				"VALUES ('"+mark.getMarkId()+"','"+mark.getId()+"','"+mark.getUserId()+"','"+mark.getUserName()+"','','','"+
				mark.getEmailId()+"','"+mark.getLocation()+"','"+mark.getDate()+"','"+
				mark.getCategory()+"','"+mark.getTransportation()+"','"+
				mark.getReason()+"','"+mark.getSeverity()+"','"+mark.getDescription()+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.COUNTRYCODE)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.COUNTRY)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LEVEL1CODE)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LEVEL1)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LEVEL2CODE)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LEVEL2)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LOCALITYCODE)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.LOCALITY)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.ROUTE)+"','"+
				GoogleUtil.INSTANCE.getAddress(mark.getAddress(),GeoAddressConstants.STREET_NUMBER)+"',''," +
				"'"+mark.getNbrOfLikes()+"','"+mark.getNbrOfStars()+"','"+mark.getNbrOfDisLikes()+"')");

			sql.execute();
		} catch (Exception e) {	      
	    	log.severe(e.getMessage());
	    	throw new ServletException(e);
	    } 
	}*/
	
	
	/**
	 * Read markers based on the given condition
	 * @param input
	 * @return
	 * @throws ServletException
	 */
	/*public static TSRMessage[] getMarkers(Map<String,String> input) throws ServletException{
		if(input==null) {
			return null;
		}
		List<TSRMessage> messageList = new ArrayList<TSRMessage>();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT "+DataConstants.MARKER_COLUMN_USERNAME.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_DATE.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_NATURE.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_TRANSPORTATION.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_REASON.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_SEVERITY.getValue()+",")
						.append(DataConstants.MARKER_COLUMN_DESCRIPTION.getValue()+",")
						.append(" FROM "+DataConstants.MARKER_TABLE_NAME.getValue());
			if(input!=null) {
				queryString.append(" WHERE ");
				queryString.append(DataConstants.MARKER_COLUMN_COUNTRYCODE.getValue()+" = '"+input.get(DataConstants.MARKER_COLUMN_COUNTRYCODE.getValue())+"'");
				if(input.get(DataConstants.MARKER_COLUMN_ADDRESS_LEVEL1CODE.getValue())!=null) {
					queryString.append(" AND "+DataConstants.MARKER_COLUMN_ADDRESS_LEVEL1CODE.getValue()+" = '"+input.get(DataConstants.MARKER_COLUMN_ADDRESS_LEVEL1CODE.getValue())+"'");
				}
				if(input.get(DataConstants.MARKER_COLUMN_ADDRESS_LEVEL2CODE.getValue())!=null) {
					queryString.append(" AND "+DataConstants.MARKER_COLUMN_ADDRESS_LEVEL2CODE.getValue()+" = '"+input.get(DataConstants.MARKER_COLUMN_ADDRESS_LEVEL2CODE.getValue())+"'");
				}
				if(input.get(DataConstants.MARKER_COLUMN_LOCALITYCODE.getValue())!=null) {
					queryString.append(" AND "+DataConstants.MARKER_COLUMN_LOCALITYCODE.getValue()+" = '"+input.get(DataConstants.MARKER_COLUMN_LOCALITYCODE.getValue())+"'");
				}
			}
						
			Sql sql = FusionTableFactory.INSTANCE.getFTInstance().query().sql(queryString.toString());
			Sqlresponse result = sql.execute();
			List<List<Object>> rows = result.getRows();
			if(rows!=null) {
				for(List<Object> row : rows) {
					TSRMessage message = new TSRMessage();
					if(row.get(0)!=null) {
						message.setUsername(row.get(0).toString());
					}
					if(row.get(1)!=null) {
						message.setDate(row.get(1).toString());
					}
					if(row.get(2)!=null) {
						message.setCategory(row.get(2).toString());
					}
					if(row.get(3)!=null) {
						message.setTransportation(row.get(3).toString());
					}
					if(row.get(4)!=null) {
						message.setNature(row.get(4).toString());
					}
					if(row.get(5)!=null) {
						message.setSeverity(row.get(5).toString());
					}
					if(row.get(6)!=null) {
						message.setDescription(row.get(6).toString());
					}
					message.setCountrycode(input.get(DataConstants.MARKER_COLUMN_COUNTRYCODE.getValue()));
					message.setLocality(input.get(DataConstants.MARKER_COLUMN_LOCALITYCODE.getValue()));
					messageList.add(message);
				}
			}
		} catch (IOException e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
		return messageList.toArray(new TSRMessage[messageList.size()]);
		
	}*/
}
