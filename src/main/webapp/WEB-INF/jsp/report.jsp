<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<link
	href="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="http://netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.min.css"
	rel="stylesheet">

<link rel="stylesheet" href="./css/daterangepicker-bs3.css" />

<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type="text/javascript"
	src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	
<script src="./js/plugins/moment.js"></script>
<script src="./js/plugins/datepicker/daterangepicker.js"></script>





</head>
<body>
<div class="well" style="overflow: auto">
<div id="reportrange" class="pull-right"
		style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc">
		<i class="glyphicon glyphicon-calendar fa fa-calendar"></i> <span></span>
		<b class="caret"></b>
	</div>
	
	<script type="text/javascript">
	$('#reportrange span').html(
			moment().subtract(29, 'days').format('DD/MM/YYYY') + ' - '
					+ moment().format('DD/MM/YYYY'));
	$('#reportrange')
			.daterangepicker(
					{
						ranges : {
							'Today' : [ moment(), moment() ],
							'Yesterday' : [ moment().subtract('days', 1),
									moment().subtract('days', 1) ],
							'Last 7 Days' : [ moment().subtract('days', 6),
									moment() ],
							'Last 30 Days' : [ moment().subtract('days', 29),
									moment() ],
							'This Month' : [ moment().startOf('month'),
									moment().endOf('month') ],
							'Last Month' : [
									moment().subtract('month', 1).startOf(
											'month'),
									moment().subtract('month', 1)
											.endOf('month') ]
						},
						startDate : moment().subtract('days', 29),
						endDate : moment()
					},
					function(start, end) {
						$('#reportrange span').html(
								start.format('DD/MM/YYYY') + ' - '
										+ end.format('DD/MM/YYYY'));
					});
</script>
</div>
	
</body>
</html>