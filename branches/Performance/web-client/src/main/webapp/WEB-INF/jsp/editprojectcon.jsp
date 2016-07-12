<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<style>
h2.error {
    color: red;
}
</style>
</head>
<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:500px;width:1000px;overflow:auto">
			<form:form method="post" action="/CityOPT/ConcurrencySample/editproject.html?action=save" modelAttribute="projectForm">
				
			<table style="width:900px">
				<tr><td><h2>Edit project concurrency test</h2></td></tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<form:hidden path="prjid" value="${projectForm.prjid}"/>
								<form:hidden path="version" value="${projectForm.version}"/>
								<td><form:input type="text" path="name" style="width:250px"/></td>
							</tr>
							<tr>						
								<td>Location:</td>
								<td><form:input type="text" path="location" style="width:250px"/></td>
							</tr>
							<tr>						
								<td>Design target:</td>
								<td><form:input type="text" path="designtarget" style="width:250px"/></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width:250px"></form:textarea></td>
							</tr>
							<tr>						
								<td>Energy model:</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<td>
									Parameter level:
								</td>
								<td>
							 		<select name="parameterLevel">
									  	<option value="1">1</option>
									  	<option value="2">2</option>
									  	<option value="3">3</option>
									  	<option value="4">4</option>
									</select>
								</td> 
							</tr>
							<tr>						
								<td></td>
								<td><a href="uploaddiagram.html"><button type="button">Upload</button></a>
								<!--<form:input type="submit" path="" value="Upload" style="width:120px"/>--></td>
							</tr>
						</table>
					</td>
					<td align="right">
						<img src="assets/img/test_map.jpg"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="Right"><input type="button" id="upload" value="Upload" style="width:120px"/></td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<col style="width:600px">
							<tr>
								<td>Input parameters</td>
								<td><a href="projectparameters.html"><button type="button" style="width:120px">Set</button></a></td>
								<td align="right"><a href="closeproject.html"><button type="button" style="width:120px">Close project</button></a></td>
							</tr>
							<tr>
								<td>Output variables</td>
								<td><a href="outputvariables.html"><button type="button" style="width:120px">Set</button></a></td>
								<td align="right"><form:input type="submit" path="" value="Save project" style="width:120px"/></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</form:form>
			<h2 class="error">${errorMessage}</h2>
			</div>
		</td>
	</tr>
</table>


</body>
</html>