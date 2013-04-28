package com.winster.routemarks.rest.endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
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
				/*try {
					ServletFileUpload upload = new ServletFileUpload();
				    FileItemIterator iterator = upload.getItemIterator(req);
				    while (iterator.hasNext()) {
				    	FileItemStream item = iterator.next();
				        InputStream stream = item.openStream();
				        String FILE_NAME = "a.jpg";//id+System.currentTimeMillis();
				        if (item.isFormField()) {
				        	log.warning("Got a form field: " + item.getFieldName());
				        } else {
				        	log.warning("Got an uploaded file: " + item.getFieldName() +", name = " + item.getName());
				     	    FileService fileService = FileServiceFactory.getFileService();
						    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
												       .setBucket(BUCKET_NAME)
												       .setKey(FILE_NAME)
												       .setMimeType("image/jpg")
												       .setAcl("public_read")
												       .addUserMetadata("date-created", System.currentTimeMillis()+"");
						    AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
						    // Open a channel to write to it
						    boolean lock = true;
						    FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, lock);
						    // copy byte stream from request to channel
				            byte[] buffer = new byte[10000];
				            int len;
				            while ((len = stream.read(buffer)) > 0) {
				                writeChannel.write(ByteBuffer.wrap(buffer, 0, len));
				            }
				            writeChannel.closeFinally();
						     
				            String filename = "/gs/" + BUCKET_NAME + "/" + FILE_NAME;
				            AppEngineFile readableFile = new AppEngineFile(filename);
				            FileReadChannel readChannel =
				                fileService.openReadChannel(readableFile, false);
				            // Again, different standard Java ways of reading from the channel.
				            BufferedReader reader =
				                    new BufferedReader(Channels.newReader(readChannel, "UTF8"));
				            String line = reader.readLine();
				            
				            // line = "The woods are lovely, dark, and deep."
				            readChannel.close();
				            
						    resp.getWriter().append("http://storage.googleapis.com/"+BUCKET_NAME+"/"+FILE_NAME);
							resp.setStatus(HttpServletResponse.SC_OK);
				        }
				    }
				}  catch (Exception ex) {
					resp.getWriter().append(ex.getMessage());
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			    }	*/
				String BUCKETNAME = "puzzycats", FILENAME="test.html";
				resp.setContentType("text/plain");
			    resp.getWriter().println("Hello, world from java");
			    FileService fileService = FileServiceFactory.getFileService();
			    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
										    .setBucket("my_bucket")
										    .setKey("my_object")
										    .setAcl("public-read")
										    .setMimeType("text/html")
										    .addUserMetadata("date-created", "092011");
			    AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
			    // Open a channel to write to it
			     boolean lock = true;
			     FileWriteChannel writeChannel =
			         fileService.openWriteChannel(writableFile, lock);
			     // Different standard Java ways of writing to the channel
			     // are possible. Here we use a PrintWriter:
			     PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
			     out.println("The woods are lovely dark and deep.");
			     out.println("But I have promises to keep.");
			     // Close without finalizing and save the file path for writing later
			     out.close();
			     
			     writeChannel.closeFinally();
			     resp.getWriter().println("Done writing...");

			     // At this point, the file is visible in App Engine as:
			     // "/gs/BUCKETNAME/FILENAME"
			     // and to anybody on the Internet through Cloud Storage as:
			     // (http://storage.googleapis.com/BUCKETNAME/FILENAME)
			     // We can now read the file through the API:
			     String filename = "/gs/" + BUCKETNAME + "/" + FILENAME;
			     AppEngineFile readableFile = new AppEngineFile(filename);
			     FileReadChannel readChannel =
			         fileService.openReadChannel(readableFile, false);
			     // Again, different standard Java ways of reading from the channel.
			     BufferedReader reader =
			             new BufferedReader(Channels.newReader(readChannel, "UTF8"));
			     String line = reader.readLine();
			     resp.getWriter().println("READ:" + line);

			    // line = "The woods are lovely, dark, and deep."
			     readChannel.close();
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