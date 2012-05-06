$(document).ready(function() {
	$('#maincont').delay(200).animate({marginTop: '+=700'},800)
					.animate({marginTop: '-=35'},300)
					.animate({marginTop: '+=25'},400)
					.animate({marginTop: '+=10'},200);

	console.log("start!");
	if ($("#creditfooter")) {
		$("#creditfooter").remove();
		console.log("done!");
	}
});