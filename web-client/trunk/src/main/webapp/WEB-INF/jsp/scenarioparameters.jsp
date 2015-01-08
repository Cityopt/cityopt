<%--@elvariable id="project" type="com.cityopt.model.Project"--%>
<%--@elvariable id="component" type="com.cityopt.model.Component"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.model.Component"--%>
<%--@elvariable id="inputParam" type="com.cityopt.model.InputParameter"--%>
<%--@elvariable id="extParam" type="com.cityopt.model.ExtParam"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
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
	<col style="width:100px">
	<col style="width:800px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td>
			<div style="overflow:scroll;height:600px;width:800px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td colspan="2" height="80">
						<h2>Scenario parameters</h2>
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
									<table width="750">
										<col style="width:150px">
										<col style="width:50px">
										<col style="width:400px">
										<col style="width:150px">
										<tr>						
											<td>
												<table class="tablestyle">
													<col style="width:80px">
													<col style="width:150px">
													<col style="width:80px">
													<tr>
														<th>Select</th>
														<th>Components</th>
														<th>Id</th>
													</tr>
													
													<c:forEach items="${project.components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: rgb(140, 200, 200)">
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
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
													<col style="width:100px">
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
									</table>
								</td>
							</tr>
							<tr>
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