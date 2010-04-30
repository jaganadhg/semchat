$(document).ready(function() {

	function errh(msg, exc) {
		alert("Error message is: " + msg + " - Error Details: " + dwr.util.toDescriptiveString(exc, 2));
	}

	dwr.engine.setErrorHandler(errh);



	$('#login-name').focus();

	var input = $('#send-text');
	input.focus();

	// enable comet
	dwr.engine.setActiveReverseAjax(true);

	// ajax sending
//	$('#send').submit(function() {
//		var text = jQuery.trim(input.val());
//		if (text != '') {
//			chatService.addMessage(text);
//			input.val('');
//		}
//		return false;
//	});

	// sidebar discussions links to external window
	$('similar-segments h3 a').attr("target", "_blank");
});


/**
 * Obnovi spravy.
 */
function refreshMessages()
{
	$('#messages').load('messages.html');
}

/**
 * Obnovi informacie o segmentoch.
 */
function refreshSegments()
{
	$('#sidebar').load('segments-info.html');
}
