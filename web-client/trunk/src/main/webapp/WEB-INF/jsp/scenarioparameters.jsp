<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParamVal" type="com.cityopt.DTO.InputParamValDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="componentInputParamVal" type="com.cityopt.DTO.ComponentInputParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Scenario parameters</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td>
			<div style="overflow:scroll;height:600px;width:800px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td></td>
					<td height="80">
						<h2>Scenario parameters</h2>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:50px">
							<col style="width:300px">	
							<tr>
								<td>
									<b>Components</b>
								</td>
								<td></td>
								<td>
									<b>Input parameter values</b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:80px">
										<col style="width:150px">
										<col style="width:80px">
										<tr>
											<th>Select</th>
											<th>Components</th>
											<th>Id</th>
										</tr>
										
										<c:forEach items="${components}" var="component">
										<c:if test="${selectedComponent.componentid == component.componentid}">
											<tr style="background-color: rgb(140, 200, 200)">
										</c:if>
										<c:if test="${selectedComponent.componentid != component.componentid}">
											<tr>
										</c:if>
											<td><a href="<c:url value='scenarioparameters.html?selectedcompid=${component.componentid}'/>">Select</a></td>
											<td>${component.name}</td>
									    	<td>${component.componentid}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:150px">
										<col style="width:60px">
										<col style="width:60px">
										<col style="width:60px">
										<tr>
											<th>Input parameter</th>
											<th>Id</th>
											<th>Value</th>
											<th>Edit</th>
										</tr>
										
										<c:forEach items="${componentInputParamVals}" var="componentInputParamVal">
										<tr>
											<td>${componentInputParamVal.inputparametername}</td>
									    	<td>${componentInputParamVal.inputid}</td>
									    	<td>${componentInputParamVal.value}</td>
											<td>
												<a href="<c:url value='editinputparamvalue.html?inputparamvalid=${componentInputParamVal.inputid}'/>">
													<button align="right" type="button" value="Edit">Edit</button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td align="right">
									<a href="editscenario.html"><button type="button">Close</button></a>
							    </td>
							</tr>
						</table>
					</td>
				</tr>	
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>