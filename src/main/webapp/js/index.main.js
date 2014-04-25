/*
 * jQuery File Upload Plugin JS Example 8.9.0
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*jslint nomen: true, regexp: true */
/*global $, window, blueimp */
$(function() {
	
	// Url Mapping Information
	baseUrl = "../action";
	urls = {
		"upload" : [ baseUrl + "/upload", 'POST', 'json' ],
		"uploadfile":[ baseUrl + "/uploadfile", 'POST', 'json' ],
		"downloadfile":[ baseUrl + "/downloadfile/%d?attachment=%s", 'GET', 'json' ],
		"thumbnails_list" : [ baseUrl + '/list?pageNum=%d', 'GET', 'json' ],
		"convert" : [ baseUrl + "/convert?id1=%d&id2=%d&option=%d", 'GET',
				'json' ],
		"file" : [ baseUrl + "/download/%s?%s", 'GET',
				'application/octet-stream' ]
	};
	
	sizes={
			"small":0,
			"medium":1,
			"big":2
	};
	
	
	'use strict';
	// Initialize the jQuery File Upload widget:
	$('#fileupload').fileupload({
		// Uncomment the following to send cross-domain cookies:
		// xhrFields: {withCredentials: true},
		url : urls.upload[0],
		autoUpload:true
	});

	// Enable iframe cross-domain access via redirect option:
	$('#fileupload').fileupload('option', 'redirect',
			window.location.href.replace(/\/[^\/]*$/, '/cors/result.html?%s'));

	if (window.location.hostname === 'blueimp.github.io') {
		// Demo settings:
		$('#fileupload').fileupload(
				'option',
				{
					url : '//jquery-file-upload.appspot.com/',
					// Enable image resizing, except for Android and Opera,
					// which actually support image resizing, but fail to
					// send Blob objects via XHR requests:
					disableImageResize : /Android(?!.*Chrome)|Opera/
							.test(window.navigator.userAgent),
					maxFileSize : 5000000,
					acceptFileTypes : /(\.|\/)(gif|jpe?g|png)$/i
				});
		// Upload server status check for browsers with CORS support:
		if ($.support.cors) {
			$.ajax({
				url : '//jquery-file-upload.appspot.com/',
				type : 'HEAD'
			}).fail(
					function() {
						$('<div class="alert alert-danger"/>').text(
								'Upload server currently unavailable - '
										+ new Date()).appendTo('#fileupload');
					});
		}
	} else {
		// Load existing files:
		$('#fileupload').addClass('fileupload-processing');
		$.ajax({
			// Uncomment the following to send cross-domain cookies:
			// xhrFields: {withCredentials: true},
			url : $('#fileupload').fileupload('option', 'url'),
			dataType : 'json',
			context : $('#fileupload')[0]
		}).always(function() {
			$(this).removeClass('fileupload-processing');
		}).done(function(result) {
			$(this).fileupload('option', 'done').call(this, $.Event('done'), {
				result : result
			});
		});
	}

});
