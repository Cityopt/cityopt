<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
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
	padding: 2%;
	margin-left: 2%;
	margin-top: 2%;
	margin-right: 2%;
	margin-bottom: 2%;
	outline: #064861 inset;
	font: Blanch;
	color: #4D4D4D;
	background-color: #ffffff;
}

h2 {	
	margin: 3%;
}

p {
	display: block;
	margin-top: 1em;
	margin-bottom: 1em;
	margin-left: 3%;
	margin-right: 3%;
	color: #4D4D4D;
	font-size: 100%;
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

	
	<div class="yellow"><h1 class="info">CityOpt simulation info</h1></div>
	<div>
		<table class="InfoVisual" style="height: 100%; width: 100%">
		
		<tr><td><td></tr>
		</table>
	</div>
	
	<h2>${title}</h2>
		
	<div class="info">
		<pre>${infotext}</pre>
	</div>

	<table class="InfoVisual" style="height: 100%; width: 100%">
	
	<tr><td><td><tr>
	
	</table>
	
</body>
</html>