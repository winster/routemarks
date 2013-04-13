$(function(){
	getCurrentLocationfromGoogle(function(geo_address){
		requestServerForToken(geo_address.address_components);
		var autocomplete = new google.maps.places.Autocomplete($("#location")[0], {});

        google.maps.event.addListener(autocomplete, 'place_changed', function() {
            var place = autocomplete.getPlace();
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
	});
});

$(document).ready(function() {
	   
	//loadUpdates(getDummyData());

	
});

function getDummyData() {
	var data = {loadType:'reload',updates:[{markId:"101",starcount:"2",likecount:"3",dislikecount:"6",username:"Winster",countrycode:"IN",locality:"Kelakam",category:"Accident",transportation:"Car",nature:"Traffic rule violation",severity:"critical",description:"It was a mistake from truck driver that took lives of 3."},
	  	               {markId:"102",starcount:"2",likecount:"3",dislikecount:"6",username:"Jose",countrycode:"US",locality:"Columbia",category:"Disaster",transportation:"Public Transport",nature:"Wrong message board",severity:"major",description:"Correct the message board."}]};
	//var message = {data:data};
	return data;
}

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
	    $bar.text($bar.width()/(factor*.1) +10+ "%");
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
function onOpened(geo_address_components, message){
	$(".locationSelected").text(getCountryFromGeo(geo_address_components).long_name);
	loadUpdates(message);
}
function onMessage(msg){
	var json = $.parseJSON(msg.data);
	loadUpdates(json);
}
function onError(){
	
}
function onClose(){
	
}
function loadUpdates(data){
	loadTemplate().done(function(){
		fillUpdates(data);
	});
}
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
		attachListeners(communityUpdate);
		if(data.loadType==='prepend') {
			$('.communityUpdates').prepend(communityUpdate);
		} else {
			$('.communityUpdates').append(communityUpdate);
		}
	});
	attachListeners();
}
var communityRowTemplate;
function loadTemplate(){
    var dfd = $.Deferred();
    $.ajax({
        url : "templates/communityupdate.html",
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

function attachListeners(communityUpdate){
	$(communityUpdate).find(".star").click(starMark);
	$(communityUpdate).find(".like").click(likeMark);
	$(communityUpdate).find(".dislike").click(dislikeMark);
	$(".morerow").show();
	$(".morerow").click(function(){
		$.post("communitytoken/more",function(){
			
		}).success(function(){
			
		});
	});
}

function starMark(){
	var starcount = $(this).find(".starcount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"star","count":starcount, "markId" :markId};
	updateMark(data);
}

function likeMark(){
	var likecount = $(this).find(".likecount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"like","count":likecount, "markId" :markId};
	updateMark(data);
}

function dislikeMark(){
	var dislikecount = $(this).find(".dislikecount").text();
	var markId = $(this).parent().parent().parent().find(".markId").text();
	var data = {"type":"dislike","count":dislikecount, "markId" :markId};
	updateMark(data);	
}

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