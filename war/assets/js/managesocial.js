$(document).ready(function() {
   
	$(".manage-social input[type='checkbox']").click(toggleMessage);
	userPreferences();
});

function toggleMessage(){
	
	if ($(".manage-social input[type='checkbox']").is(':checked')) {
		$(".manage-social input[type='text']").prop("disabled",true);
		$(".manage-social input[type='text']").val("");
	} else {
		$(".manage-social input[type='text']").prop("disabled",false);
	}
}

function userPreferences(){
	$.getJSON('user/preferences', function(data) {
		$.each(data, function(index, item) {
			if(item.type==='socialconnected') {
				if(item.value==='0') {
					$(".manage-social input[type='checkbox']").attr('checked', false);
				} else {
					$(".manage-social input[type='checkbox']").attr('checked', true);
					$(".manage-social input[type='text']").prop("disabled",true);
					$(".manage-social input[type='text']").val("");
				}
			}
		});		
	});
}