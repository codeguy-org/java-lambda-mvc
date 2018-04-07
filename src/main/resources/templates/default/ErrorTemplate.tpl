<html>
	<head>
		<title>Error - ${errorName}</title>
		<style>
			.center-div
			{
			     position: absolute;
			     margin: auto;
			     top: 0;
			     right: 0;
			     bottom: 0;
			     left: 0;
			     width: 100px;
			     height: 100px;
			}
			
			body {
				background-color: #222222;
				color: #cccccc;
				font-size: .9em;
				overflow: hidden;
			}
			
			.bold {
				font-size: 1.5em;
				color: white;
			}
			
			.weak {
				font-size: 1.2em;
				color: #999999;
			}
		</style>
	</head>
	<body style="width: 100%; height: 100%;">
		<div class="center-div" style="text-align: center; width: 100%; line-height: 1.5;">
			<div style="width: 100%; text-align: center; line-height: 2;">
				<span class="bold">${errorName}</span> <span class="weak">Error ${errorNumber}</span><br />
			</div>
			${errorMessage}<br />
			Our service team is working on bringing this page back online.
		</div>
	</body>
</html>