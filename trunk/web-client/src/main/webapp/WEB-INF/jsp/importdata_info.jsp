<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<style>

div.info{
	border-style: medium;
	border-color: black;
	solid border-width: 10px;
	padding: 4%;
	margin-left: 10%;
	margin-top: 5%;
	margin-right: 5%;
	margin-bottom: 10%;
	outline: #064861 inset thick;
	font: Blanch;
	color: #4D4D4D;
	background-color: #ffffff;
}

h2 {	
	margin: 5%;
}

p {
	display: block;
	margin-top: 1em;
	margin-bottom: 1em;
	margin-left: 10%;
	margin-right: 10%;
	color: #4D4D4D;
	font-size: 100%;
}

b{
	margin: 5%;
}

li {
	margin-top: 1em;
	margin-bottom: 1em;
	margin-left: 11%;
	margin-right: 10%;
}

u1 {
	margin-top: 1em;
	margin-bottom: 1em;
	margin-left: 12%;
	margin-right: 10%;
	color: #4D4D4D;
}


visualInfoCanvas{
	height: 100%;
	width: 100%;
	background: #4dd2ff;

}

table.InfoVisual{
	background: #47AFE2;	
	margin: 1px;
}

h1.info{
	margin-left:5%;
	margin-right:5%;
	margin-background: #99ddff;
}

div.infoBlackGray{
	background-color: #cef3fd;
}
div.yellow {
	background-color: #FBBA00;
}

</style>

</head>
<body class="Info">	


<div class="yellow"><h1 class="info">CityOpt: Info</h1></div>		
	<div>	
	<table class="InfoVisual" style="height: 100%; width: 100%">
	
	<tr><td><td></tr>	
	</table>	
	</div>
	
	<h2>CityOpt info: Import data </h2>
		
	<div class="info">
		<p> In this page you can import data into City opt.
			City opt uses CSV files. In create project window you can create a project. Projects
			represent a city district in energy simulation optimization. You can
			also Organize your organization within projects in User management.</p>
			
		<p> To import files into city opt just press an browse file, then press load file to load your file into CityOpt.</p>
	
		<b>File types that can be imported</b>
		<p><ul>
				<li>Energy model </li>
				<li>Project </li>
				<li>Scenario</li>
				<li>Time series file</li>
				<li>Optimization set</li>
				<li>Optimization problem</li>
			</ul></p>	
		
	</div>

	<table class="InfoVisual" style="height: 100%; width: 100%">
	
	<tr><td><td><tr>
	
	</table>
</body>
</html>