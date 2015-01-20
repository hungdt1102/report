<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/preinc.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Thống kê sản lượng SMS</title>
<meta name="description" content="">
<!-- start: Mobile Specific -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- end: Mobile Specific -->

<!-- start: CSS -->
<link id="bootstrap-style" href="./css/bootstrap.min.css"
	rel="stylesheet">
<link href="./css/bootstrap-responsive.min.css" rel="stylesheet">
<link id="base-style" href="./css/style.css" rel="stylesheet">
<link id="base-style-responsive" href="./css/style-responsive.css"
	rel="stylesheet">
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800&subset=latin,cyrillic-ext,latin-ext'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="./css/daterangepicker-bs3.css" />

<!-- end: CSS -->
<%@include file="include/js_include.jsp"%>


<script>
     $(document).ready(function(){
     
     	var ajax_image_mini = "<img src='./img/ajax-loader_mini.gif' alt='Loading...' />";
		$('#revenueday .number').html(ajax_image_mini);
		
		$('#revenuelastday .number').html(ajax_image_mini);
		
		$('#revenuemonth .number').html(ajax_image_mini);
		
		$('#revenuelastmonth .number').html(ajax_image_mini);
		
		
         var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
       $.ajax({
			url : contextPath + '/home/getday.htm',
			//data : JSON.stringify(json),
			contentType : 'application/json',
			type : "POST",
			success : function(day) {
                var revenue = day.revenue;
				$('#revenueday .number').html(revenue.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + ' Đ');
                
			}

		}); 
		
		 $.ajax({
			url : contextPath + '/home/getlastday.htm',
			//data : JSON.stringify(json),
			contentType : 'application/json',
			type : "POST",
			success : function(day) {
                var revenue = day.revenue;
				$('#revenuelastday .number').html(revenue.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + ' Đ');
                
			}

		}); 
		
		 $.ajax({
			url : contextPath + '/home/getlastmonth.htm',
			//data : JSON.stringify(json),
			contentType : 'application/json',
			type : "POST",
			success : function(day) {
                var revenue = day.revenue;
				$('#revenuelastmonth .number').html(revenue.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + ' Đ');
                
			}

		}); 
                
              $.ajax({
			url : contextPath + '/home/getmonth.htm',
			//data : JSON.stringify(json),
			contentType : 'application/json',
			type : "POST",
			success : function(day) {
                            var revenue = day.revenue;
				$('#revenuemonth .number').html(revenue.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + ' Đ');
                
			}

		}); 
    });
</script>


