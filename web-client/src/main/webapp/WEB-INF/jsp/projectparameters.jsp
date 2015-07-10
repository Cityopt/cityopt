<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="com.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="extParamValSet" type="com.cityopt.DTO.ExtParamValSetDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project parameters</title>

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
			<div style="overflow:scroll;height:600px;width:1000px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td colspan="2" height="80">
						<!-- Project parameters -->
						<h2><spring:message code="project_parameters"/></h2>
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<table>
							<tr>
								<td>
									<table width="850">
										<col style="width:200px">
										<col style="width:50px">
										<col style="width:500px">
										<tr>
											<td>
												<!-- Components -->
												<b><spring:message code="components"/></b>
											</td>
											<td></td>
											<td>
												<!-- Input parameters -->
												<b><spring:message code="input_parameters"/></b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:80px">
													<col style="width:180px">
													<col style="width:80px">
													<col style="width:50px">
													<tr>
														<!-- Select -->
														<th><spring:message code="select"/></th>
														<!-- Component -->
														<th><spring:message code="components"/></th>
														<!-- ID -->
														<th><spring:message code="id"/></th>
														<!-- Edit -->
														<th><spring:message code="edit"/></th>
													</tr>
													
													<c:forEach items="${components}" var="component">
														<c:choose>
															<c:when test="${selectedcompid == component.componentid}">
																<tr style="background-color: #D4D4D4">
																	<td><spring:message code="selected"/></td>
															</c:when>
															<c:otherwise>
																<tr>
																	<td><a href="<c:url value='projectparameters.html?selectedcompid=${component.componentid}'/>">
																	<spring:message code="select"/></a></td>
															</c:otherwise>
														</c:choose>
															<td>${component.name}</td>
													    	<td>${component.componentid}</td>
															<td>
																<a href="<c:url value='editcomponent.html?componentid=${component.componentid}'/>">
																	<button align="right" type="button" value="Edit"><spring:message code="edit"/></button>
																</a>
															</td>
													   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:60px">
													<col style="width:150px">
													<col style="width:60px">
													<tr>
														<!-- Input parameter -->
														<th><spring:message code="input_parameter"/></th>
														<!-- ID -->
														<th><spring:message code="id"/></th>
														<!-- Default value -->
														<th><spring:message code="default_value"/></th>
														<!-- Edit -->
														<th><spring:message code="edit"/></th>
													</tr>
													
													<c:forEach items="${inputParameters}" var="inputParam">
													<tr>
														<td>${inputParam.name}</td>
												    	<td>${inputParam.inputid}</td>
												    	<td>${inputParam.defaultvalue}</td>
														<td>
															<a href="<c:url value='editinputparameter.html?inputparameterid=${inputParam.inputid}'/>">
																<button align="right" type="button" value="Edit"><spring:message code="edit"/></button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
													
												</table>
											</td>
										</tr>
										<tr>
											<td align="right">
												<a href="createcomponent.html"><button type="button"><spring:message code="create_component"/></button></a>
											</td>
											<td></td>
											<td align="right">
												<c:if test="${selectedcompid != null}">
													<a href="createinputparameter.html?selectedcompid=${selectedcompid}"><button type="button"><spring:message code="create_input_parameter"/></button></a>
												</c:if>
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
									<b><spring:message code="selected_external_parameter_sets"/>:</b>  
									${extParamValSet.name}
									<br>
									<table class="tablestyle">
										
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td>
												<table class="tablestyle" width="750">
													<col style="width:200px">
													<col style="width:200px">
													<col style="width:100px">
													<col style="width:50px">
													<tr height="20">
														<!-- Name -->
													    <th><spring:message code="name"/></th>
													    <!-- Comment -->
													    <th><spring:message code="comment"/></th>
													    <!-- Value -->
													    <th><spring:message code="value"/></th>
													    <!-- Edit -->
													    <th><spring:message code="edit"/></th>
													</tr>
													
													<c:forEach items="${extParamVals}" var="extParamVal">
													<tr>
														<td>${extParamVal.extparam.name}</td>
														<td>${extParamVal.comment}</td>
												    	<td>${extParamVal.value}</td>
												    	<td>
															<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}'/>">
																<button align="right" type="button" value="Edit"><spring:message code="edit"/></button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td valign="top">
												<a href="createextparam.html"><button type="button" style="width: 150px"><spring:message code="create_external_parameter"/></button></a>
											</td>
										</tr>
										<tr>
											<td width="400" align="right">
												<a href="selectextparamset.html"><button type="button"><spring:message code="select_external_parameter_set"/></button></a>
												<a href="editproject.html"><button type="button"><spring:message code="close"/></button></a>
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