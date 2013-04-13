package com.winster.routemarks.data.helper;

import static com.googlecode.objectify.ObjectifyService.ofy;
import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.client.vo.PreferenceData;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.GeoAddressConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.data.entity.User;
import com.winster.routemarks.data.entity.UserActivity;
import com.winster.routemarks.data.entity.UserActivityMaster;
import com.winster.routemarks.data.entity.UserCriteria;
import com.winster.routemarks.data.entity.UserPreference;
import com.winster.routemarks.data.txn.Transact;
import com.winster.routemarks.util.GoogleUtil;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.googlecode.objectify.TxnType;

/**
 * A Data helper class for all user entities: User, UserCriteria, UserPreference, UserActivity
 * @author root
 *
 */
public class UserHelper {

	/**
	 * A helper method for creating/updating a user entity into datastore
	 * @param userId
	 * @param addressComponents
	 * @return
	 */
	public static String updateUser(String visitorId,  AccountDetails userDetails) {
		User user = fetchUserEntity(userDetails.getUserId(), userDetails.getAccountType());
		if(user!=null) { //User already exists
			user.setLastUpdatedTime(new Date());
			saveUserEntity(user);			
		}
		User guestUser = null;
		if(visitorId!=null){
			guestUser = fetchUserEntityById(visitorId); //fetch guest user
			if(guestUser!=null) {//Guest user already exists			
				if(user==null){ //New user created 
					user = createUserObj(guestUser, userDetails);
					//String id = UUID.randomUUID().toString();
					//user.setId(id);
					deleteUserEntity(guestUser);
					saveUserEntity(user);
					createUserActivityMaster(user);
				}
			}
		}
		if(user==null && guestUser==null) {//New User directly visits Account page and login; Very rare case
			user = createUserObj(userDetails);
			String id = UUID.randomUUID().toString();
			user.setId(id);
			saveUserEntity(user);
			createUserActivityMaster(user);
		}
		return user.getId();
	}
	
	/**
	 * Creates a userActivityMaster for every User entity
	 * @param user
	 */
	private static void createUserActivityMaster(User user) {
		UserActivityMaster userActivityMaster = new UserActivityMaster();
		userActivityMaster.setId(user.getId());
		double totalPoints = 0;
		long totalActivityCount = 0;
		long totalMarkCount = 0;
		List<UserActivity> activities = fetchUserActivityEntitiesById(user.getId());
		totalActivityCount = activities.size();
		if(activities!=null) {
			for(UserActivity activity : activities) {
				totalPoints += activity.getPoints();
				userActivityMaster.setRecentActivityId(activity.getActivityId());
			}			
		}
		totalMarkCount = MarkHelper.getMarkEntitiesCount(user.getId());
		userActivityMaster.setTotalPoints(totalPoints);
		userActivityMaster.setTotalActivityCount(totalActivityCount);
		userActivityMaster.setTotalMarkCount(totalMarkCount);
		saveUserActivityMasterEntity(userActivityMaster);
	}

	
	/**
	 * 
	 * Updates a userActivityMaster
	 * @param user
	 */
	public static UserActivityMaster updateUserActivityMaster(String id, String type, double points, String recentActivityId) {
		UserActivityMaster userActivityMaster = fetchUserActivityMasterEntityById(id);
		if(userActivityMaster!=null) {
			if(ApplicationConstants.CHILD_ENTITY_TYPE_MARK.getValue().equals(type)) {
				userActivityMaster.setTotalMarkCount(userActivityMaster.getTotalMarkCount()+1);
			}
			if(ApplicationConstants.CHILD_ENTITY_TYPE_ACTIVITY.getValue().equals(type)) {
				userActivityMaster.setTotalActivityCount(userActivityMaster.getTotalActivityCount()+1);
			}
			if(ApplicationConstants.CHILD_ENTITY_TYPE_PREFERENCE.getValue().equals(type)) {
				userActivityMaster.setTotalPreferenceCount(userActivityMaster.getTotalPreferenceCount()+1);
			}
			if(points>0) {
				userActivityMaster.setTotalPoints(userActivityMaster.getTotalPoints()+points);
			}
			if(recentActivityId!=null) {
				userActivityMaster.setRecentActivityId(recentActivityId);
			}
			saveUserActivityMasterEntity(userActivityMaster);
			return userActivityMaster;
		}
		return null;
	}

	
	/**
	 * Helper method for creating an anonymous user entity
	 * @param userId
	 * @param addressComponents
	 * @return
	 */
	public static void createAnonymousUser(String userId, GeocodeAddressComponents[] addressComponents) {
		User user = createAnonymousUserObj(userId, addressComponents);
		saveUserEntity(user);
	}

