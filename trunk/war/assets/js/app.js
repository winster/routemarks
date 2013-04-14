$(document).ready(initialize);

var layer, map, googlePosition, directionsService, infowindow, 
		infoWindowContent = null, markers = [], marker = new google.maps.Marker({ map: map	});
var SWLAT,SWLNG,NELAT,NELNG;

/***********Search routes starts here**************/
/**
 * 
 */
function initialize() {
	directionsService = new google.maps.DirectionsService();
	var rendererOptions = {hideRouteList: false};
	directionsRenderer = new google.maps.DirectionsRenderer(rendererOptions);
	var mapOptions = {
    	zoom: 18,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map($('#map_canvas')[0],mapOptions);
    directionsRenderer.setMap(map);
    infowindow = getInfoBox($(".infobox")[0], 280, 0.75);
    if(navigator.geolocation) {
    	getGeoLocation();
    } else {
    	// Browser doesn't support Geolocation
    	handleNoGeolocation(false);
    }
    var startinput = $('#start')[0];
    var startautocomplete = new google.maps.places.Autocomplete(startinput);
    startautocomplete.bindTo('bounds', map);
	google.maps.event.addListener(startautocomplete, 'place_changed', function() {
		var place = startautocomplete.getPlace();
		autoCompleteField(place, startinput, infowindow);
        calcRoute();
        handleEnableDisable();
        $("#end").focus();
    });
    var endinput = $('#end')[0];
    var endautocomplete = new google.maps.places.Autocomplete(endinput);
    endautocomplete.bindTo('bounds', map);
    google.maps.event.addListener(endautocomplete, 'place_changed', function() {
    	$(".filterdiv").hide();
    	$(".details").hide();
    	var place = endautocomplete.getPlace();
        autoCompleteField(place, endinput, infowindow);
        calcRoute();
        handleEnableDisable();
    });
    attachEventListeners();	
}
function autoCompleteField(place, input, infobox) {
	marker.setVisible(false);
    if (!place.geometry) {
    	// Inform the user that the place was not found and return.
    	$(input).addClass('notfound');
    	return;
    } else {
    	$(input).removeClass('notfound');
    }
    // If the place has a geometry, then present it on a map.
    if (place.geometry.viewport) {
    	map.fitBounds(place.geometry.viewport);
    	map.setZoom(15);
    } else {
    	map.setCenter(place.geometry.location);
    	map.setZoom(15);  // Why 17? Because it looks good.
    }
    var image = new google.maps.MarkerImage(
    		place.icon,
    		new google.maps.Size(71, 71),
    		new google.maps.Point(0, 0),
    		new google.maps.Point(17, 34),
    		new google.maps.Size(35, 35));
    marker.setIcon(image);
    marker.setPosition(place.geometry.location);

    var address = '';
    if (place.address_components) {
    	address = [
    	           (place.address_components[0] && place.address_components[0].short_name || ''),
    	           (place.address_components[1] && place.address_components[1].short_name || ''),
    	           (place.address_components[2] && place.address_components[2].short_name || '')
    	           ].join(' ');
    }
    $(infobox.content_).html('<div><strong>' + place.name + '</strong><br>' + address);
    infobox.open(map, marker);
}
/**
 * 
 * @param date
 * @param spot
 */
function calcRoute(date, spot) {
	if(layer) {
		layer.setMap(null);
	}
	$('#showroutetomark').hide();
	var startinput,endinput;
	if(!spot) {
		startinput = $('#start').val();
		if(startinput==='Current Location') {
			startinput = $('#currentlocation').val();
		}
		endinput = $('#end').val();
		if(endinput==='Mark Location') {
			endinput = $('#marklocation').val();
		}
	} else {
		startinput = $('#currentlocation').val();
		endinput = $('#marklocation').val();
	}
	if(!startinput || !endinput) {	
		return;
	}
	if(!date) {
		date = getDate(-365);
		var description = 'From Last Year';
		$("select#filter option")
		   .each(function() { this.selected = (this.text == description); });
	}
	var request = {
    	origin:startinput,
        destination:endinput,
        travelMode: google.maps.DirectionsTravelMode.DRIVING,
        provideRouteAlternatives:true
    };
    directionsService.route(request, function(response, status) {
    	if (status == google.maps.DirectionsStatus.OK) {
    		directionsRenderer.setDirections(response);
    		directionsRenderer.setPanel($('#routes')[0]);
    		$('#routes').addClass("scrollablepane");
        	SWLAT = response.routes[0].bounds.getSouthWest().lat();
            SWLNG = response.routes[0].bounds.getSouthWest().lng();
            NELAT = response.routes[0].bounds.getNorthEast().lat();
            NELNG = response.routes[0].bounds.getNorthEast().lng();
            
            layer = new google.maps.FusionTablesLayer({
            	query: {
                	select: ' Location ',
                	from: '19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA',
                	//from: '133ytL1x27ziKjcAPei7KURC866U9LdK6wipRtFU',
                	where: "ST_INTERSECTS(Location, RECTANGLE(LATLNG("+SWLAT+", "+SWLNG+"), LATLNG("+NELAT+", "+NELNG+"))) and LastUpdatedDate >= '"+date+"' "
                }
            });
            layer.setMap(map);
            
            $(".mini-layout-sidebar").show();
            $(".mini-layout-body").addClass("mini-layout-body-adjusted");
        }
    });

}

/**
 * 
 * @param spot
 */
function handleEnableDisable(spot){
	var startinput,endinput ;
	if(!spot) {
		startinput = $('#start').val();
		if(startinput==='Current Location') {
			startinput = $('#currentlocation').val();
		}
		endinput = $('#end').val();
		if(endinput==='Mark Location') {
			endinput = $('#marklocation').val();
		}
	} else {
		startinput = $('#currentlocation').val();
		endinput = $('#marklocation').val();
	}
    
	if(startinput && endinput) {
		$(".filterdiv").show();
	    $(".details").show();
	}
}

/**
 * 
 */
function attachEventListeners(){
	$(".details").click(function(){
		getReport();
	});
	$("#filter").change(handleFilter);
	$('#showroutetomark').click(function(){
		calcRoute(null, true);
		$('#showroutetomark').hide();
		handleEnableDisable(true);
	});//Do not display route without users consent    
}

/**
 * 
 */
function handleFilter(){
	layer.setMap(null);
	var option = $("select option:selected").val();
	var date = '01/01/13';
	if(option==='today') {
		date = getDate(0);
	} if(option==='lastday') {
		date = getDate(-1);
	} else if(option==='lastweek') {
		date = getDate(-7);
	} else if(option==='lastmonth') {
		date = getDate(-30);
	} else if(option==='lastyear') {
		date = getDate(-365);
	}     
	calcRoute(date);
}

/**
 * 
 * @param infowindow
 */
function getGeoLocation(){
	navigator.geolocation.getCurrentPosition(function(position) {
		googlePosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
		map.setCenter(googlePosition);
		if(infowindow) {//infowindow is null from mark screen and do not display infowindow in that case
			infowindow.close();
			var marker = new google.maps.Marker({ position: googlePosition});
			infowindow.open(map, marker);
			map.panTo(googlePosition);
			geoCodeLocation();
		}
	}, function() {
		handleNoGeolocation(true);
	});  
}
/**
 * Invoked when a row in community message section is selected
 * @param location
 * @returns
 */
function showMarkOnMap(location){
	$(".details").hide();
	var locs = location.split(",");
	var pos = new google.maps.LatLng(locs[0],locs[1]);
	map.setCenter(pos);
	map.setZoom(18);
	getLocationfromGoogle(pos, function(geo_address){
		var target = getLocationStringFromGeo(geo_address.address_components); 
		var spotMarker = new google.maps.Marker({
    		position: pos,
    		map: map,
    		title: target
    	});
		$('#marklocation').val(target);
		$('#end').val("Mark Location");		
		getLocationfromGoogle(googlePosition, function(current_geo_address){
    		var source = getLocationStringFromGeo(current_geo_address.address_components);
    		$('#currentlocation').val(source);
    		$('#start').val("Current Location");
    		$('#showroutetomark').show();
		});
	});
}

/**
 * 
 * @param latLng
 * @param callback
 * @returns
 */
function getLocationfromGoogle(latLng, callback) {
	var geocoder = new google.maps.Geocoder();
	geocoder.geocode({ 'latLng': latLng}, function (results, status) {
    	if (status == google.maps.GeocoderStatus.OK) {
        	//console.log(results[0].formatted_address);
    		if (callback && typeof(callback) === "function") {  
    			callback(results[0]);  
    	    }
        } else {
        	console.log('Google convertion is not succesfully done.');  
        }
    });
}
/************Search marks ends here************************/
/************Community script starts here***************/
/**
 * 
 */
function geoCodeLocation(){
	var geocoder = new google.maps.Geocoder();
    geocoder.geocode({ 'latLng': googlePosition}, function (results, status) {
    	if (status == google.maps.GeocoderStatus.OK) {
        	//console.log(results[0].formatted_address);
    		requestServerForToken(results[0].address_components);
    		//Create anonymous user as a post loading process
    	    createAnonymous();
        } else {
        	console.log('Google convertion is not succesfully done.');  
        }
    });
}

/**
 * 
 * @param geo_address_components
 */
function requestServerForToken(geo_address_components){
	$('.progress').show();
	var maxWidth = $('.progress').width();
	var factor = maxWidth/10;
	var progress = setInterval(function() {
	    var $bar = $('.bar');
	    if ($bar.width()>=maxWidth) {
	        clearInterval(progress);
	        $('.progress').removeClass('active');
	        $('.progress').hide();
	    } else {
	        $bar.width($bar.width()+factor);
	    }
	    $bar.text(parseInt($bar.width()/(factor*.1)) +10+ "%");
	}, 800);
	
	$.ajax({
		  url:"communitytoken/list",
		  type:"POST",
		  data:JSON.stringify(geo_address_components),
		  contentType:"application/json; charset=utf-8",
		  dataType:"json",
		  success: function(msg){
			  channel = new goog.appengine.Channel(msg.token);
			  socket = channel.open();
			  socket.onopen = function(){
				  /*clear progress flag*/
				  clearInterval(progress);
			      $('.progress').removeClass('active');
			      $('.progress').hide();
			      /* progress bar*/
				  onOpened(geo_address_components, msg);
				};
			  socket.onmessage = onMessage;
			  socket.onerror = onError;
			  socket.onclose = onClose;
		  },
		  error : function(){ 
			  /*clear progress flag*/
			  clearInterval(progress);
		      $('.progress').removeClass('active');
		      $('.progress').hide();
		      /* progress bar*/
			  $('.alert').show();
			  deleteAllCookies();
		  } 
		});
}
/**
 * 
 * @param geo_address_components
 * @param message
 */
function onOpened(geo_address_components, message){
	var autocomplete = new google.maps.places.Autocomplete($("#location")[0], {});
    google.maps.event.addListener(autocomplete, 'place_changed', function() {
        var place = autocomplete.getPlace();
        /*var infobox = getInfoBox($(".infobox")[0], 280, 0.75);
        autoCompleteField(place, $("#location")[0], infobox);*/
        $(".locationSelected").text(getLocationStringFromGeo(place.address_components));
        $.ajax({
  		  url:"communitytoken/criteria",
  		  type:"POST",
  		  data:JSON.stringify(place.address_components),
  		  contentType:"application/json; charset=utf-8",
  		  success: function(msg){},
  		  error : function(){} 
  		});
    });

	$(".locationSelected").text(getCountryFromGeo(geo_address_components).long_name);
	loadUpdates(message);
}

/**
 * 
 * @param msg
 */
function onMessage(msg){
	var json = $.parseJSON(msg.data);
	loadUpdates(json);
}

/**
 * 
 */
function onError(){}

/**
 * 
 */
function onClose(){}

/**
 * 
 * @param geo_address_components
 * @returns
 */
function getCountryFromGeo(geo_address_components) {
	var country;
	$.each(geo_address_components, function(i, obj) {
		if($.inArray("country", obj.types)>-1){
			country = obj;
		}
	});
	return country;
}

/**
 * 
 * @param geo_address_components
 * @returns
 */
function getLocationStringFromGeo(geo_address_components) {
	var location;
	$.each(geo_address_components, function(i, obj) {
		if(!location) {
			location = obj.long_name;
		} else {
			location+=" , "+obj.long_name;
		}
	});
	return location;
}

/**
 * 
 * @param data
 */
function loadUpdates(data){
	loadTemplate().done(function(){
		fillUpdates(data);
	});
}

var communityRowTemplate;
/**
 * 
 * @returns
 */
function loadTemplate(){
    var dfd = $.Deferred();
    $.ajax({
        url : "assets/messageTemplate.html",
        contentType : "text/html",
        success : function(resp) {
        	communityRowTemplate = new ElementTpl(resp);
            dfd.resolve();
        },
        error : function(jqXHR, textStatus, errorThrown) {
            console.log("Error");
        }
    });
    return dfd;
}

/**
 * 
 * @param data
 */
function fillUpdates(data) {
	$(".nomarks").hide();
	if(data.loadType==='reload') {
		$('.communityUpdates').empty();
		if(data.updates.length===0) {
			$(".nomarks").show();
		}
	}
	if(data.updates.length===0) {
		$(".morerow button").hide();
	}
	$.each(data.updates, function(index, item) {
		var communityUpdate = communityRowTemplate.fill(item);
		attachMessageActionsListeners(communityUpdate, item);
		if(data.loadType==='prepend') {
			$('.communityUpdates').prepend(communityUpdate);
		} else {
			$('.communityUpdates').append(communityUpdate);
		}
	});
	$(".morerow").show();
	$(".morerow").click(function(){
		$.post("communitytoken/more",function(){
			
		}).success(function(){
			
		});
	});
}

/**
 * 
 * @param communityUpdate
 */
function attachMessageActionsListeners(communityUpdate, item){
	$(communityUpdate).find(".star").click(starMark);
	$(communityUpdate).find(".like").click(likeMark);
	$(communityUpdate).find(".dislike").click(dislikeMark);	
	$(communityUpdate).find(".profile a").click(
											function(){
												showMarkOnMap(item.location);
											});
}

/**
 * 
 */
function starMark(){
	var starcount = $(this).find(".starcount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"star","count":starcount, "markId" :markId};
	updateMark(data);
}

/**
 * 
 */
function likeMark(){
	var likecount = $(this).find(".likecount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"like","count":likecount, "markId" :markId};
	updateMark(data);
}

/**
 * 
 */
function dislikeMark(){
	var dislikecount = $(this).find(".dislikecount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"dislike","count":dislikecount, "markId" :markId};
	updateMark(data);	
}

/**
 * 
 * @param data
 */
function updateMark(data){
	$.post("communitytoken/updatemark",JSON.stringify(data))
		.success(function(msg) {
			var newcount = parseInt(data.count) +1;
			if(data.type==='star') {
				$("#"+data.markId).parent().find(".starcount").text(newcount);
			} else if(data.type==='like') {
				$("#"+data.markId).parent().find(".likecount").text(newcount);
			} else if(data.type==='dislike') {
				$("#"+data.markId).parent().find(".dislikecount").text(newcount);
			}
		})
		.error(function() {
			console.log("error");
		});
}

/**
 * 
 * @param address_components
 */
function createAnonymous(address_components){
	if(getCookie("firstvisit")==="true") {
		$.ajax({
			  url:"user/anonymous",
			  type:"POST",
			  data:JSON.stringify(address_components),
			  contentType:"application/json; charset=utf-8",
			  dataType:"json",
			  success: function(msg){
				  console.log("successful");
			  },
			  error : function(){ 
				  console.log('error while sending request!');
			  } 
			});
	}
}

/**********Community script ends here***********/

/****Report Section Starts Here*****/
/**
 * 
 * @returns
 */
function getReport() {
	
	$("#SWLAT").text(SWLAT);
	$("#SWLNG").text(SWLNG);
	$("#NELAT").text(NELAT);
	$("#NELNG").text(NELNG);
	
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "all");
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "today");
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "yesterday");
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "lastweek");
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "lastmonth");
	getData("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA", "lastyear");
	
	$('#reportModal').modal('show');
	
	$("#reportLabel").text("Report : "+$('#start').val() + " - "+ $('#end').val());
}

