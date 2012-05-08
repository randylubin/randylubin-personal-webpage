$(document).ready(function() {
	$('#maincont').delay(200).animate({marginTop: '+=700'},800)
					.animate({marginTop: '-=45'},350)
					.animate({marginTop: '+=45'},450);



	if ($("#creditfooter")) {
		$("#creditfooter").remove();
		console.log("done!");
	}
});