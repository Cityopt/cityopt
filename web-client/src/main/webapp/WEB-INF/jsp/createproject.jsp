<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create project</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form:form method="post" action="createproject.html" modelAttribute="project">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table style="width:900px">
				<tr><td><h2>Create project</h2></td></tr>
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
						<input type="button" id="uploadDiagram" value="Upload diagram" style="width:120px"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width:150px">
							<col style="width:150px">
							<col style="width:600px">
							<tr>
								<td></td>
								<td></td>
								<td align="right"><input type="submit" value="Create a project" style="width:120px"></td>
							</tr>
						</table>
					</td>
					
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>