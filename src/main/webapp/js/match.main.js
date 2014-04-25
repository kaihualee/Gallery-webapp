$(function() {

	// Debug JavaScript
	//lkh_appendImageTo("../tmp/Desert.jpg", "sourceDiv", true);

	messages = {
		// Error
		"upload_require" : [ 'bottom-right', 'bangTidy', '请先上传图片.' ],
		"service_error" : [ 'bottom-right', 'bangTidy', '请求服务器出错.' ],
		"loading_img_error" : [ 'bottom-right', 'bangTidy', '加载图片失败.' ],
		"upload_error" : [ 'bottom-right', 'bangTidy', '上传失败.' ],
		// Info
		"upload_success" : [ 'bottom-right', 'bangTidy', '上传完毕.' ],
		"no_more" : [ 'bottom-right', 'bangTidy', '没有可以加载.' ],
		"convert_sucess" : [ 'bottom-right', 'bangTidy', '图片转换成功.' ],
		"processing" : [ 'bottom-right', 'bangTidy', '正在处理中，请稍等...' ],
	};

	// Url Mapping Information
	baseUrl = "../action";
	urls = {
		"upload" : [ baseUrl + "/upload", 'POST', 'json' ],
		"uploadfile" : [ baseUrl + "/uploadfile", 'POST', 'json' ],
		"downloadfile" : [ baseUrl + "/downloadfile/%d?attachment=%s", 'GET',
				'json' ],
		"download" : [ baseUrl + "/download/%s?attachment=%s&%s", 'GET', 'json' ],
		"thumbnails_list" : [ baseUrl + '/list?pageNum=%d', 'GET', 'json' ],
		"thumbnail":[ baseUrl + '/thumbnail/%d?size=%d', 'GET', 'json' ],
		"convert" : [ baseUrl + "/convert?id1=%d&id2=%d&option=%d&size=%d", 'GET',
				'json' ],
	};
	
	sizes={
			"small":0,
			"medium":1,
			"big":2
	};

	// Initialize thumbnails sidebar via jQUery
	$('#loading-thumbnails-btn').on('click',
			getThumbnails('loading-thumbnails-btn', urls.thumbnails_list))
			.click();

 	$('.selectpicker').selectpicker();

	// Initialize the jQuery File Upload widget:
	$('#fileupload')
			.fileupload(
					{
						// Uncomment the following to send cross-domain cookies:
						// xhrFields: {withCredentials: true},
						maxNumberOfFiles : 1,
						url : urls.upload[0],
						dataType : urls.upload[2],
						add : function(e, data) {
							if (data.files && data.files[0]) {
								prepareLoading("sourceDiv", true);
								data.submit();
							}
						},
						done : function(e, data) {
							var url = $.sprintf(urls.thumbnail[0],
									data.result.files[0].id, sizes.medium);
							lkh_appendImageTo(url, "sourceDiv");
							$('#sourceDiv').attr('data_id',
									data.result.files[0].id);
							var message = messages.upload_success;
							$('.' + message[0]).notify({
								message : {
									text : message[2]
								},
								type : message[1],
								fadeOut : {
									delay : 2500
								}
							}).show();
							console.log('Upload finished.');
						},
						fail : function(e, data) {
							var message = messages.upload_error;
							$('.' + message[0]).notify({
								message : {
									text : message[2]
								},
								type : message[1],
								fadeOut : {
									delay : 2500
								}
							}).show();
							console.log("Upload failed.");
						},
						progressall : function(e, data) {
							var progress = parseInt(data.loaded / data.total
									* 100, 10);
							$('#progress #img-progress-bar').css('width',
									progress + '%').text(progress);
						}
					});
});
// attaching a Loading icon
function prepareLoading(containerId, empty) {
	var container = "#" + containerId;

	// empty the child element of container
	if (empty == true) {
		$(container).empty();
	}

	// add loading icon background
	if ($(container).hasClass('loading') != true) {
		$(container).addClass('loading');
	}
}

