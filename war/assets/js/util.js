/*$(window).unload(function() {
	$.get("user/invalidate");
});*/

$(document).ready(function() {
	highlightCurrentTab();
});

function autoCompleteField( map, place, input, infobox, marker ) {
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

function handleGeoLocation(infowindow){
	navigator.geolocation.getCurrentPosition(function(position) {
		var pos = new google.maps.LatLng(position.coords.latitude,
                           position.coords.longitude);
		map.setCenter(pos);
		if(infowindow) {//infowindow is null from mark screen and do not display infowindow in that case
			infowindow.close();
			/*infowindow.setContent('You are located here!');
			infowindow.setPosition(pos);
			infowindow.open(map);*/
			var marker = new google.maps.Marker({ position: pos});
			infowindow.open(map, marker);
			map.panTo(pos);
		}
	}, function() {
		handleNoGeolocation(true);
	});  
}

function handleNoGeolocation(errorFlag) {
	if (errorFlag) {
		var content = 'Error: The Geolocation service failed.';
	} else {
		var content = 'Error: Your browser doesn\'t support geolocation.';
	}
	map.setCenter(new google.maps.LatLng(60, 105));
}
function validateCaptcha(parent){
	$(parent).find('.captchamessage').empty();
	var d = $(parent).find('.captchaInput').val();
	if (d == c) {
		return true;
	} else {
		$(parent).find('.captchaInput').val("Wrong input. Try again!");
		reloadCaptchaDiv(parent);		
		return false;
	}
}
var a,b,c;
function reloadCaptchaDiv(parent){
	a = Math.ceil(Math.random() * 10);
    b = Math.ceil(Math.random() * 10);       
    c = a + b;
    $(parent).find(".captchalabel").empty();
    //$(parent).find(".captchaInput").val('');
    $(parent).find(".captchaInput").focus(function() {
        $(this).val('');
    })
    //$(".captchaInput").val("");
    $(parent).find(".captchalabel").text("What is "+ a + " + " + b +"? ");
}

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
           background: "url('../img/tipbox.gif') no-repeat",
           opacity: opacity,
           width: width+"px"
       },
       closeBoxMargin: "12px 4px 2px 2px",
       closeBoxURL: "assets/img/close.gif",
       infoBoxClearance: new google.maps.Size(1, 1)
    });
}

function getCurrentLocationfromGoogle(callback) {
	navigator.geolocation.getCurrentPosition(
	  function(pos) {
	    var geocoder = new google.maps.Geocoder();
	    var latLng = new google.maps.LatLng(pos.coords.latitude,pos.coords.longitude);
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
	  },function error(msg){	
		  	console.log('Error occured! '+msg); 
		  	if (callback && typeof(callback) === "function") {
		  		callback(getDummyGeocoderResponse());  
		  	}
	  }	  
	);
}

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

function setCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

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

function deleteCookie(name) {
    setCookie(name,"",-1);
}

function deleteAllCookies(){
	var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {   
        var spcook =  cookies[i].split("=");
        deleteCookie(spcook[0]);
    }
}

function postLoadingProcess(noLocation) {
	if(noLocation) {
		getCurrentLocationfromGoogle(function(geo_address_components){
			createAnonymous(geo_address_components.address_components);
		});  	
	} else {
		createAnonymous(getDummyGeocoderResponse());
	}
}


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

function getCountryFromGeo(geo_address_components) {
	var country;
	$.each(geo_address_components, function(i, obj) {
		if($.inArray("country", obj.types)>-1){
			country = obj;
		}
	});
	return country;
}

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


function highlightCurrentTab() {
	var currenturl = location.href;
	var index = currenturl.lastIndexOf("/");
	var relativeurl = currenturl.substring(index+1, currenturl.length);
	if(!relativeurl || relativeurl ==='home') {
		relativeurl = "/home";
	}
	$('a[href="' + relativeurl + '"]').parent().addClass('active');
}

function getDummyGeocoderResponse(){
	var response =  [
		            {
		               "long_name" : "United States",
		               "short_name" : "US",
		               "types" : [ "country", "political" ]
		            }
		         ];
	return response;
}


function getTodaysDate(){
	var now = new Date();
	now.setDate(now.getDate());
	var mm = (now.getMonth()+1);
	if(mm<10){
		mm='0'+mm;
	}
	date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
	return date;
}
function getYesterdaysDate(){
	var now = new Date();
	now.setDate(now.getDate() - 1);
	var mm = (now.getMonth()+1);
	if(mm<10){
		mm='0'+mm;
	}
	date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
	return date;
}

function getLastWeekDate(){
	var now = new Date();
	now.setDate(now.getDate() - 7);
	var mm = (now.getMonth()+1);
	if(mm<10){
		mm='0'+mm;
	}
	date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
	return date;
}

function getLastMonthDate(){
	var now = new Date();
	now.setDate(now.getDate() - 30);
	var mm = (now.getMonth()+1);
	if(mm<10){
		mm='0'+mm;
	}
	date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
	return date;
}

function getLastYearDate(){
	var now = new Date();
	now.setDate(now.getDate() - 365);
	var mm = (now.getMonth()+1);
	if(mm<10){
		mm='0'+mm;
	}
	date = mm+"/"+now.getDate()+"/"+(now.getFullYear()-2000);
	return date;
}

function gup( name ){
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");  
	var regexS = "[\\?&]"+name+"=([^&#]*)";  
	var regex = new RegExp( regexS );  
	var results = regex.exec( window.location.href ); 
	 if( results == null )    return "";  
	else    return results[1];
}

function getQueryVariable(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if(pair[0] == variable){return pair[1];}
  }
  return(false);
}

function getLocationPathParam() {
	 var pathname = window.location.pathname;
	 var parts = pathname.split('/');
	 var paramValue;
	 if(parts && parts.length>1) {
		 for(var i=0;i<parts.length;++i) {
			 if(parts[i]==='loc') {
				 paramValue = parts[i+1]; 
				 break;
			 }
		 } 
	 }	
	 return paramValue;
}