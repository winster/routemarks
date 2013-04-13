var SWLAT,SWLNG,NELAT,NELNG;
	

$(document).ready(function() {
	
	SWLAT = gup("SWLAT");
	SWLNG = gup("SWLNG");
	NELAT = gup("NELAT");
	NELNG = gup("NELNG");
	
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
	
});

function getData(table, type) {
    // Builds a Fusion Tables SQL query and hands the result to dataHandler()

    var queryUrlHead = 'http://www.google.com/fusiontables/api/query?sql=';
    var queryUrlTail = '&jsonCallback=?'; // ? could be a function name
    
    // write your SQL as normal, then encode it
    var query = "SELECT TSRId FROM " + table + " WHERE ST_INTERSECTS(Location, RECTANGLE(LATLNG("+SWLAT+", "+SWLNG+"), LATLNG("+NELAT+", "+NELNG+"))) ";
    if(type==="all") {
    	query += "AND LastUpdatedDate >= '01/01/2013'" 
    } else if(type==="today") {
    	query += "AND LastUpdatedDate >= '"+getTodaysDate()+"'" 
    } else if(type==="yesterday") {
    	query += "AND LastUpdatedDate >= '"+getYesterdaysDate()+"'" 
    } else if(type==="lastweek") {
    	query += "AND LastUpdatedDate >= '"+getLastWeekDate()+"'" 
    } else if(type==="lastmonth") {
    	query += "AND LastUpdatedDate >= '"+getLastMonthDate()+"'" 
    } else if(type==="lastyear") {
    	query += "AND LastUpdatedDate >= '"+getLastYearDate()+"'" 
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