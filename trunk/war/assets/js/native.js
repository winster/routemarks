
google.maps.event.addDomListener(window, 'load', initialize);

var map;
var directionDisplay;
var directionsService;
var options = {map: map};
var boxpolys = null;
//var routeBoxer = null;
var infowindow;
var marker = new google.maps.Marker({ map: map	});
var SWLAT,SWLNG,NELAT,NELNG; 
var layer;
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
    //routeBoxer = new RouteBoxer();
    //HTML5 geolocation
    var loc = getLocationPathParam();
    if(!loc) {
	    if(navigator.geolocation) {
	    	handleGeoLocation(infowindow);
	    } else {
	    	// Browser doesn't support Geolocation
	    	handleNoGeolocation(false);
	    }
    } else {
    	var locs = loc.split(",");
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
    		navigator.geolocation.getCurrentPosition(function(position) {
    			var current_pos = new google.maps.LatLng(position.coords.latitude,
    	                           position.coords.longitude);
    			getLocationfromGoogle(current_pos, function(current_geo_address){
    	    		var source = getLocationStringFromGeo(current_geo_address.address_components);
    	    		$('#currentlocation').val(source);
    	    		$('#start').val("Current Location");
    	    		$('#showroutetomark').show();
    			});
    		});
		});
    }
    var startinput = $('#start')[0];
    var startautocomplete = new google.maps.places.Autocomplete(startinput);
    startautocomplete.bindTo('bounds', map);
	google.maps.event.addListener(startautocomplete, 'place_changed', function() {
		var place = startautocomplete.getPlace();
		autoCompleteField(map, place, startinput, infowindow, marker);
        calcRoute();
        handleEnableDisable();
    });

    var endinput = $('#end')[0];
    var endautocomplete = new google.maps.places.Autocomplete(endinput);
    endautocomplete.bindTo('bounds', map);

    google.maps.event.addListener(endautocomplete, 'place_changed', function() {
    	$(".filterdiv").hide();
    	$(".details").hide();
    	var place = endautocomplete.getPlace();
        autoCompleteField(map, place, endinput, infowindow, marker);
        calcRoute();
        handleEnableDisable();
    });
    attachAnalyzers();
    //Create anonymous user as a post loading process
    postLoadingProcess();
}

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
		date = getLastYearDate();
		var description = 'From Last Year';
		$("select#filter option")
		   .each(function() { this.selected = (this.text == description); });
	}
	//clearBoxes();
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
        	// Box around the overview path of the first route
            /*var path = response.routes[0].overview_path;
            var boxes = routeBoxer.box(path, 1);
            //drawBoxes(boxes);
            var SWLAT = boxes[0].getSouthWest().lat();
            var SWLNG = boxes[0].getSouthWest().lat();
            var NELAT = boxes[boxes.length-1].getNorthEast().lat();
            var NELNG = boxes[boxes.length-1].getNorthEast().lat();
            
            for (var i = 0; i < boxes.length; i++) {
            	if(boxes[i].getSouthWest().lat()<SWLAT){
            		SWLAT = boxes[i].getSouthWest().lat();
            	}
            	if(boxes[i].getSouthWest().lng()<SWLNG){
            		SWLNG = boxes[i].getSouthWest().lng()
            	}
            	if(boxes[i].getNorthEast().lat()>NELAT){
            		NELAT = boxes[i].getNorthEast().lat()
            	}
            	if(boxes[i].getNorthEast().lng()>NELNG){
            		NELNG = boxes[i].getNorthEast().lng()
            	}
            }*/
    		SWLAT = response.routes[0].bounds.getSouthWest().lat();
            SWLNG = response.routes[0].bounds.getSouthWest().lng();
            NELAT = response.routes[0].bounds.getNorthEast().lat();
            NELNG = response.routes[0].bounds.getNorthEast().lng();
            
            layer = new google.maps.FusionTablesLayer({
            	query: {
                	select: ' Location ',
                	from: '19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA',
                	//from: '133ytL1x27ziKjcAPei7KURC866U9LdK6wipRtFU',
                	where: "ST_INTERSECTS(Location, RECTANGLE(LATLNG("+SWLAT+", "+SWLNG+"), LATLNG("+NELAT+", "+NELNG+"))) and LastUpdatedDate >= '"+date+"' "/* +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.64, 76.8), LATLNG(8.65, 76.9))) '/*or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.59, 76.8), LATLNG(8.60, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.60, 76.8), LATLNG(8.61, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.61, 76.8), LATLNG(8.62, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.62, 76.8), LATLNG(8.63, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.63, 76.8), LATLNG(8.64, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.64, 76.8), LATLNG(8.65, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.65, 76.8), LATLNG(8.66, 76.9))) or ' +
                            'ST_INTERSECTS(Location, RECTANGLE(LATLNG(8.66, 76.8), LATLNG(8.67, 76.9)))'*/
                	
                }
            });
            layer.setMap(map);
        }
    });

}

//Draw the array of boxes as polylines on the map
function drawBoxes(boxes) {
	boxpolys = new Array(boxes.length);
	for (var i = 0; i < boxes.length; i++) {
		boxpolys[i] = new google.maps.Rectangle({
			bounds: boxes[i],
			fillOpacity: 0,
			strokeOpacity: 1.0,
			strokeColor: '#000000',
			strokeWeight: 1,
			map: map
		});
	}
}

//Clear boxes currently on the map
function clearBoxes() {
	if (boxpolys != null) {
		for (var i = 0; i < boxpolys.length; i++) {
			boxpolys[i].setMap(null);
		}
	}
	boxpolys = null;
}

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

function attachAnalyzers(){
	$(".details").click(function(){
		location.href="/report?SWLAT="+SWLAT+"&SWLNG="+SWLNG+"&NELAT="+NELAT+"&NELNG="+NELNG;
	});
	$("#filter").change(handleFilter);
	$('#showroutetomark').click(function(){
		calcRoute(null, true);
		$('#showroutetomark').hide();
		handleEnableDisable(true);
	});//Do not display route without users consent    
}

function handleFilter(){
	layer.setMap(null);
	var option = $("select option:selected").val();
	var date = '01/01/13';
	if(option==='today') {
		date = getTodaysDate();
	} if(option==='lastday') {
		date = getYesterdaysDate();
	} else if(option==='lastweek') {
		date = getLastWeekDate();
	} else if(option==='lastmonth') {
		date = getLastMonthDate();
	} else if(option==='lastyear') {
		date = getLastYearDate();
	}     
	calcRoute(date);
}