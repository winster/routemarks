google.maps.event.addDomListener(window, 'load', initialize);

var map;
var directionDisplay;
var directionsService;
var options = {map: map};
var infobox;//infobox for search box
var infowindow;//infobox for marker
var marker = new google.maps.Marker({ map: map	});
var markers = [];
var infoWindowContent = null;// $(".markinfowindow")[0];

function initialize() {
	directionsDisplay = new google.maps.DirectionsRenderer();
	directionsService = new google.maps.DirectionsService();
    var mapOptions = {
    		zoom: 13,
    		mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    // HTML5 geolocation
    if(navigator.geolocation) {
    	handleGeoLocation();
    } else {
    	// Browser doesn't support Geolocation
    	handleNoGeolocation(false);
    }
    map = new google.maps.Map($('#map_canvas')[0], mapOptions);
    directionsDisplay.setMap(map);
    
    var locationinput = $('#location')[0];
    var autocomplete = new google.maps.places.Autocomplete(locationinput);
    autocomplete.bindTo('bounds', map);
    infobox = getInfoBox($(".infobox")[0], 280, 0.75);
    google.maps.event.addListener(autocomplete, 'place_changed', function() {
    	var place = autocomplete.getPlace();
    	autoCompleteField(map, place, locationinput, infobox, marker);
    });
    infoWindowContent  = loadMarkerInfoWindowContent();
    assignListeners();
    //Create anonymous user as a post loading process
    postLoadingProcess();
}
function assignListeners() {
	
	google.maps.event.addListener(map, 'click', function(event) {
		if(this.getZoom()<17) {
			this.setZoom(17);
			map.setCenter(event.latLng);	    	
		} else {
			addMarker(event.latLng);
		}
    });
    
}

function assignInfoBoxListeners(clonedObj){
	$(clonedObj).find(".nextdiv").bind( "click", function(event) {
    	navigateToSubmit(event);
	});
	$(clonedObj).find(".prevdiv").bind( "click", function(event) {
    	navigateToData(event);
	});
	$(clonedObj).find(".category").bind( "change", function(event) {
    	selectNatureElements(event);
	});
	$(clonedObj).find(".submitmarker").bind( "click", function(event) {
    	if(validate(event)) {
    		getLocationfromGoogle(marker.getPosition(), function(geo_address){
												insertMark(event, geo_address.address_components);
											}
    		);    		
    	}
	});   
}

function attachListenerToMarker(marker) {
	google.maps.event.addListener(marker, 'click', function () {
		if(infowindow){
			infowindow.close();
		}
		var clonedObj = $(infoWindowContent).clone()[0];
		infowindow = getInfoBox(clonedObj,350, 1);
		assignInfoBoxListeners(clonedObj);
		
		infowindow.open(map, this);
    	map.panTo(this.getPosition());
    });
}

//Add a marker to the map and push to the array.
function addMarker(location) {
	marker = new google.maps.Marker({
		position: location,
		map: map,
		draggable : true,
	});
	var id = marker.__gm_id;
	markers[id] = marker;
	attachListenerToMarker(marker);
	google.maps.event.addListener(marker, "rightclick", function (point) { 
		id = this.__gm_id; 
		delMarker(id); 
	});
}

// Sets the map on all markers in the array.
function setAllMap(map) {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(map);
	}
}

// Removes the overlays from the map, but keeps them in the array.
function clearOverlays() {
	setAllMap(null);
}

// Shows any overlays currently in the array.
function showOverlays() {
	setAllMap(map);
}
function delMarker(id) {
    marker = markers[id]; 
    marker.setMap(null);
}
// Deletes all markers in the array by removing references to them.
function deleteOverlays() {
	clearOverlays();
	markers = [];
}


function loadMarkerInfoWindowContent(){
	$.ajax({
		url : "../templates/markerInfoWindow.html",
		contentType : "text/html",
		success : function(resp) {
			infoWindowContent = resp;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log("Error");
		}
	});  
}