	/**
	 * Helper method for creating anonymous user object
	 * @param userId
	 * @param addressComponents
	 * @return
	 */
	private static User createAnonymousUserObj(String userId, GeocodeAddressComponents[] addressComponents) {
		User user = new User();
		user.setId(userId);
		user.setUserId(UserConstants.NAME_USER_ANONYMOUS.getValue());
		user.setAccount(UserConstants.NAME_USER_ANONYMOUS.getValue());
		user.setName(UserConstants.NAME_USER_ANONYMOUS.getValue());
		
		user.setCountry(GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.COUNTRY));
		user.setLevel1(GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL1));
		user.setLevel2(GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL2));
		user.setLocality(GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LOCALITY));
		user.setLastUpdatedTime(new Date());
		
		return user;
	}
	
	/**
	 * Helper method for creating user objects from AccountDetails(client object)
	 * @param userDetails
	 * @return
	 */
	private static User createUserObj(AccountDetails userDetails) {
		User user = new User();
		user.setUserId(userDetails.getUserId());
		user.setName(userDetails.getUserName());
		user.setAccount(userDetails.getAccountType());
		user.setLastUpdatedTime(new Date());		
		return user;
	}
	
	/**
	 * Helper method for creating user objects from AccountDetails(client object) and Guest user entity
	 * @param guestUser
	 * @param userDetails
	 * @return
	 */
	private static User createUserObj(User guestUser, AccountDetails userDetails) {
		User user = new User();
		user.setId(guestUser.getId());//To retain activities
		user.setUserId(userDetails.getUserId());
		user.setName(userDetails.getUserName());
		user.setAccount(userDetails.getAccountType());
		
		user.setCountry(guestUser.getCountry());
		user.setLevel1(guestUser.getLevel1());
		user.setLevel2(guestUser.getLevel2());
		user.setLocality(guestUser.getLocality());
		user.setLastUpdatedTime(guestUser.getLastUpdatedTime());
		
		return user;
	}
	

	
	/**
	 * Helper method to create a user criteria
	 * @param id
	 * @param clientId
	 * @param addressComponents
	 * @param firstVisit
	 */
	public static void createCriteria(String id, String clientId, 
									GeocodeAddressComponents[] addressComponents, boolean firstVisit) {
		UserCriteria result = fetchUserCriteriaEntityById(id);
		if(result!=null) {
			updateCriteria(result, addressComponents);
			ofy().save().entity(result);
		} else {
			UserCriteria criteria = createCriteriaObj(id, clientId, addressComponents, firstVisit);
			createCriteriaEntity(criteria);
		}
	}
	
	/**
	 * Helper method to update a user criteria
	 * @param criteria
	 * @param addressComponents
	 */
	private static void updateCriteria(UserCriteria criteria, GeocodeAddressComponents[] addressComponents) {
		String country = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.COUNTRYCODE);
		String level1 = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL1CODE);
		String level2 = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL2CODE);
		String locality = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LOCALITYCODE);
		criteria.setCountry(country);
		criteria.setLevel1(level1);
		criteria.setLevel2(level2);
		criteria.setLocality(locality);
		criteria.setCriteria(country+"_"+level1+"_"+level2+"_"+locality);
		Date date = new Date();
		criteria.setLastUpdatedTime(date);
		criteria.setLastUpdatedTimeInMillis(date.getTime());
	}

	/**
	 * Helper method to create a User Criteria object
	 * @param id
	 * @param clientId
	 * @param addressComponents
	 * @param firstVisit
	 * @return
	 */
	private static UserCriteria createCriteriaObj(String id, String clientId, 
									GeocodeAddressComponents[] addressComponents, boolean firstVisit) {
		UserCriteria criteria = new UserCriteria();
		criteria.setId(id);
		criteria.setClientId(clientId);
		String country = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.COUNTRYCODE);
		String level1 = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL1CODE);
		String level2 = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL2CODE);
		String locality = GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LOCALITYCODE);
		criteria.setCountry(country);
		criteria.setLevel1(level1);
		criteria.setLevel2(level2);
		criteria.setLocality(locality);
		if(firstVisit) {
			criteria.setCriteria(country+"_null_null_null");
		} else {
			criteria.setCriteria(country+"_"+level1+"_"+level2+"_"+locality);
		}
		Date date = new Date();
		criteria.setLastUpdatedTime(date);
		criteria.setLastUpdatedTimeInMillis(date.getTime());
		return criteria;
	}
	
	/**
	 * Helper method to create a User Preference Entity
	 * @param id
	 * @param preferenceId
	 * @param preferenceData
	 */
	public static void createUserPreference(String id, String preferenceId, PreferenceData preferenceData) {
		UserPreference preference = new UserPreference();
		preference.setId(id);
		preference.setPreferenceId(preferenceId);
		preference.setType(preferenceData.getType());
		preference.setValue(preferenceData.getValue());
		saveUserPreferenceEntity(preference);
	}

	
	/**
	 * Helper method to create new user activity record
	 * @param userDetails
	 * @param toggleCheckbox
	 */
	public static String createUserActivity(String activityId, String id, String type, String desc, double points) {
		UserActivity activity = new UserActivity();
		activity.setActivityId(activityId);
		activity.setType(type);
		activity.setDesc(desc);
		activity.setPoints(points);
		activity.setId(id);
		return saveUserActivityEntity(activity);
	}

	/* ******Find operations for All User entities : User, UserCriteria, UserActivity, UserActivityMaster, UserPreference****** */
	
	/**
	 * Helper method to find a user entity based on userId (obtained from open auth) and account type
	 * @param id
	 * @param accountType - Not required here. Because Id is unique and is generated from TSR
	 * @return
	 */
	private static User fetchUserEntity(String userId, String accountType){
		List<User> resultSet = ofy().load().type(User.class).filter("userId", userId).list();
		User result = null;
		for(User user : resultSet) {
			if(user.getAccount().equals(accountType)) {
				result = user;
				break;
			}
		}
		return result;		
	}

	/**
	 * Helper method to find a user entity based on primary key :Id 
	 * @param id
	 * @return
	 */
	private static User fetchUserEntityById(String id){
		User result = ofy().load().type(User.class).id(id).get();
		return result;		
	}
	
	/**
	 * Helper method to find a user entity based on primary key :Id 
	 * @param id
	 * @return
	 */
	public static UserCriteria fetchUserCriteriaEntityById(String id){
		UserCriteria result = ofy().load().type(UserCriteria.class).id(id).get();
		return result;		
	}
	
	/**
	 * Helper method to find a UserActivityMaster entity based on primary key :Id 
	 * @param id
	 * @return
	 */
	public static UserActivityMaster fetchUserActivityMasterEntityById(String id){
		UserActivityMaster result = ofy().load().type(UserActivityMaster.class).id(id).get();
		return result;		
	}
	
	/**
	 * Helper method to find a user preference entity based on index Id
	 * @param id
	 * @return
	 */
	public static List<UserPreference> fetchUserPreferenceEntities(String id) {
		List<UserPreference> result = ofy().load().type(UserPreference.class).filter("id", id).list();
		return result;		
	}
	
	/**
	 * Helper method to find a user UserActivity entity based on primary key :Id
	 * @param id
	 * @return
	 */
	public static UserActivity fetchUserActivityEntity(String id) {
		UserActivity result = ofy().load().type(UserActivity.class).id(id).get();
		return result;		
	}
	
	/**
	 * Helper method to find a list of user UserActivity entities based on index:Id
	 * @param id
	 * @return
	 */
	public static List<UserActivity> fetchUserActivityEntitiesById(String id) {
		List<UserActivity> result = ofy().load().type(UserActivity.class).filter("id",id).list();
		return result;		
	}
	
	

	/* ******************Save Operations for All user entities************************ */
	
	/**
	 * Transactionally log in the Person, creating one if necessary
	 */
	@Transact(TxnType.REQUIRED)
	private static void saveUserEntity(final User user) {
		ofy().save().entity(user).now();
	}
	
	/**
	 * Transactionally creates UserActivityMaster
	 */
	@Transact(TxnType.REQUIRED)
	private static void saveUserActivityMasterEntity(final UserActivityMaster userActivityMaster) {
		ofy().save().entity(userActivityMaster).now();
	}
	

	/**
	 * Helper method to create a User Criteria entity
	 */
	@Transact(TxnType.REQUIRED)
	static void createCriteriaEntity(final UserCriteria criteria) {
		ofy().save().entity(criteria).now();
	}

	/**
	 * Helper method to create a User Preference Entity
	 */
	@Transact(TxnType.REQUIRED)
	static void saveUserPreferenceEntity(final UserPreference preference) {
		ofy().save().entity(preference).now();
	}
	
	/**
	 * Helper method to create a User Activity Entity
	 */
	@Transact(TxnType.REQUIRED)
	static String saveUserActivityEntity(final UserActivity activity) {
		ofy().save().entity(activity).now();
		return activity.getActivityId();
	}

	/* *******************Delete operation for User entity - Only a guest user is deleted*********************** */
	/**
	 * Helper method to delete a user entity
	 */
	@Transact(TxnType.REQUIRED)
	private static void deleteUserEntity(final User user) {
		ofy().delete().entity(user).now();
	}

	/**
	 * Delete a user preference based on user unique Id and preference type
	 * @param id
	 * @param type
	 */
	public static void deleteUserPreference(String id, String type) {
		List<UserPreference> preferenceList = fetchUserPreferenceEntities(id);
		if(preferenceList!=null) {
			for(UserPreference preference : preferenceList) {
				if(preference.getType().equals(type)) {
					ofy().delete().entity(preference).now();
					UserActivityMaster master = fetchUserActivityMasterEntityById(id);
					master.setTotalPreferenceCount(master.getTotalPreferenceCount()-1);
					saveUserActivityMasterEntity(master);
					break;
				}
			}
		}		
	}
	
}