function lkh_appendImageTo(url, containerId) {
	var container = "#" + containerId;

	// loading image with jquery effect
	var img = new Image();
	$(img).load(function() {
		// 图片默认隐藏
		$(this).hide();
		// 移除小动画
		$(container).removeClass("loading").append(this);
		// 使用fadeIn特效
		$(this).fadeIn("slow");
	}).error(function() {
		var message = messages.loading_img_error;
		$('.' + message[0]).notify({
			message : {
				text : message[2]
			},
			type : message[1],
			fadeOut : {
				delay : 2500
			}
		}).show();
		console.log(url + " loading image failed");
		// 加载失败时的处理
	}).attr("src", url).addClass('thumbnail');
	return this;
}

function getThumbnails(btnId, url) {
	var pageNum = 1;
	var btn = $('#' + btnId);
	var tmpl_func = tmpl("template-thumbnail");
	var func = function() {
		btn.button('loading');
		console.log(url);
		$.ajax({
			url : $.sprintf(url[0], pageNum),
			type : url[1],
			dataType : url[2],
		}).always(function() {
			console.log(url + ' complete');
			btn.button('reset');
		}).done(function(data) {
			console.log(url + ' success');
			console.log(data);
			if (data.length == 0) {
				$(btn).addClass("disabled");
				var message = messages.no_more;
				$('.' + message[0]).notify({
					message : {
						text : message[2]
					},
					type : message[1],
					fadeOut : {
						delay : 2500
					}
				}).show();
			} else {
				pageNum++;
				$.each(data, function(index, img) {
					img.data_url = $.sprintf(urls.thumbnail[0],img.id, sizes.small);
					var element = tmpl_func(img);
					// $('#image-thumbnails').append(element);
					$(element).appendTo($('#img-thumbnails', $(btn).parent()));
					$(element).fadeIn("slow");
					console.log(tmpl_func(img));
				});
				// customer click event handler
				$('.thumbnail-img').on("click", matchImage);

			}

		}).fail(function(jqXHR, textStatus, errorThrown) {
			var message = messages.service_error;
			$('.' + message[0]).notify({
				message : {
					text : message[2]
				},
				type : message[1],
				fadeOut : {
					delay : 2500
				}
			}).show();
			console.log(url + " fail: " + textStatus);
		});
	};
	return func;
};
function matchImage(event) {
	event.stopPropagation();
	var sid = $('#sourceDiv').attr('data_id');
	var tid = $(this).attr('id');
	var option = $('#dropButton').attr('option');
	if (sid == -1) {
		var message = messages.upload_require;
		$('.' + message[0]).notify({
			message : {
				text : message[2]
			},
			type : message[1],
			fadeOut : {
				delay : 2500
			}
		}).show();
	} else {
		var message = messages.processing;
		$('.' + message[0]).notify({
			message : {
				text : message[2]
			},
			type : message[1],
			fadeOut : {
				delay : 2500
			}
		}).show();
		console.log(urls.convert);
		prepareLoading('resultDiv', true);
		$.ajax({
			url : $.sprintf(urls.convert[0], sid, tid, 1,sizes.medium),
			type : urls.convert[1],
			dataType : urls.convert[2]
		}).always(function() {
			console.log(urls.convert + ' complete');
		}).fail(function() {
			var message = messages.service_error;
			$('.' + message[0]).notify({
				message : {
					text : message[2]
				},
				type : message[1],
				fadeOut : {
					delay : 2500
				}
			}).show();
			console.log(urls.convert + ' fail');
		}).done(
				function(data) {
					var message = messages.convert_sucess;
					$('.' + message[0]).notify({
						message : {
							text : message[2]
						},
						type : message[1],
						fadeOut : {
							delay : 2500
						}
					}).show();
					console.log(urls.convert + ' success');
					console.log(data);
	lkh_appendImageTo($.sprintf(urls.download[0], data.filename,"false",
							new Date().getTime()), "resultDiv");
				});

	}
}