$(function() {'use strict';
	var url = "/action/upload";
	$.getJSON(url, function(jsons) {
		$.each(jsons, function(index, imgObj) {
			console.log(imgObj.thumbnailUrl);
			$("<li class='thumbnail image-list'> <img src= " + imgObj.thumbnailUrl + "/> </li>").appendTo('#image-gallery');
		});
	});
	// Call the dropdowns via JavaScript:
	$('.dropdown-toggle').dropdown();
	$('.dropMenuList').click(function() {
		// console.log($(this).text() + '<span class="caret"></span>');
		$('#dropButton').html($(this).text() + '<span class="caret"></span>');
	});
	// Initialize the jQuery File Upload widget:
	$('#fileupload').fileupload({
		// Uncomment the following to send cross-domain cookies:
		// xhrFields: {withCredentials: true},
		maxNumberOfFiles : 1,
		url : '/action/upload',
		dataType : 'json',
		add : function(e, data) {
			if (data.files && data.files[0]) {
				var reader = new FileReader();
				reader.onload = function(e) {
					$('#previewImg').attr('src', e.target.result);
				};
				reader.readAsDataURL(data.files[0]);
				data.submit();
			}
		},
		done : function(e, data) {
			console.log('Upload finished.');
		},
		fail : function(e, data) {
			console.log("Upload failed.");
		},
		progressall : function(e, data) {
			var progress = parseInt(data.loaded / data.total * 100, 10);
			$('#progress .bar').css('width', progress + '%');
		}
	});
	// Enable iframe cross-domain access via redirect option:
	$('#fileupload').fileupload('option', 'redirect', window.location.href.replace(/\/[^\/]*$/, '/cors/result.html?%s'));

	$('.image-list').click(function(e, data) {
		console.log($(this).text());
		console.log($('img', this).attr('alt'));
		$(this).clone().insertAfter($(this));
	});
});
