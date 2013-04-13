$(document).ready(function() {
   
	userDetails();	
	
});

function userDetails(){
	
	$.getJSON('user/card', function(data) {
		$("#username").text(data.userName);
		$("#totalPoints").text(data.totalPoints);
		if(data.recentActivity) {
			$("#recentActivity").text(data.recentActivity.type +" "+data.recentActivity.desc);
		} else {
			$("#recentActivity").text("Not Available");;
		}
		$("#recentCriteria").text(data.recentCriteria);
		$("#totalActivityCount").text(data.totalActivityCount);
		$("#totalMarkCount").text(data.totalMarkCount);
	});
}
