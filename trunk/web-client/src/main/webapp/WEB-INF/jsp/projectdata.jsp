<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="com.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%--@elvariable id="selectedextparamvalsetid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="project_data"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:100%;overflow:auto">
			<table class="maintablewide">
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td colspan="2">
						<table width="100%">
							<tr>
								<td class="spacecolumn"></td>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="project_data_small"/></td>
								<td align="left" width="40">
									<div class="round-button">
										<div class="round-button-circle">
											<a href="" onclick="openInfoWindow()">?</a>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr class="spacerowbig"></tr>
				<tr>
					<td class="spacecolumn"></td>
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
															<td>
																<a href="<c:url value='projectdata.html?selectedcompid=${component.componentid}'/>">
																	<button type="button"><spring:message code="select"/></button>
																</a>
															</td>
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
													<tr>
														<th><spring:message code="output_parameter"/></th>
													</tr>
													
													<c:forEach items="${outputVars}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
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