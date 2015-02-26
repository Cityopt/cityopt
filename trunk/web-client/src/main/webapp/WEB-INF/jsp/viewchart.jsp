<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="usersession" type="com.cityopt.web.UserSession"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt view chart</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td>
			<div style="overflow:scroll;height:600px;width:1130px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:110px">	
				<tr>
					<td colspan="2" height="80">
						<h2>View chart</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<table width="1100">
										<col style="width:350px">
										<col style="width:25px">
										<col style="width:350px">
										<col style="width:25px">
										<col style="width:350px">
										<tr>
											<td>
												<b>Components</b>
											</td>
											<td></td>
											<td>
												<b>Output parameters</b>
											</td>
											<td></td>
											<td>
												<b>External parameters</b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:100px">
													<col style="width:200px">
													<col style="width:50px">
													<tr>
														<th>Select</th>
														<th>Component</th>
														<th>Id</th>
													</tr>
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: rgb(140, 200, 200)">
														<td><a href="viewchart.html?selectedcompid=${component.componentid}">Selected</a></td>
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
														<td><a href="viewchart.html?selectedcompid=${component.componentid}">Select</a></td>
													</c:if>
														<td>${component.name}</td>
												    	<td>${component.componentid}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:100px">
													<col style="width:150px">
													<col style="width:50px">
													<col style="width:50px">
													<tr>
														<th>Draw</th>
														<th>Output variable</th>
														<th>Id</th>
														<th>Type</th>
													</tr>
													<c:forEach items="${outputVars}" var="outputVar">
														<c:choose>
															<c:when test="${usersession.hasOutputVar(outputVar.outvarid)}">
																<tr style="background-color: rgb(140, 200, 200)">
																<td>Added (<a href="viewchart.html?action=remove&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Remove</a>)</td>
															</c:when>
															<c:otherwise>
																<tr>
																<td><a href="viewchart.html?action=add&outputvarid=${outputVar.outvarid}&selectedcompid=${selectedcompid}">Add to chart</a></td>
															</c:otherwise>
														</c:choose>
													
														<td>${outputVar.name}</td>
												    	<td>${outputVar.outvarid}</td>
												    	<td>${outputVar.typeid}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle" width="350">
													<col style="width:100px">
													<col style="width:150px">
													<col style="width:50px">
													<col style="width:50px">
													<col style="width:50px">
															
													<tr height="20">
													    <th>Draw</th>
													    <th>Name</th>
													    <th>Id</th>
														<th>Value</th>
													    <th>Default value</th>
													</tr>
													
													<c:forEach items="${extParamVals}" var="extParamVal">
													<tr>
														<c:choose>
															<c:when test="${usersession.hasExtParam(extParamVal.extparamvalid)}">
																<tr style="background-color: rgb(140, 200, 200)">
																<td>Added (<a href="viewchart.html?action=remove&extparamid=${extParamVal.extparamvalid}">Remove</a>)</td>
															</c:when>
															<c:otherwise>
																<tr>
																<td><a href="viewchart.html?action=add&extparamid=${extParamVal.extparamvalid}">Add to chart</a></td>
															</c:otherwise>
														</c:choose>
														
														<td>${extParamVal.extparam.name}</td>
												    	<td>${extParamVal.extparamvalid}</td>
												    	<td>${extParamVal.value}</td>
												    	<td>${extParamVal.extparam.defaultvalue}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
										<tr height="20"><td></td></tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											<td>
												<table width="100%">
													<tr>
														<td><b>Chart type</b></td>
												 		<td align="right">
													 		<select style="width: 150px" name="charttype">
															  	<option value="1">Time series</option>
															  	<option value="2">Bar chart</option>
															  	<option value="3">Pie chart</option>
															  	<option value="4">Scatter plot</option>
															</select><br>
														</td>
													</tr>
													<tr height="20"></tr>
													<tr>
														<td></td>
														<td align="right">
															<a href="drawchart.html"><button type="button">Draw chart</button></a>
														</td>
													</tr>
												</table>
											</td>
										</tr>										
									</table>
								</td>
							</tr>
							<tr height="50">
								<td>
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