var chart;
// $('div#container.chartCls').
function getEmotion(url) {
	alert(url);
	$.getJSON(url, function(json) {
		console.log("second success");
		console.log("JSON Data: " + json.activities);
		console.log("JSON Data: " + json.heats);
		console.log("JSON Data: " + json.weights);
		chart.series[0].setData(json.activities);
		chart.series[1].setData(json.weights);
		chart.series[2].setData(json.heats);
	});
}

$(function() {
	chart = new Highcharts.Chart({
		chart : {
			renderTo : 'container-emotion', // 在哪个区域呈现，对应HTML中的一个元素ID
			plotBackgroundColor : null, // 绘图区的背景颜色
			plotBorderWidth : null, // 绘图区边框宽度
			plotShadow : false, // 绘图区是否显示阴影
			type : 'area'
		},
		colors : [ "#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee",
				"#ff0066", "#eeaaee", "#55BF3B", "#DF5353", "#7798BF",
				"#aaeeee" ],
		title : {
			text : 'The Emotion Histogram of uploaded image',

			style : {
				color : '#3E576F',
				fontSize : '16px'
			}
		},
		exporting : {
			enabled : false
		},
		xAxis : {
			labels : {
				formatter : function() {
					return this.value / 10;
					// clean, unformatted number
					// for year
				}
			},
			tickmarkPlacement : 'on',
			gridLineColor : "#C0C0C0",
			gridLineWidth : '1',
			title : {
				enabled : false
			}
		},
		yAxis : {
			title : {
				text : 'Percentage'
			},
			labels : {
				formatter : function() {
					return this.value;
				}
			}
		},
		tooltip : {
			enabled : false,
			shared : true,
			valueSuffix : ' millions'
		},
		plotOptions : {
			area : {
				pointStart : 0,
				stacking : 'normal',
				lineColor : '#666666',
				lineWidth : 1,
				marker : {
					enabled : false,
					symbol : 'circle',
					radius : 2,
					states : {
						hover : {
							enabled : true
						}
					}
				}
			}
		},

		series : [ {
			name : 'activity',
			data : [ null, null, null, null, null, null, null ]
		}, {
			name : 'weight',
			data : [ null, null, null, null, null, null, null ]
		}, {
			name : 'heart',
			data : [ null, null, null, null, null, null, null ]
		} ]
	});
});
