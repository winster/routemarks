<%@ page import="com.winster.routemarks.client.vo.AccountDetails" %>
<!DOCTYPE html>
<html lang="en">
   <head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# routemarks: http://ogp.me/ns/fb/routemarks#">
    <meta charset="utf-8">
    <title>ROUTEMARKS</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="One stop location for travellers to get information about their route. Also a platform to share the news!">
    <meta name="author" content="Winster T. Jose">

    <!-- Le styles --> 
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/native.css" rel="stylesheet">
    
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
	
    <!-- Fav and touch icons -->
    <link rel="shortcut icon" href="assets/img/favicon.ico">
    <link rel="canonical" href="http://www.routemarks.com" />
 

	  <meta property="fb:app_id"                content="485688004787851" /> 
	  <meta property="og:type"                  content="routemarks:spot" /> 
	  <meta property="og:url"                   content="http://www.routemarks.com/loc/*" /> 
	  <meta property="og:title"                 content="Route Marks&middot; Make your routes more safe and secure" /> 
	  <meta property="og:image"                 content="http://www.routemarks.com/favicon.ico" /> 
 
  </head>
  
  <body>
	
	<div id="fb-root"></div>
	<script>(function(d, s, id) {
	  var js, fjs = d.getElementsByTagName(s)[0];
	  if (d.getElementById(id)) return;
	  js = d.createElement(s); js.id = id;
	  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1&appId=485688004787851";
	  fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));</script>

	<div class="navbar navbar-inverse navbar-fixed-top masthead">
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span10">
					<a href="/" style="text-decoration: none;"><img src="assets/img/logo.png" alt="Makes your route safe"><span class="logo">ROUTEMARKS</span></a>
				</div>
				<div class="span2">			
					<%if(session.getAttribute("userloginstatus")!=null){
		                 String userName = ((AccountDetails)session.getAttribute("account")).getUserName();
		             %>
					<a href="#account"><%=userName%></a>
				  	<%}else{ %>
					<a href="#login" data-toggle="modal">ID Card</a>
				  	<%}%>
				</div>
			</div>
		</div> 
    </div>
    <div class="container-fluid app-container">
    	<div class="row-fluid">
    		<div class="span7">
    			<section> 
				    <div class="row-fluid detailsdiv">
				    	<div class="input-prepend span4">
							<span class="add-on"><i class=" icon-hand-right"></i></span>
							<input class="input-large" id="start" type="text" placeholder="Enter start point">
							<input id="currentlocation" type="hidden">
				  		</div>				  		
				  		<div class="input-append span4">
							<input class="input-large" id="end" type="text"  placeholder="Enter stop point">
							<input id="marklocation" type="hidden">
							<span class="add-on"><i class=" icon-hand-left"></i></span>
				  		</div>
				  		<div class="span2">
				  			<button type="button" id="showroutetomark" class="btn btn-primary btn-small pull-right hide">
					  					Show Route <i class="icon-forward"></i>
					  		</button>
					  	</div>
						<div class="span2 details hide pull-right">								
							<button type="button" class="btn btn-primary btn-small">Get Report <i class="icon-forward"></i></button>
						</div>
					</div>
			    	<div class="mini-layout fluid">
			        	<div class="mini-layout-sidebar hide">
					  		<button class="btn btn-large btn-block disabled" type="button">View Marks</button>
						  	<div class="hide filterdiv">
								<select class="selectboxblue span12" id="filter">
									<option value="today" selected="selected">Today</option>						
									<option value="lastday">From Last Day</option>
									<option value="lastweek">From Last Week</option>
									<option value="lastmonth">From Last Month</option>
									<option value="lastyear">From Last Year</option>
									<option value="all">All</option>						
								</select>
					        </div>					        
						  	<div id="routes"></div>
						</div>
			        	<div class="mini-layout-body">
			           		<div id="map_canvas"></div>
			           	</div>
			        </div>
				</section>
			    <div class="infobox-wrapper">
			    	<div class="infobox">You are somewhere around here!</div>
			  	</div>
    		</div>
	    	<div class="span5">
		    	<section class="communitysection"> 
					<div class="row-fluid show-grid">
						<div class="span6"> 
						  <input class="input-large" id="location" type="text" placeholder="Search for place">
						</div>
						<div class="span6">
							<span class="badge badge-info locationSelected">...</span>
						</div>						
					</div>
					<div class="pull-right">
						<abbr title="eye witness"><i class="icon-eye-open"></i></abbr>
						<abbr title="I concur"><i class="icon-bell"></i></abbr>
						<abbr title="fake information"><i class="icon-trash"></i></abbr>
					</div>
					<hr>
					<div class="alert fade in hide">
					    <a class="close" data-dismiss="alert" href="#">&times;</a>
					    <strong>Warning!</strong> Could not make a connection with server. Please refresh the page.  
				    </div>
				    <div class="progress progress-striped active hide">
				        <div class="bar" style="width: 0%;"></div>
				    </div>
				    <div class="nomarks hide"><code>No marks available here.</code></div>
					<div class="communityUpdates"></div>
					<div class="row-fluid show-grid morerow hide">
						<div class="span12">			
							<button type="button" class="btn btn-info">more &middot;  &middot;  &middot;</button>
						</div>		
					</div>
				</section>
	  		</div>
	  	</div>	    
	  	<!-- Le javascript
	    ================================================== -->
	    <!-- Placed at the end of the document so the pages load faster -->
	    <script src="assets/js/ext/jquery-1.9.1.min.js"></script>
	    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
		<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=places"></script>
	  	<script type="text/javascript" src="/_ah/channel/jsapi"></script>
		<script src="assets/js/infobox.js"></script>
		<script src="assets/js/template.js"></script>
	  	<script src="assets/js/app.js" ></script>
	</div> <!-- /container -->	
  	<div id="phonePromo">
  		<div id="divider">
  			<a href="#mobile"><span class="promoLink">Get RouteMarks on your phone   <i class="icon-chevron-down"></i></span></a>
  		</div>
		<div class="row-fluid" id="mobile">
			<div class="offset2 span8">
				<div class="row-fluid">
					<div class="span6" id="textColumn">
						<h2>Be social</h2>
		  				<p>Discover and learn about the routes nearby, search for what you are craving, and get details along the way. Save marks and analyze the routes.</p>
		  				<div id="downloadLinks">
		  					<div id="deviceLogos">
		  						<h2>Get the app
			  						<a target="_blank" href="https://play.google.com/store/apps/details?id=info.tsr.mobile#" rel="nofollow" id="androidLink" title="Download ROUTEMARKS from Google Play">								
									</a>
								</h2>
		  					</div>
		  				</div>
					</div>
					<div class="span5" id="imageColumn">
					</div>			
				</div>
			</div>
		</div>
  	</div>
	<div class="footer">
		<div class="footercontent">
			<div class="row-fluid">
		   		<div class="span5" >
		   			<div class="fb-like" data-href="http://www.routemarks.com/" data-send="true" data-layout="button_count" data-width="450" data-show-faces="false"></div>
		   		
		   			<g:plusone></g:plusone>
			   		<script type="text/javascript">
				      window.___gcfg = {
				        lang: 'en-US'
				      };
				
				      (function() {
				        var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
				        po.src = 'https://apis.google.com/js/plusone.js';
				        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
				      })();
				    </script>
				    <script type="text/javascript">
					  var _gaq = _gaq || [];
					  _gaq.push(['_setAccount', 'UA-39564014-1']);
					  _gaq.push(['_trackPageview']);
					
					  (function() {
					    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
					    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
					    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
					  })();					
					</script>
		   		</div>
		   		<div class="span3" style="text-align:center">
		   	 		<a target="_blank" href="http://winstertjose.com">winstertjose.com</a>&copy; 2012 
		   		</div>
				<div class="span4 pull-right footerlinks">
					<a href="assets/about.html">about</a>
					<a target="_blank" href="https://docs.google.com/spreadsheet/viewform?formkey=dFdWai1JMmFKX1RCWTROMm1CZXltMWc6MA">feedback</a><!-- <a href="#feedback" data-toggle="modal">feedback</a> -->
					<a href="assets/policies.html">policies</a> 
					<a href="assets/disclaimer.html">disclaimer</a>
					<a href="assets/help.html">help</a>			  
				</div>		   
		 	</div>
		</div>	   	 
	</div>
	<!-- Modal -->
	<div id="login" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    <h3 id="myModalLabel">login for ID card</h3>
	  </div>
	  <div class="modal-body">
	  	<section class="loginsection">
			<div class="container-fluid">
				<div class="row-fluid">
			    	<div class="offset3 span2">
			      		<a href="socialAuth/auth?id=facebook"><img src="assets/img/facebook_icon.png" alt="Facebook" 
			              			title="Facebook" border="0"></img></a>                				      			
			      	</div>
				    <div class="span2">
				      	<a href="socialAuth/auth?id=twitter"><img src="assets/img/twitter_icon.png" alt="Twitter" 
			              			title="Twitter" border="0"></img></a>
				    </div>
				    <div class="span2">
				     	<a href="socialAuth/google"><img src="assets/img/google_icon.jpg" alt="Google" 
			              			title="Google" border="0"></img></a>
					</div>
			  	</div>			  	
			  	<br>
			  	<p class="text-success">After login, visit profile to manage integration with your service provider.</p>
			</div>
		</section>  
	  </div>
	  <div class="modal-footer">
	    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	  </div>
	</div>
	
	<div class="markinfowindowcontainer hide">
	    <div class="markinfowindow infobox">
	        <div class="data-entry current">
				<div class="row-fluid show-grid">
					<div class="span11">
						<select class="selectbox span6 category" autofocus="autofocus">
							<option value="" disabled="disabled" selected="selected" style="display:none">Select Category</option>
							<option value="Accident">Accident</option>
							<option value="Natural Disaster">Natural Disaster</option>
						</select>
						<select class="selectbox span6 transportation_types">
							<option value="" disabled="disabled" selected="selected" style="display:none">Select Transport Mode</option>
							<option value="Bicycle">BiCycle</option>
							<option value="Car">Car</option>
							<option value="Motor Cycle">Motor Cycle</option>
							<option value="Public Transport">Public Transport</option>
							<option value="SUV">SUV</option>
							<option value="Truck">Truck</option>
							<option value="Train">Train</option>						
						</select>
						<select class="selectbox hide span6 transportation_na">
							<option value="" disabled="disabled" selected="selected" style="display:none">Not Applicable</option>
						</select>
					</div>	
				</div>
				<div class="row-fluid show-grid">
					<div class="span11">
						<textarea class="news span8" rows="2" placeholder="Enter a small description of the accident" maxlength="65"></textarea>
						<button type="button" class="btn btn-primary nextdiv">Submit <i class="icon-forward"></i></button>
					</div>
				</div>
				<div class="row-fluid show-grid">
					<div class="span11">
						<select class="selectbox span6 reason_accident">
							<option value="" disabled="disabled" selected="selected" style="display:none">Select Reason</option>
							<option value="Over speed">Over speed</option>
							<option value="Reckless driving">Reckless driving</option>
							<option value="Sharp Curve">Sharp Curve</option>
							<option value="Traffic rule violation">Traffic rule violation</option>
							<option value="Wrong message board">Wrong message board</option>
							<option value="Ice road">Ice road</option>
							<option value="Other">Other</option>
						</select>
						<select class="selectbox hide span6 reason_calamity">
							<option value="" disabled="disabled" selected="selected" style="display:none">Select Nature</option>
							<option value="Avalanches and Landslides">Avalanche and Landslides</option>
							<option value="Earthquake">Earthquake</option>
							<option value="Hurricanes or Typhoons">Hurricane or Typhoon</option>
							<option value="Tornadoes">Tornado</option>
							<option value="Tsunamis">Tsunami</option>
							<option value="Volcanic Eruptions">Volcanic Eruptions</option>
							<option value="Other">Other</option>
						</select>
						<select class="selectbox span6 severity">
							<option value="" disabled="disabled" selected="selected" style="display:none">Select Severity</option>
							<option value="Critical">Critical</option>
							<option value="Major">Major</option>
							<option value="Minor">Minor</option>
						</select>
					</div>	
				</div>
			</div>
			<div class="data-submit">
				<button type="button" class="btn btn-small btn-info prevdiv pull-left"> <i class="icon-backward"></i> Back</button>
				<div class="row show-grid">
					<div class="span4 text-error">Please read policies before you submit. Please refrain<br> from submitting this mark, if you are unsure about the incident.</div>
				</div>	
				<div class="input-prepend pagination-centered">
					<span class="add-on captchalabel"></span>
					<input class="input-medium captchaInput" type="text" placeholder="Are you human?">
				</div>				
				<button type="button" class="btn btn-block btn-success submitmarker">Submit</button>				
			</div>
	    </div>
    </div>
	
	<div id="reportModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="reportLabel" aria-hidden="true">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
		   	<h3 id="reportLabel"></h3>
		 </div>
		 <div class="modal-body">
		 	<section class="reportsection">
			   	<table class="table table-hover">
					<thead>
						<tr>
							<th style="width:50%">Category</th>
							<th style="width:50%">Mark Count</th>
						</tr>
					</thead>
		            <tbody>
		                <tr class="success">
		                  <td>All</td>
		                  <td><span id="all"></span></td>
		                </tr>
		                <tr class="success">
		                  <td>Today</td>
		                  <td><span id="today"></span></td>
		                </tr>
		                <tr class="success">
		                  <td>Yesterday</td>
		                  <td><span id="yesterday"></span></td>
		                </tr>
		                <tr class="success">
		                  <td>Last week</td>
		                  <td><span id="lastweek"></span></td>
		                </tr>
		                <tr class="success">
		                  <td>Last Month</td>
		                  <td><span id="lastmonth"></span></td>
		                </tr>		                
		                <tr class="success">
		                  <td>Last year</td>
		                  <td><span id="lastyear"></span></td>
		                </tr>
		                <tr class="success">
		                  <td>Average per day</td>
		                  <td><span id="average"></span></td>
		                </tr>
		            </tbody>
		          </table>		 
		          <table class="table table-hover">
					<thead>
						<tr>
							<th style="width:50%">Bounds</th>
							<th style="width:50%">Location</th>
						</tr>
					</thead>
		            <tbody>
		                <tr class="error">
		                  <td>South West Latitude</td>
		                  <td><span id="SWLAT"></span></td>
		                </tr>
		                <tr class="error">
		                  <td>South West Longitude</td>
		                  <td><span id="SWLNG"></span></td>
		                </tr>
		                <tr class="error">
		                  <td>North East Latitude</td>
		                  <td><span id="NELAT"></span></td>
		                </tr>
		                <tr class="error">
		                  <td>North East Longitude</td>
		                  <td><span id="NELNG"></span></td>
		                </tr>
		            </tbody>
		        </table>	
		   	</section>
		 </div>
		 <div class="modal-footer">
		   	<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		 </div>	
	</div>
		
	<div id="accountModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="accountLabel" aria-hidden="true">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
		   	<h3 id="accountLabel">ID Card</h3>
		 </div>
		 <div class="modal-body">
		 	<section>
				<div class="container-fluid">
					<div class="row-fluid">
						<!-- <div class="span6">Welcome <span id="username" class="text-info"></span></div>-->
						<div class="offset6 span6 useractions">
							<%
								String accountType = null;
								if(session.getAttribute("userloginstatus")!=null){
									accountType = ((AccountDetails)session.getAttribute("account")).getAccountType();
								}
							%>								
							<fieldset>
								<%if(!"google".equals(accountType)) {%>						
				            	<a href="managesocial">Manage <%=accountType%></a><br>
								<!-- <a href="newgroup"><span>Create user group</span></a> -->
								<%}%>					
				            </fieldset>
						</div>
					</div>
				</div>
				<div class="greencard-layout">
					<div class="greencard-layout-body">
						<button class="btn btn-large btn-block btn-success" type="button">Green Card</button>
						<table class="table table-hover">
				            <tbody>
				                <tr class="success">
				                  <td>Current score</td>
				                  <td><span id="totalPoints"></span></td>
				                </tr>
				                <tr class="error">
				                  <td>Recent activity</td>
				                  <td><span id="recentActivity"></span></td>
				                </tr>
				                <tr class="info">
				                  <td>Recent criteria</td>
				                  <td><span id="recentCriteria"></span></td>
				                </tr>
				                <tr class="warning">
				                  <td>Total activities</td>
				                  <td><span id="totalActivityCount"></span></td>
				                </tr>		                
				                <tr class="success">
				                  <td>Marks created</td>
				                  <td><span id="totalMarkCount"></span></td>
				                </tr>
				                <tr class="error">
				                  <td>Groups own</td>
				                  <td><span id="totalPoints">Not Available</span></td>
				                </tr>
				                <tr class="info">
				                  <td>Group member</td>
				                  <td><span id="totalPoints">Not Available</span></td>
				                </tr>
		              		</tbody>
		            	</table>
					</div>
		          </div>
			</section>
		 </div>
		 <div class="modal-footer">
		   	<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		 </div>	
	</div>
  </body>
</html>