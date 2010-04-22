$(document).ready(function() {

	var input = $('#send input[type=text]');
	input.focus();

	// enable comet
	dwr.engine.setActiveReverseAjax(true);

	// ajax sending
	$('#send').submit(function() {
		var text = jQuery.trim(input.val());
		if (text != '') {
			chatService.addMessage(text);
			input.val('');
		}

		return false;
	});

});
