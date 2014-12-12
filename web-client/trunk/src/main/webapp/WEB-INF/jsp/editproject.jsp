<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td>
			<div style="overflow:scroll;height:500px;width:1000px;overflow:auto">
			<form:form method="post" action="editproject.html?action=save" modelAttribute="project">
			<table style="width:900px">
				<tr><td><h2>Edit project</h2></td></tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<td><form:input type="text" path="name"/></td>
							</tr>
							<tr>						
								<td>Location:</td>
								<td><form:input type="text" path="location"/></td>
							</tr>
							<tr>						
								<td>Design target:</td>
								<td><form:input type="text" path="designtarget"/></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description"></form:textarea></td>
							</tr>
							<tr>						
								<td>Energy model:</td>
								<td></td>
							</tr>
							<tr>						
								<td></td>
								<td><input type="button" id="upload" value="Upload"/></td>
							</tr>
						</table>
					</td>
					<td align="right">
						<img src="assets/img/test_map.jpg"/>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="Right">
						<input type="button" id="uploadDiagram" value="Upload diagram"/>
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
								<td><a href="projectparameters.html"><button type="button" style="width:150px">Set</button></a></td>
								<td></td>
							</tr>
							<tr>
								<td>Output variables</td>
								<td><a href="outputvariables.html"><button type="button" style="width:150px">Set</button></a></td>
								<td align="right"><a href="closeproject.html"><button type="button" style="width:120px">Close project</button></a></td>
							</tr>
							<tr>
								<td>GIS Coordinates</td>
								<td><a href="coordinates.html"><button type="button" style="width:150px">Set</button></a></td>
								<td align="right"><form:input type="submit" path="" value="Save project" style="width:120px"/></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</form:form>
			</div>
		</td>
	</tr>
</table>
</body>
</html>