/**
 * 
 * @param name
 * @returns
 */
function gup( name ){
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");  
	var regexS = "[\\?&]"+name+"=([^&#]*)";  
	var regex = new RegExp( regexS );  
	var results = regex.exec( window.location.href ); 
	 if( results == null )    return "";  
	else    return results[1];
}

/**
 * 
 */
function getData(table, type) {
    // Builds a Fusion Tables SQL query and hands the result to dataHandler()

    var queryUrlHead = 'http://www.google.com/fusiontables/api/query?sql=';
    var queryUrlTail = '&jsonCallback=?'; // ? could be a function name
    
    // write your SQL as normal, then encode it
    var query = "SELECT TSRId FROM " + table + " WHERE ST_INTERSECTS(Location, RECTANGLE(LATLNG("+SWLAT+", "+SWLNG+"), LATLNG("+NELAT+", "+NELNG+"))) ";
    if(type==="all") {
    	query += "AND LastUpdatedDate >= '01/01/2013'" 
    } else if(type==="today") {
    	query += "AND LastUpdatedDate >= '"+getDate(0)+"'" 
    } else if(type==="yesterday") {
    	query += "AND LastUpdatedDate >= '"+getDate(-1)+"'" 
    } else if(type==="lastweek") {
    	query += "AND LastUpdatedDate >= '"+getDate(-7)+"'" 
    } else if(type==="lastmonth") {
    	query += "AND LastUpdatedDate >= '"+getDate(-30)+"'" 
    } else if(type==="lastyear") {
    	query += "AND LastUpdatedDate >= '"+getDate(-365)+"'" 
    }
    var queryurl = encodeURI(queryUrlHead + query + queryUrlTail);

    var jqxhr = $.get(queryurl, function(data){
    	populateResult(data, type);
    }, "jsonp");
}

