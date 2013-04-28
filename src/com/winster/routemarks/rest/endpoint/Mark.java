package com.winster.routemarks.rest.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;
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

/**
 * Interface which handles all Mark operations
 * @author root
 *
 */
public class Mark extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Mark.class.getName());
	
	private static final String BUCKET_NAME = ApplicationConstants.CLOUD_STORAGE_BUCKET_NAME.getValue();

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
			String id = (String)RequestHelper.getSessionAttribute(req, UserConstants.ATTR_ID.getValue());
			AccountDetails userDetails  = (AccountDetails) req.getSession().getAttribute(UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
			if(path.endsWith("insert")) {
				MarkData markData = LocationHelper.retriveMarkFromRequest(req);
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
			} else if(path.endsWith("uploadimg")){
				try {
					ServletFileUpload upload = new ServletFileUpload();
					upload.setSizeMax(102400);
				    FileItemIterator iterator = upload.getItemIterator(req);
				    while (iterator.hasNext()) {
				    	FileItemStream item = iterator.next();
				        InputStream stream = item.openStream();
				        String FILE_NAME = id+"_"+item.getName();
				        String mimeType = null;
				        if(item.getName().indexOf("png")>-1){
				        	mimeType = "image/png";
				        } else if(item.getName().indexOf("gif")>-1){
				        	mimeType = "image/gif";
				        } else if(item.getName().indexOf("jpg")>-1 || item.getName().indexOf("jpeg")>-1){
				        	mimeType = "image/jpg";
				        } else {
				        	resp.getWriter().append("Upload failed : File format not supported.");
							resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return;
				        }
				        if (item.isFormField()) {
				        	log.warning("Got a form field: " + item.getFieldName());
				        } else {
				        	log.warning("Got an uploaded file: " + item.getFieldName() +", name = " + item.getName());				        	
				        	byte[] imageData = IOUtils.toByteArray(stream);				        	
				            /*ImagesService imagesService = ImagesServiceFactory.getImagesService();
				            Image oldImage = ImagesServiceFactory.makeImage(imageData);
				            Transform resize = ImagesServiceFactory.makeResize(200, 300);
				            Image newImage = imagesService.applyTransform(resize, oldImage);
				            byte[] newImageData = newImage.getImageData();*/
				            // Wrap a byte array into a buffer
				            ByteBuffer buf = ByteBuffer.wrap(imageData);
				            // Retrieve bytes between the position and limit
				            // (see Putting Bytes into a ByteBuffer)
				            imageData = new byte[buf.remaining()];
				            // transfer bytes from this buffer into the given destination array
				            buf.get(imageData, 0, imageData.length);

				            FileService fileService = FileServiceFactory.getFileService();
						    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
												       .setBucket(BUCKET_NAME)
												       .setKey(FILE_NAME)
												       .setMimeType(mimeType)
												       .setAcl("public_read")
												       .addUserMetadata("date-created", System.currentTimeMillis()+"");
						    AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
						    // Open a channel to write to it
						    boolean lock = true;
						    FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, lock);
						    writeChannel.write(buf);
				            writeChannel.closeFinally();
						    resp.getWriter().append("http://storage.googleapis.com/"+BUCKET_NAME+"/"+FILE_NAME);
							resp.setStatus(HttpServletResponse.SC_OK);
				        }
				    }
				} catch(SizeLimitExceededException se){
					resp.getWriter().append("Upload failed : Maximum file size is 100kB");
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} catch (Exception ex) {
					resp.getWriter().append(ex.getMessage());
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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