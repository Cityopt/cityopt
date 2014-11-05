<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form>
<table>
	<h2>Project definition</h2>
	<tr>
		<td>
			<table>
				<tr>
					<td>Project name:</td>
					<td><input type="text" id="projectname"></td>
				</tr>
				<tr>						
					<td>Location:</td>
					<td><input type="text" id="location"></td>
				</tr>
				<tr>						
					<td>Project creator:</td>
					<td><input type="text" id="creator"></td>
				</tr>
				<tr>						
					<td>Date:</td>
					<td><input type="text" id="date"></td>
				</tr>
				<tr>						
					<td>Description:</td>
					<td><textarea type="text" rows="3" id="description"></textarea></td>
				</tr>
				<tr>						
					<td>Energy model:</td>
					<td><input type="text" id="energymodel"></td>
				</tr>
				<tr>						
					<td></td>
					<td><input type="button" id="upload" value="Upload"></td>
				</tr>
			</table>
		</td>
		<td>
			<p><img src="/assets/img/test_map.jpg" height="301" width="464"/></p>
		</td>
	</tr>

</table>
</form>
</body>
</html>