function populateResult(data, type) {
	$("#"+type).text(data.table.rows.length);
	if(type==='all'){
		$("#average").text(Math.round(data.table.rows.length/365*100)/100);
	}
}
/****Report Section Ends Here*****/

/** Common script starts here****/
/**
 * 
 * @param content
 * @param width
 * @param opacity
 * @returns {InfoBox}
 */
function getInfoBox(content, width, opacity){
	if(!opacity) {
		opacity=1;
	}
	return new InfoBox({
        content: content,
        disableAutoPan: false,
        maxWidth: width,
        pixelOffset: new google.maps.Size(-140, 0),
        zIndex: null,
        boxStyle: {
           background: "url('assets/img/tipbox.gif') no-repeat",
           opacity: opacity,
           width: width+"px"
       },
       closeBoxMargin: "12px 4px 2px 2px",
       closeBoxURL: "assets/img/close.gif",
       infoBoxClearance: new google.maps.Size(1, 1)
    });
}

/**
 * 
 * @param errorFlag
 */
function handleNoGeolocation(errorFlag) {
	if (errorFlag) {
		var content = 'Error: The Geolocation service failed.';
	} else {
		var content = 'Error: Your browser doesn\'t support geolocation.';
	}
	map.setCenter(new google.maps.LatLng(40.714353, -74.005973));
}

