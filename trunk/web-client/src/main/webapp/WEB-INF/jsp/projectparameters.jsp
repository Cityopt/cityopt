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
<title>CityOpt <spring:message code="project_parameters" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<script>
    function openInfoWindow() {
    	   window.open("projectparameters_info.html",'<spring:message code="project_parameters" /> info','width=600,height=600');
    }
</script>

<style type="text/css">

table.external_parameter_sets{
margin-top: 20%;
padding-top: 20%;
}

table.extern{
margin-top: 5%;
padding-top: 5%;
padding-right: 5%;
}

table.close_button{
right: 0px;
width:100%;
}

.big{
float:right;
margin-right: 2%;
margin-top: 5%;
margin-bottom: 10%;
}	

</style>

</head>
<body>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<%@ include file="mainmenu.inc"%>
			</td>
			<td width="5%"></td>
			<td valign="top">
				<div style="overflow: auto; height: 100%; width: 100%; overflow: auto;">
					<table>
						<col style="width: 5%">
						<col style="width: 90%">
						<col style="width: 5%">
						<tr>
							<td colspan="2"><h2 class="error">${error}</h2></td>
						</tr>
						<tr class="project_parameters">
							<td colspan="2" height="80">
								<h1>
									<spring:message code="project_parameters" />
								</h1>
							</td>
							<td>
								<div class="round-button">
									<div class="round-button-circle" onclick="openInfoWindow()">
										<a>?</a>
									</div> 
								</div>
							</td>
						</tr>
						<tr class="content_tables">
							<td></td>
							<td class="external_parameters">
								<table class="tablegroup">
									<tr>
										<td>
											<table width="100%" class="input_parameter_table">
												<col style="width: 200px">
												<col style="width: 50px">
												<col style="width: 400px">
												<tr>
													<td>
														<!-- Components --> <b><spring:message
																code="components" /></b>
													</td>
													<td></td>
													<td>
														<!-- Input parameters --> <b><spring:message
																code="input_parameters" /></b>
													</td>
												</tr>
												<tr>
													<td valign="top">
														<table class="tablestyle">
															<col style="width: 80px">
															<col style="width: 180px">
															<col style="width: 50px">
															<tr>
																<!-- Select -->
																<th><spring:message code="select" /></th>
																<!-- Component -->
																<th><spring:message code="components" /></th>
																<!-- Edit -->
																<th><spring:message code="edit" /></th>
															</tr>
															<!-- Tooltips -->
															<c:set var="tooltip_select">
																<spring:message code="tooltip_select" />
															</c:set>
															<c:set var="tooltip_selected">
																<spring:message code="tooltip_selected" />
															</c:set>
															<c:set var="tooltip_projectparameters">
																<spring:message code="tooltip_projectparameters" />
															</c:set>

															<c:forEach items="${components}" var="component">
																<c:choose>
																	<c:when
																		test="${selectedcompid == component.componentid}">
																		<tr style="background-color: #D4D4D4"
																			title="${tooltip_selected}">
																			<td><spring:message code="selected" /></td>
																	</c:when>
																	<c:otherwise>
																		<tr>
																			<td><a href="<c:url value='projectparameters.html?selectedcompid=${component.componentid}'/>"
																				title="${tooltip_select}"> <spring:message
																						code="select" /></a></td>
																	</c:otherwise>
																</c:choose>
																<td>${component.name}</td>
																<td><a href="<c:url value='editcomponent.html?componentid=${component.componentid}'/>">
																		<button align="right"
																			title="${tooltip_projectparameters}" type="button"
																			value="Edit">
																			<spring:message code="edit" />
																		</button>
																</a></td>
																</tr>
															</c:forEach>
														</table>
													</td>
													<td></td>
													<td valign="top">
														<table class="tablestyle">
															<col style="width: 150px">
															<col style="width: 150px">
															<col style="width: 60px">
															<col style="width: 60px">
															<col style="width: 60px">
															<tr>
																<!-- Input parameter -->
																<th><spring:message code="input_parameter" /></th>
																<!-- Default value -->
																<th><spring:message code="default_value" /></th>
																<th><spring:message code="type" /></th>
																<!-- Units -->
																<th><spring:message code="units" /></th>
																<!-- Edit -->
																<th><spring:message code="edit" /></th>
																<!-- Delete -->
																<th><spring:message code="remove" /></th>
															</tr>

															<c:forEach items="${inputParameters}" var="inputParam">
																<tr>
																	<td>${inputParam.name}</td>
																	<td>${inputParam.defaultvalue}</td>
																	<c:choose>
																		<c:when test="${inputParam.getType().getTypeid() >= 5}">
																			<td>Time series</td>																		
																		</c:when>
																		<c:otherwise>
																			<td><spring:message code="value"></spring:message></td>
																		</c:otherwise>
																	</c:choose>
																	<td>${inputParam.unit.name}</td>
																	<td><a href="<c:url value='editinputparameter.html?inputparamid=${inputParam.inputid}'/>">
																			<button align="right" type="button" value="Edit">
																				<spring:message code="edit" />
																			</button>
																	</a></td>
																	<td><a  onclick="return confirm('Are you sure you want to delete')" href="<c:url value='deleteinputparameter.html?inputparamid=${inputParam.inputid}'/>">
																			<button align="right" type="button" value="Remove">
																				<spring:message code="remove" />
																			</button>
																	</a></td>
																</tr>
															</c:forEach>

														</table>
													</td>
												</tr>
												<tr>
													<c:set var="tooltip_createcomponent">
														<spring:message code="tooltip_create_component" />
													</c:set>
													<td align="right"><a href="createcomponent.html"><button
																title="${tooltip_createcomponent}" type="button">
																<spring:message code="create_component" />
															</button></a></td>
													<td></td>
													<td align="right"><c:if
															test="${selectedcompid != null}">
															<a
																href="createinputparameter.html?selectedcompid=${selectedcompid}"><button
																	type="button">
																	<spring:message code="create_input_parameter" />
																</button></a>
														</c:if>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
   							<td></td>
   							<td align="right">
   								<table class="close_button">
									<tr>														
										<td align="right">
											<a href='editproject.html?prjid=${project.prjid}'>
												<button type="button">
													<spring:message code="back" />
												</button>
											</a>
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