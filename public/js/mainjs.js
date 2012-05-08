$(document).ready(function() {
	$('#maincont').delay(200).animate({marginTop: '+=700'},800)
					.animate({marginTop: '-=35'},300)
					.animate({marginTop: '+=35'},400);



	if ($("#creditfooter")) {
		$("#creditfooter").remove();
		console.log("done!");
	}
});