/**
 * 
 * @param map
 * @param place
 * @param input
 * @param infobox
 * @param marker
 */
function autoCompleteField( place, input, infobox) {
	marker.setVisible(false);
    if (!place.geometry) {
    	// Inform the user that the place was not found and return.
    	$(input).addClass('notfound');
    	return;
    } else {
    	$(input).removeClass('notfound');
    }
    // If the place has a geometry, then present it on a map.
    if (place.geometry.viewport) {
    	map.fitBounds(place.geometry.viewport);
    	map.setZoom(15);
    } else {
    	map.setCenter(place.geometry.location);
    	map.setZoom(15);  // Why 17? Because it looks good.
    }
    var image = new google.maps.MarkerImage(
    		place.icon,
    		new google.maps.Size(71, 71),
    		new google.maps.Point(0, 0),
    		new google.maps.Point(17, 34),
    		new google.maps.Size(35, 35));
    marker.setIcon(image);
    marker.setPosition(place.geometry.location);

    var address = '';
    if (place.address_components) {
    	address = [
    	           (place.address_components[0] && place.address_components[0].short_name || ''),
    	           (place.address_components[1] && place.address_components[1].short_name || ''),
    	           (place.address_components[2] && place.address_components[2].short_name || '')
    	           ].join(' ');
    }
    $(infobox.content_).html('<div><strong>' + place.name + '</strong><br>' + address);
    infobox.open(map, marker);
}

