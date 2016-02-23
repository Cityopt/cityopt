<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="com.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="selectedextparamvalsetid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project data</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:1000px;width:1100px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td colspan="2" height="80">
						<!-- Project data -->
						<h2><spring:message code="project_data"/></h2>
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<table>
							<tr>
								<td>
									<table class="tablegroup" width="850">
										<col style="width:250px">
										<col style="width:50px">
										<col style="width:250px">
										<col style="width:50px">
										<col style="width:250px">
										<tr>
											<td>
												<!-- Components -->
												<b><spring:message code="components"/></b>
											</td>
											<td></td>
											<td>
												<!-- Input parameters-->
												<b><spring:message code="input_parameters"/></b>
											</td>
											<td></td>
											<td>
												<!-- Output parameters -->
												<b><spring:message code="output_parameters"/></b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:80px">
													<col style="width:180px">
													<tr>
														<!-- Select -->
														<th><spring:message code="select"/></th>
														<!-- Components -->
														<th><spring:message code="components"/></th>
													</tr>
													
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: #D4D4D4"><td>
														<spring:message code="selected"/></td>
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
															<td><a href="<c:url value='projectdata.html?selectedcompid=${component.componentid}'/>">
															<spring:message code="select"/></a></td>
													</c:if>
														<td>${component.name}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:150px">
													<tr>
														<th><spring:message code="input_parameter"/></th>
														<th><spring:message code="default_value"/></th>
													</tr>
													
													<c:forEach items="${inputParameters}" var="inputParam">
													<tr>
														<td>${inputParam.name}</td>
												    	<td>${inputParam.defaultvalue}</td>
												   	</tr>
													</c:forEach>
													
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:60px">
													<tr>
														<th><spring:message code="output_parameter"/></th>
														<th><spring:message code="id"/></th>
													</tr>
													
													<c:forEach items="${outputVars}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
												    	<td>${outputVar.outvarid}</td>
												   	</tr>
													</c:forEach>
													
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
							<tr>
								<td>
									<table width="100%">
										<tr>
											<!-- External parameter sets -->
											<td><spring:message code="external_parameter_sets"/></td>
											<!-- External parameters -->
											<td><spring:message code="external_parameters"/></td>
										</tr>
										<tr>
											<td>
												<table>
													<tr>
														<td>
															<table class="tablestyle" width="300px">
																<col style="width:100px">
																<col style="width:200px">
																<tr height="20">
																	<!-- Select -->
																    <th><spring:message code="select"/></th>
																    <!-- Name -->
																    <th><spring:message code="name"/></th>
																</tr>
																
																<c:forEach items="${extParamValSets}" var="extParamValSet">
																<c:choose>
																	<c:when test="${selectedextparamvalsetid == extParamValSet.extparamvalsetid}">
																		<tr style="background-color: #D4D4D4"><td>
																		<spring:message code="selected"/></td>
																	</c:when>
																	<c:otherwise>
																		<tr>
																	</c:otherwise>
																</c:choose>
																	<td>
																		<a href="<c:url value='projectdata.html?selectedextparamvalsetid=${extParamValSet.extparamvalsetid}'/>">
																			<button align="right" type="button" value="Select">
																			<spring:message code="select"/></button>
																		</a>
																	</td>
																	<td>${extParamValSet.name}</td>
															   	</tr>
																</c:forEach>
															</table>
														</td>
													</tr>
												</table>
											</td>
											<td>
												<table>
													<tr>
														<td>
															<table class="tablestyle" width="350">
																<col style="width:250px">
																<col style="width:100px">
																<tr height="20">
																	<!-- Name -->
																    <th><spring:message code="name"/></th>
																    <!-- Value -->
																    <th><spring:message code="value"/></th>
																</tr>
																
																<c:forEach items="${extParamVals}" var="extParamVal">
																<tr>
																	<td>${extParamVal.extparam.name}</td>
															    	<td>${extParamVal.value}</td>
															   	</tr>
																</c:forEach>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td></td>
											<td width="400" align="right">
												<a href="importdata.html"><button type="button">
												<spring:message code="back"/></button></a>
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