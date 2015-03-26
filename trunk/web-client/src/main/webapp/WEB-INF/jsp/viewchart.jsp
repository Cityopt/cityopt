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
		<td width="20"></td>
		<td valign="top">
			<div style="overflow:scroll;height:600px;width:1130px;overflow:auto">
			<table width="1100px">
				<col style="width:300px">
				<col style="width:30px">
				<col style="width:800px">
				<tr>
					<td><h2>View chart</h2></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<b>Scenarios</b>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="300">
										<col style="width:50px">
										<col style="width:200px">	
																															
										<tr height="20">
										    <th>Select</th>
										    <th>Name</th>
										</tr>
														
										<c:forEach items="${scenarios}" var="scenario">
										<tr>
											<c:choose>
												<c:when test="${usersession.hasScenarioId(scenario.scenid)}">
													<tr style="background-color: rgb(140, 200, 200)">
													<td>Added (<a href="viewchart.html?action=remove&scenarioid=${scenario.scenid}">Remove</a>)</td>
												</c:when>
												<c:otherwise>
													<tr>
													<td><a href="viewchart.html?action=add&scenarioid=${scenario.scenid}">Add</a></td>
												</c:otherwise>
											</c:choose>
											<td>${scenario.name}</td>
									   	</tr>
										</c:forEach>				
									</table>
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr>
								<td>
									<b>Components</b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle" style="width:300px">
										<col style="width:60px">
										<col style="width:240px">
										<tr>
											<th>Select</th>
											<th>Component</th>
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
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>		
							<tr height="10">
							</tr>
							<tr>
								<td>
									<b>Output parameters</b>
								</td>
							</tr>
							<tr>						
								<td valign="top">
									<table class="tablestyle" style="width:300px">
										<col style="width:100px">
										<col style="width:200px">
										<tr>
											<th>Draw</th>
											<th>Output variable</th>
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
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr height="20">
								<td>
									<b>External parameters</b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle" style="width:300px">
										<col style="width:100px">
										<col style="width:200px">
												
										<tr height="20">
										    <th>Draw</th>
										    <th>Name</th>
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
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr>
								<td>
									<b>Project metrics</b>
								</td>
							</tr>
							<tr>
								<td>	
									<table class="tablestyle" style="width:300px" border="1">
									<col style="width:100px">
									<col style="width:200px">
										
									<tr height="20">
										<th>Draw</th>
									    <th>Name</th>
									</tr>
									
									<c:forEach items="${metrics}" var="metric">
										<c:choose>
											<c:when test="${usersession.hasMetric(metric.metid)}">
												<tr style="background-color: rgb(140, 200, 200)">
												<td>Added (<a href="viewchart.html?action=remove&metricid=${metric.metid}">Remove</a>)</td>
											</c:when>
											<c:otherwise>
												<tr>
												<td><a href="viewchart.html?action=add&metricid=${metric.metid}">Add to chart</a></td>
											</c:otherwise>
										</c:choose>
										<td>${metric.name}</td>
									   	</tr>
									</c:forEach>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td></td>
					<td valign="top">
						<table>
							<tr>
								<td valign="top">
									<img src="chart.png">
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td align="right">
												<table>
													<tr>
														<td>	
											 				<b>Select chart type</b>
										 				</td>
									 				</tr>
									 				<tr>	
														<td>
									 					<c:choose>
															<c:when test="${userSession.getChartType() == 0}">
										 						<b><a href="viewchart.html?charttype=0">Time series</a></b>
															</c:when>
															<c:otherwise>
																<a href="viewchart.html?charttype=0">Time series</a>
															</c:otherwise>
														</c:choose>
								 						</td>
							 						</tr>
							 						<tr>
							 							<td>
												 			<a href="viewchart.html?charttype=1">Scatter plot</a>
											 			</td>
									 				</tr>
									 				<tr>
									 					<td>
												 			<a href="viewchart.html?charttype=2">Bar chart</a>
											 			</td>
										 			</tr>
										 			<tr>
														<td>
												 			<a href="viewchart.html?charttype=3">Pie chart</a>
											 			</td>
										 			</tr>
									 			</table>
											</td>
											<td align="right">
												<table>
													<tr>
														<td>
															<a href="viewchart.html?action=removeall"><button type="button" style="width: 150px">Remove selections</button></a>
														</td>
													</tr>
													<tr>
														<td>
															<a href="viewchart.html?action=refreshchart"><button type="button" style="width: 150px">Refresh chart</button></a>
														</td>
													</tr>
													<tr>
														<td>
															<a href="viewchart.html?action=openwindow"><button type="button" style="width: 150px">Open chart window</button></a>
														</td>
													</tr>
												</table>
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
		</td>
	</tr>
</table>
</body>
</html>