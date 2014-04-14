$(function() {'use strict';
	var url = "/action/upload";
	$.getJSON(url, function(jsons) {
		$.each(jsons, function(index, imgObj) {
			var thumbnailUrl = imgObj.thumbnailUrl;
			var id = thumbnailUrl.substring(thumbnailUrl.lastIndexOf('/') + 1, thumbnailUrl.length);
			$("<li class='thumbnail '> <img class='image-list' src= " + imgObj.thumbnailUrl + " id=" + id + " /> </li>").appendTo('#image-gallery').on('click', function() {
				var mimage_id = $('img', this).attr('id');
				var simage_id = $('#previewImg').attr('image_id');
				var option = $('#dropButton').attr('option');
				var url = "/action/convert?id1=" + simage_id + "&id2=" + mimage_id;
//				$.ajax({
//					  url: url,
//					  dataType: 'json',
//					  success: function(result){
//					    alert("success token recieved: " + result.token);
//					  },
//					  error: function(request, textStatus, errorThrown) {
//					    alert(textStatus);
//					  },
//					  complete: function(request, textStatus) { //for additional info
//					    alert(request.responseText);
//					    alert(textStatus);
//					  }
//					});
				$.getJSON(url,function(data){
					console.log(data);
					$('#resultImg').attr('src', "/action/download/" + data.filename+"?" + new Date().getTime());
				});
			});
		});
	});
	// Call the dropdowns via JavaScript:
	$('.dropdown-toggle').dropdown();
	$('.dropMenuList').click(function() {
		// console.log($(this).text() + '<span class="caret"></span>');
		$('#dropButton').html($(this).text() + '<span class="caret"></span>');
		$('#dropButton').attr('option', $(this).attr('option'));
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
			var thumbnailUrl = data.result.files[0].thumbnailUrl;
			var id = thumbnailUrl.substring(thumbnailUrl.lastIndexOf('/') + 1, thumbnailUrl.length);
			$('img#previewImg').attr('image_id', id);
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
});
