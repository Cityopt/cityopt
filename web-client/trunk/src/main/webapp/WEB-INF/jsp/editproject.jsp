<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form>
<table style="width:900px">
	<tr><td><h2>Project definition</h2></td></tr>
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
		<td align="right">
			<img src="assets/img/test_map.jpg"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="Right">
			<input type="button" value="Upload diagram">
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table>
				<col style="width:150px">
				<col style="width:150px">
				<col style="width:600px">
				<tr>
					<td>Input parameters</td>
					<td><input type="button" value="Set" style="width:150px"></td>
					<td></td>
				</tr>
				<tr>
					<td>Output variables</td>
					<td><input type="button" value="Set" style="width:150px"></td>
					<td></td>
				</tr>
				<tr>
					<td>GIS Coordinates</td>
					<td><input type="button" value="Set" style="width:150px"></td>
					<td align="right"><input type="button" value="Save project" style="width:150px"></td>
				</tr>
			</table>
		</td>
		
	</tr>
</table>
</form>
</body>
</html>