/**
 * Utility method to get date 0: today, -1: yesterday, 7: lastweek, -30: last month, -365 : last year
 * @param input
 * @returns {Date}
 */
function getDate(input){
	var now = new Date(), date = null;
	switch(input){
		case 0: 
			now.setDate(now.getDate());
			var mm = (now.getMonth()+1);
			if(mm<10){
				mm='0'+mm;
			}
			date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
			break;
		case -1:
			now.setDate(now.getDate() - 1);
			var mm = (now.getMonth()+1);
			if(mm<10){
				mm='0'+mm;
			}
			date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
			break;
		case -7:
			now.setDate(now.getDate() - 7);
			var mm = (now.getMonth()+1);
			if(mm<10){
				mm='0'+mm;
			}
			date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
			break;
		case -30:
			now.setDate(now.getDate() - 30);
			var mm = (now.getMonth()+1);
			if(mm<10){
				mm='0'+mm;
			}
			date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
			break;
		case -365:
			now.setDate(now.getDate() - 365);
			var mm = (now.getMonth()+1);
			if(mm<10){
				mm='0'+mm;
			}
			date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
			break;
	}
	return date;
}

/**
 * 
 * @param name
 * @returns
 */
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
/***Common script ends here***/

function getDummyData() {
	var data = {loadType:'reload',updates:[{markId:"101", location:'10.115253001463541,76.50215864181519', starCount:"2",likeCount:"3",dislikeCount:"6",username:"Winster",countrycode:"IN",locality:"Kelakam",category:"Accident",transportation:"Car",nature:"Traffic rule violation",severity:"critical",description:"It was a mistake from truck driver that took lives of 3."},
	  	               {markId:"102",starCount:"2",likeCount:"3",dislikeCount:"6",username:"Jose",countrycode:"US",locality:"Columbia",category:"Disaster",transportation:"Public Transport",nature:"Wrong message board",severity:"major",description:"Correct the message board."}]};
	//var message = {data:data};
	return data;
}