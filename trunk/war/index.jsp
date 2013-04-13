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

   	<div class="masthead">
   		<div class="mastheadcontent">
			<div>
				<div class="pull-left">
					<a href="/home" style="text-decoration: none;"><img src="assets/img/logo.png" alt="Makes your route safe"><span class="logo">ROUTEMARKS</span></a>
				</div>
				<div class="welcomebar">
					<a href="#login" data-toggle="modal">ID</a>
				</div>
			</div>
		</div>              
	</div> 	
    <div class="container-narrow">
    <div class="row-fluid">
		<div class="span10 text-warning"><strong>Find marks in a time period</strong> / <strong>Analyze safety</strong> / <strong>View safety report </strong>of any route.</div>
		<div class="span2 details hide">								
			<button type="button" class="btn btn-primary btn-small">Get Report <i class="icon-forward"></i></button>
		</div>
	</div>
  
    <section> 
    	<div class="mini-layout fluid">
        	<div class="mini-layout-sidebar">
		  		<button class="btn btn-large btn-block disabled" type="button">Select your route</button>
		  		<br>
  		    	<div class="input-prepend">
					<span class="add-on"><i class=" icon-hand-right"></i></span>
					<input class="input-large" id="start" type="text" placeholder="Enter start point">
					<input id="currentlocation" type="hidden">
		  		</div>
		  		<div class="input-append">
					<input class="input-large" id="end" type="text"  placeholder="Enter stop point">
					<input id="marklocation" type="hidden">
					<span class="add-on"><i class=" icon-hand-left"></i></span>
		  		</div>
		  		<div>
		  			<button type="button" id="showroutetomark" class="btn btn-primary btn-small pull-right hide">
		  					Show Route <i class="icon-forward"></i>
		  			</button>
			  	</div>
			  	<div id="routes"></div>
			  	<div class="hide filterdiv">
					<select class="selectboxblue span2" id="filter">
						<option value="today" selected="selected">Today</option>						
						<option value="lastday">From Last Day</option>
						<option value="lastweek">From Last Week</option>
						<option value="lastmonth">From Last Month</option>
						<option value="lastyear">From Last Year</option>
						<option value="all">All</option>						
					</select>
		        </div>
			</div>
        	<div class="mini-layout-body">
           		<div id="map_canvas"></div>
           	</div>
        </div>
	</section>
    <div class="infobox-wrapper">
    	<div class="infobox">You are somewhere around here!</div>
  	</div>
  	<!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="assets/js/ext/jquery-1.9.1.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
	<script src="assets/js/template.js"></script>
  	<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=places"></script>
	<script src="assets/js/infobox.js"></script>
	<script src="assets/js/native.js" ></script>
	<script src="assets/js/util.js" ></script>
	</div> <!-- /container -->
	
  	<div id="phonePromo">
  		<div id="divider">
  			<a href="#pitch"><span class="promoLink">Get RouteMarks on your phone   <i class="icon-chevron-down"></i></span></a>
  		</div>
		<div class="row-fluid" id="pitch">
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
	
  </body>
</html>