function insertMark(event, address_components){
	var location = marker.getPosition().lat()+","+marker.getPosition().lng();
	var category = $(event.target).parent().parent().find(".category").val();
	var transportation = $(event.target).parent().parent().find(".transportation_types").val();
	var reason = $(event.target).parent().parent().find(".reason_accident").val();
	var severity = $(event.target).parent().parent().find(".severity").val();
	if(category==='Natural Disaster') {
		transportation= '';
		reason = $(event.target).parent().parent().find(".reason_calamity").val();
	}
	var description = $(event.target).parent().parent().find(".news").val();
	var data = {"location":location,
				"category":category,"transportation":transportation,
				"reason":reason,"severity":severity,
				"description":description, "address":address_components};

	$.post("markfn/insert",JSON.stringify(data))
		.success(function(msg) {
			//alert(msg);
			$('.mini-layout').fadeTo('slow',1);
			$('.mini-layout').removeClass('disabledDiv');
			$('.markmessage').text('We have successfully stored your mark. Thank you!');
			$('.markmessage').removeClass('text-warning');
			$('.markmessage').addClass('text-success');
			if(infowindow) {
				infowindow.close();
			}		 
		})
		.error(function() { 
			$('.mini-layout').fadeTo('slow',1);
			$('.mini-layout').removeClass('disabledDiv');
			$('.markmessage').text('Error during save. Please try again.');
			$('.markmessage').removeClass('text-warning');
			$('.markmessage').addClass('text-error');
		});
}

function navigateToSubmit(event) {
	var parent = $(event.target).parent().parent().parent().parent();
	$(parent).find(".captchaInput").val('');
	reloadCaptchaDiv(parent);
    $(event.target).parent().parent().parent().parent().children('.current').removeClass('current').hide()
        .next().show().addClass('current');    
}

function navigateToData(event) {
	$(event.target).parent().parent().children('.current').removeClass('current').hide()
     				.prev().show().addClass('current');
    if($(event.target).parent().parent().find(".category").val().trim().length===0){
    	$(event.target).parent().parent().find(".category").addClass("mandatoryfield");
	} else {
		$(event.target).parent().parent().find(".category").removeClass("mandatoryfield");
	}
    if($(event.target).parent().parent().find(".transportation_types").is(":visible")){
	    if($(event.target).parent().parent().find(".transportation_types").val().trim().length===0){
	    	$(event.target).parent().parent().find(".transportation_types").addClass("mandatoryfield");
		} else {
			$(event.target).parent().parent().find(".transportation_types").removeClass("mandatoryfield");
		}
	}
    if($(event.target).parent().parent().find(".reason_accident").is(":visible")){
	    if($(event.target).parent().parent().find(".reason_accident").val().trim().length===0){
	    	$(event.target).parent().parent().find(".reason_accident").addClass("mandatoryfield");
		} else {
			$(event.target).parent().parent().find(".reason_accident").removeClass("mandatoryfield");
		}
    }
    if($(event.target).parent().parent().find(".reason_calamity").is(":visible")){
	    if($(event.target).parent().parent().find(".reason_calamity").val().trim().length===0){
	    	$(event.target).parent().parent().find(".reason_calamity").addClass("mandatoryfield");
		} else {
			$(event.target).parent().parent().find(".reason_calamity").removeClass("mandatoryfield");
		}
    }
    if($(event.target).parent().parent().find(".severity").val().trim().length===0){
    	$(event.target).parent().parent().find(".severity").addClass("mandatoryfield");
	} else {
		$(event.target).parent().parent().find(".severity").removeClass("mandatoryfield");
	}
}

function validate(event){
	if(validateCaptcha($(event.target).parent().parent())){
		if($(event.target).parent().parent().find(".category").val().trim().length!==0 && 
				($(event.target).parent().parent().find(".transportation_types").val().trim().length!==0  || $(event.target).parent().parent().find(".transportation_types").is(":hidden"))&& 
				($(event.target).parent().parent().find(".reason_accident").val().trim().length!==0 ||  $(event.target).parent().parent().find(".reason_calamity").val().trim().length!==0) 
				&& $(event.target).parent().parent().find(".severity").val().trim().length!==0){
			$('.mini-layout').fadeTo('slow',.3);
			$('.mini-layout').addClass("disabledDiv");
			$('.markmessage').removeClass('text-error');
			$('.markmessage').removeClass('text-success');
			$('.markmessage').addClass('text-warning');
			$('.markmessage').text('Saving your data...');
			return true;
		} else {
			navigateToData(event);			
		}
	}
	return false;
}
function selectNatureElements(event) {
	var selectedValue = $(".category option:selected").val();
	
	if(selectedValue==="Accident"){
		$(".reason_calamity").hide();
		$(".reason_accident").show();
		$(".transportation_na").hide();
		$(".transportation_types").show();
	} else if(selectedValue==="Natural Disaster"){
		$(".reason_accident").hide();
		$(".reason_calamity").show();
		$(".reason_calamity").removeClass("hidden");
		$(".transportation_types").hide();
		$(".transportation_na").show();
		$(".transportation_na").removeClass("hidden");
	}
}