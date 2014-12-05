<%--@elvariable id="projectForm" type="com.cityopt.controller.ProjectForm"--%>
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
			<form:form method="post" action="editproject.html?action=save" modelAttribute="projectForm">
			<table style="width:900px">
				<tr><td><h2>Edit project</h2></td></tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<td><form:input type="text" path="projectName"/></td>
							</tr>
							<tr>						
								<td>Location:</td>
								<td><form:input type="text" path="location"/></td>
							</tr>
							<tr>						
								<td>Project creator:</td>
								<td><form:input type="text" path="projectCreator"/></td>
							</tr>
							<tr>						
								<td>Date:</td>
								<td><form:input type="text" path="date"/></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description"></form:textarea></td>
							</tr>
							<tr>						
								<td>Energy model:</td>
								<td><form:input type="text" path="energyModel"/></td>
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
								<td align="right"><form:input type="submit" path="" value="Save project" style="width:150px"/></td>
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