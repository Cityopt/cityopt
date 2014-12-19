<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%--@elvariable id="component" type="com.cityopt.model.Component"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.model.Component"--%>
<%--@elvariable id="inputParam" type="com.cityopt.model.InputParameter"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project parameters</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
<table>
	<col style="width:40px">
	<col style="width:1000px">	
	<tr>
		<td colspan="2" height="80">
			<h2>Project parameters</h2>
		</td>
	</tr>
	<tr>
		<td>
		</td>
		<td>
			<table>
				<tr>
					<td>
						<b>Components parameters</b>
					</td>
				</tr>
				<tr>
					<td>
						<table width="1000">
							<col style="width:150px">
							<col style="width:50px">
							<col style="width:550px">
							<col style="width:250px">
							<tr>						
								<td>
									<table class="tablestyle">
										<col style="width:80px">
										<col style="width:150px">
										<col style="width:80px">
										<col style="width:50px">
										<tr>
											<th>Select</th>
											<th>Components</th>
											<th>Id</th>
											<th>Edit</th>
										</tr>
										
										<c:forEach items="${project.components}" var="component">
										<c:if test="${selectedcompid == component.componentid}">
											<tr style="background-color: rgb(140, 200, 200)">
										</c:if>
										<c:if test="${selectedcompid != component.componentid}">
											<tr>
										</c:if>
											<td><a href="<c:url value='projectparameters.html?selectedcompid=${component.componentid}'/>">Select</a></td>
											<td>${component.name}</td>
									    	<td>${component.componentid}</td>
											<td>
												<a href="<c:url value='editcomponent.html?componentid=${component.componentid}'/>">
													<button align="right" type="button" value="Edit">Edit</button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:120px">
										<col style="width:60px">
										<col style="width:80px">
										<col style="width:60px">
										<tr>
											<th>Input parameter</th>
											<th>Id</th>
											<th>Default value</th>
											<th>Edit</th>
										</tr>
										
										<c:forEach items="${selectedComponent.inputparameters}" var="inputParam">
										<tr>
											<td>${inputParam.name}</td>
									    	<td>${inputParam.inputid}</td>
									    	<td>${inputParam.defaultvalue}</td>
											<td>
												<a href="<c:url value='editinputparameter.html?inputparameterid=${inputParam.inputid}'/>">
													<button align="right" type="button" value="Edit">Edit</button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
										
									</table>
								</td>
								<td>
									<b>Parameters selection</b><br>
									<input type="radio" >All parameters<br>	
									<input type="radio">Completed parameters<br>	
									<input type="radio">Empty parameters<br><br>
									<input type="button" value="Upload default values">	
								</td>
							</tr>
							<tr>
								<td>
									<a href="createcomponent.html"><button type="button">Create component</button></a>
								</td>
								<td></td>
								<td>
									<a href="createinputparameter.html?selectedcompid=${selectedcompid}"><button type="button">Create input parameter</button></a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height="50">
					<td>
					</td>
				</tr>
				<tr>
					<td>
						<b>External parameters</b>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="800">
						
							<tr height="20">
							    <th>Variable</th>
							    <th>Units</th>
							    <th>Upload default profile</th>
							    <th>Date</th>
							    <th>Reliability</th>
							    <th>Visualize</th>
							    <th>Select</th>
							</tr>
							
							<tr height="20">
							    <td>variable 1</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td> <input type="radio"/> </td>
							</tr>
							
							<tr height="20">
							    <td>variable 2</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td> <input type="radio"/> </td>
							</tr>
							
						</table>
						
						<table width="100%">
						
							<tr height="30">
								<td></td>
							</tr>
							<tr>
								<td align="right">
									<a href="editproject.html"><button type="button">Close</button></a>
							    </td>
							</tr>
							      
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>	
</table>
</div>
</body>
</html>