</head>
<body>

	<!-- start: Header -->
	<%@include file="include/navbar.jsp"%>
	<!-- start: Header -->

	<div class="container-fluid-full">
		<div class="row-fluid">
			<!-- start: Main Menu -->

			<%@include file="include/mainbar.jsp"%>



			<!-- start: Main Menu -->
			<!-- start: Content -->

			<div id="content" class="span10">
				<ul class="breadcrumb">
					<li><i class="icon-home"></i> <a href="index.htm">Home</a> <i
						class="icon-angle-right"></i></li>
					<li><a href="#">Trang chủ</a></li>
				</ul>

				<div class="row-fluid">
					<div id="revenueday" class="span3 statbox purple" onTablet="span6"
						onDesktop="span3">
						<div class="boxchart">5,6,7,2,3,4,2,4,8,2,3,3,2</div>
						<div id="value" class="number"></div>
						<div class="title">Hôm nay</div>
						<div id="value" class="mo"></div>
					</div>
					<div id="revenuelastday" class="span3 statbox green"
						onTablet="span6" onDesktop="span3">
						<div class="boxchart">5,6,7,2,3,4,2,4,8,2,3,3,2</div>
						<div id="value" class="number"></div>
						<div class="title">Hôm qua</div>
						<div id="value" class="mo"></div>
					</div>
					<div id="revenuemonth" class="span3 statbox blue noMargin"
						onTablet="span6" onDesktop="span3">
						<div class="boxchart">5,6,7,2,3,4,2,4,8,2,3,3,2</div>
						<div id="value" class="number"></div>
						<div class="title">Tháng này</div>
						<div id="value" class="mo"></div>

					</div>
					<div id="revenuelastmonth" class="span3 statbox yellow"
						onTablet="span6" onDesktop="span3">
						<div class="boxchart">5,6,7,2,3,4,2,4,8,2,3,3,2</div>
						<div id="value" class="number"></div>
						<div class="title">Tháng trước</div>
						<div id="value" class="mo"></div>
					</div>
				</div>

				<div class="row-fluid">
					<div class="span8 widget" onTablet="span7" onDesktop="span8">
						<div class="btn-groups">
							<div id="reportrange" class="pull-right"
								style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc">
								<i class="glyphicon glyphicon-calendar fa fa-calendar"></i> <span></span>
								<b class="caret"></b>
							</div>
						</div>

						<script>
                                         function formatCurrency(num) {
                                        var p = num.toFixed(2).split(".");
                                            return p[0].split("").reverse().reduce(function(acc, num, i, orig) {
                                        return num + (i && !(i % 3) ? "," : "") + acc;
                                        }, "") +"." + p[1];
                                        }

                                                                            
	function createChart(revenue) {

		var val1 = [];
		var val2 = [];
		var val3 = [];
		
		console.log(revenue.categories);
		
		$.each(revenue.series, function(i, seriesItem)
		{
			
			
			  $.each(seriesItem.data, function(j, seriesItemData) {
            
                  	if(seriesItem.name == 'MO')
                  	{
                  		val2.push(parseInt(seriesItemData));
                  	}
                  	else if (seriesItem.name == 'DT')
                  	{
                  		val1.push(parseInt(seriesItemData));
                  	}
                  	else if (seriesItem.name == 'DTCK')
                  	{
                  		val3.push(parseInt(seriesItemData));
                  	}
                });
		});
		
		
		 $('#container').highcharts({
        chart: {
            zoomType: 'xy'
        },
        title: {
            text: 'THỐNG KÊ DOANH THU'
        },
        subtitle: {
            text: ''
        },
        xAxis: [{
            categories: revenue.categories
        }],
        yAxis: [{ // Primary yAxis
            labels: {
                format: ''
            },
            title: {
                text: 'MO',
                align: 'high',
                offset: 0,
                rotation: 0,
                y: 325,
                x: 30
                
            },
            opposite: true,
            min: 0
        }, { // Secondary yAxis
            gridLineWidth: 0,
            title: {
                text: 'Doanh thu',
                align: 'high',
                offset: 0,
                rotation: 0,
                y: 325,
                x: 0
            },
            
            min: 0

        }],
        tooltip: {
                        crosshairs: true,
                        formatter: function() {
                            var p = '<span style="font-size: 10px">Day / Month : ' + this.x + '</span><br/>';
                            $.each(this.points, function(i, series) {
                                p += '<span style="color:' + this.series.color + '">' + this.series.name + '</span>: <b>' + formatCurrency(this.y) + '</b><br/>';
                            });
                            return p;
                        },
                        shared: true
                    },
        
        series: [{
            name: 'Doanh thu',
            type: 'column',
            yAxis: 1,
            data: val1 

        },{
            name: 'Doanh thu kỳ trước',
            type: 'column',
            color: '#FF9933',
            yAxis: 1,
            data: val3 

        }, {
            name: 'MO',
            type: 'spline',
            color: '#339900',
            data: val2
        }]
    });
	}

	function getChart(start, end) {

		var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
        var ajax_image = "<center><img src='./img/ajax-loader.gif' alt='Loading...' /></center>";        
		$('#container').html(ajax_image);
		var json = {
			"fromDate" : start,
			"toDate" : end,
			"merchantId" : ${merchantId}
		};
		
		$.ajax({
			url : contextPath + '/home/loadchart.htm',
			data : JSON.stringify(json),
			contentType : 'application/json',
			type : "POST",
			success : function(revenue) {
				createChart(revenue);
			}

		});
	}
</script>

						<script type="text/javascript">
	$('#reportrange span').html(
			moment().subtract(6, 'days').format('DD/MM/YYYY') + ' - '
					+ moment().format('DD/MM/YYYY'));
	var start = moment().subtract(6, 'days').format('DD/MM/YYYY');

	var end = moment().format('DD/MM/YYYY');

	getChart(start, end);

	$('#reportrange')
			.daterangepicker(
					{
						ranges : {
							'Hôm nay' : [ moment(), moment() ],
							'Hôm qua' : [ moment().subtract('days', 1),
									moment().subtract('days', 1) ],
							'7 ngày trước' : [ moment().subtract('days', 6),
									moment() ],
							'30 ngày trước' : [ moment().subtract('days', 29),
									moment() ],
							'Tháng này' : [ moment().startOf('month'),
									moment().endOf('month') ],
							'Tháng trước' : [
									moment().subtract('month', 1).startOf(
											'month'),
									moment().subtract('month', 1)
											.endOf('month') ]
						},
						startDate : moment().subtract('days', 7),
						endDate : moment(),
						locale : {
							applyLabel : 'Xem',
							cancelLabel : 'Hủy',
							fromLabel : 'Từ ngày',
							toLabel : 'Tới ngày',
							customRangeLabel : 'Tùy chỉnh',
							daysOfWeek : [ 'CN', 'T2', 'T3', 'T4', 'T5', 'T6',
									'T7' ],
							monthNames : [ 'Tháng 1', 'Tháng 2', 'Tháng 3',
									'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7',
									'Tháng 8', 'Tháng 9', 'Tháng 10',
									'Tháng 11', 'Tháng 12' ],
							firstDay : 1
						}
					},
					function(start, end) {
						$('#reportrange span').html(
								start.format('DD/MM/YYYY') + ' - '
										+ end.format('DD/MM/YYYY'));
						getChart(start.format('DD/MM/YYYY'), end.format('DD/MM/YYYY'));

					});
</script>

						<div id="container"
							style="min-width: 300px; height: 400px; margin: 0 auto"></div>

					</div>


					<!-- End .sparkStats -->
				</div>
				<!-- start: Content -->
				<c:if test="${merchantId ==0 }">
					<div class="row-fluid">
						<script>
					
					 function formatCurrency(num) {
                                        var p = num.toFixed(2).split(".");
                                            return p[0].split("").reverse().reduce(function(acc, num, i, orig) {
                                        return num + (i && !(i % 3) ? "," : "") + acc;
                                        }, "") +"." + p[1];
                                        }
						
						function createHourChart(revenue) {
							
							var val1 = [];
							var val2 = [];
							var val3 = [];
							var val4 = [];
							
							console.log(revenue.categories);
							
							$.each(revenue.series, function(i, seriesItem)
							{
								
								  $.each(seriesItem.data, function(j, seriesItemData) {
					            
					                  	if(seriesItem.name == 'DTLD')
					                  	{
					                  		val2.push(parseInt(seriesItemData));
					                  	}
					                  	else if (seriesItem.name == 'DT')
					                  	{
					                  		val1.push(parseInt(seriesItemData));
					                  	}
					                  	else if (seriesItem.name == 'DTLW')
					                  	{
					                  		val3.push(parseInt(seriesItemData));
					                  	}
					                  	else if (seriesItem.name == 'DTLM')
					                  	{
					                  		val4.push(parseInt(seriesItemData))
					                  	}
					                });
							});
						
							$('#container_hour').highcharts({
						        title: {
						            text: 'Thống kê doanh thu',
						            x: -20 //center
						        },
						        subtitle: {
						            text: '',
						            x: -20
						        },
						        xAxis: {
						            categories: revenue.categories
						        },
						        yAxis: {
						            title: {
						                text: 'Doanh thu',
						                 align: 'high',
							                offset: 0,
							                rotation: 0,
							                y: 325,
							                x: 0
						            },
						             min: 0,
						            plotLines: [{
						                value: 0,
						                width: 1,
						                color: '#808080'
						            }]
						        },
						        
						        tooltip: {
						            crosshairs: true,
				                        formatter: function() {
				                            var p = '<span style="font-size: 10px">' + this.x + ':00</span><br/>';
				                            $.each(this.points, function(i, series) {
				                                p += '<span style="color:' + this.series.color + '">' + this.series.name + '</span>: <b>' + formatCurrency(this.y) + '</b><br/>';
				                            });
				                            return p;
				                        },
				                        shared: true
						        },
							        plotOptions: {
							            spline: {
							                lineWidth: 4,
							                states: {
							                    hover: {
							                        lineWidth: 5
							                    }
							                },
							                marker: {
							                    enabled: false
							                },
							                pointInterval: 3600000, // one hour
							                pointStart: Date.UTC(2009, 9, 6, 0, 0, 0)
							            }
							        },
						        
						        series: [{
						            name: 'Hôm nay',
						            color: '#339900',
						            data: val1
						        }, {
						            name: 'Hôm qua',
						            color: '#FF9933',
						            data: val2
						        }, {
						            name: 'Tuần trước',
						            color: '#87CEFF',
						            data: val3
						        }, {
						            name: 'Tháng trước',
						            color: '#8DB6CD',
						            data: val4
						        }]
						    });
						}
					
					$(document).ready(function() {
						var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
				        var ajax_image = "<center><img src='./img/ajax-loader.gif' alt='Loading...' /></center>";        
						$('#container_hour').html(ajax_image);
						var json = {
							"fromDate" : '',
							"toDate" : '',
							"merchantId" : ${merchantId}
						};
		
						$.ajax({
							url : contextPath + '/home/loadcharthour.htm',
							data : JSON.stringify(json),
							contentType : 'application/json',
							type : "POST",
							success : function(revenue) {
								createHourChart(revenue);
							}
				
						});
					});
				     	
						
				    </script>
						<div class="span8 widget" onTablet="span7" onDesktop="span8"
							style="margin-left: 0px;">
							<div id="container_hour"
								style="min-width: 300px; height: 400px; margin: 0 auto"></div>
						</div>

					</div>
				</c:if>

			</div>
			<!--/row-fluid-->
		</div>
		<!--/fluid-row-->


		<div class="clearfix"></div>

		<footer>

		<p>
			<span style="text-align: left; float: left">&copy; 2014 <a
				href="#" alt="VNS Telecom">VNSTelecom</a></span>

		</p>

		</footer>
</body>
</html>