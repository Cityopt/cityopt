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
<title>CityOpt <spring:message code="external_parameters" /></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<script>
    function openInfoWindow() {
    	   window.open("extparams_info.html", '<spring:message code="external_parameters" /> info', 'width=600, height=600');
    }
</script>

</head>
<body>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<%@ include file="mainmenu.inc"%>
			</td>
			<td valign="top">
				<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
				<table class="maintablewide">			
					<%@ include file="toprow.inc"%>
					<tr class="titlerow">
						<td class="spacecolumn"></td>
						<td>
							<table width="100%">
								<tr>
									<td><spring:message code="external_parameters"/></td>
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
					<tr>
						<td class="spacecolumn"></td>
						<td class="error">${error}</td>
					</tr>
					<tr>
						<td class="spacecolumn"></td>
						<td valign="top">
							<table>
								<tr>
									<td valign="top">
										<table>
											<tr>
												<td valign="top">			
													<table>										
														<tr>
															<td>
																<table class="tablestyle">
																	<tr>
																		<td class="regular">
																			<spring:message code="selected_external_parameter_set" />:
																		</td>
																		<td>
																			${extParamValSet.name}
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="active"><spring:message code="external_parameters" /></td>
														</tr>
														<tr class="external_parameter_tables">
															<td valign="top">
																<table width="800" class="external_parameter_tables">
																	<tr>
																		<td>
																			<table class="tablestyle" width="860">
																				<col style="width: 200px">
																				<col style="width: 250px">
																				<col style="width: 100px">
																				<col style="width: 100px">
																				<col style="width: 50px">
																				<col style="width: 80px">
																				<col style="width: 80px">
																				<tr height="20">
																					<!-- Name -->
																					<th><spring:message code="name" /></th>
																					<!-- Comment -->
																					<th><spring:message code="comment" /></th>
																					<!-- Type -->
																					<th><spring:message code="type" /></th>
																					<!-- Value -->
																					<th><spring:message code="value" /></th>
																					<!-- Unit -->
																					<th><spring:message code="unit" /></th>
																					<!-- Edit -->
																					<th><spring:message code="edit" /></th>
																					<th><spring:message code="export" /></th>
																				</tr>
				
																				<c:forEach items="${extParamVals}" var="extParamVal">
																					<tr>
																						<td>${extParamVal.extparam.name}</td>
																						<td>${extParamVal.comment}</td>
																						<td>${extParamVal.extparam.getType().getName()}</td>
																						<td>${extParamVal.value}</td>
																						<td>${extParamVal.extparam.unit.name}</td>
																						<td>
																							<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}'/>">
																								<button align="right" type="button" value="Edit">
																									<spring:message code="edit" />
																								</button>
																							</a>
																						</td>
																						<td>
																							<c:choose>
																								<c:when test="${extParamVal.extparam.getType().getTypeid() >= 5}">
																									<a href="<c:url value='exportextparam.html?extparamvalid=${extParamVal.extparamvalid}&extparamvalsetid=${extParamValSet.extparamvalsetid}'/>">
																										<button align="right" type="button" value="Export">
																											Export
																										</button>
																									</a>
																								</c:when>
																								<c:otherwise>
																								
																								</c:otherwise>
																							</c:choose>
																						</td>
																					</tr>
																				</c:forEach>
																			</table>
																		</td>
																	</tr>
																	<tr>
																		<td align="right">
																			<a href="selectextparamset.html">
																				<button	type="button">
																					<spring:message code="select_external_parameter_set" />
																				</button>
																			</a>
																		</td>
																	</tr>
																	<tr height="20"></tr>
																	<tr>
																		<td><b><spring:message code="project_external_parameters"/></b></td>
																	</tr>
																	<tr>
																		<td>
																			<table class="tablestyle" width="860">
																				<col style="width: 500px">
																				<col style="width: 230px">
																				<col style="width: 50px">
																				<col style="width: 80px">
																				<tr height="20">
																					<!-- Name -->
																					<th><spring:message code="name" /></th>
																					<!-- Type -->
																					<th><spring:message code="type" /></th>
																					<!-- Unit -->
																					<th><spring:message code="unit" /></th>
																					<!-- Remove -->
																					<th><spring:message code="remove" /></th>
																				</tr>
				
																				<c:forEach items="${extParams}" var="extParam">
																					<tr>
																						<td>${extParam.name}</td>
																						<td>${extParam.type.name}</td>
																						<td>${extParam.unit.name}</td>
				
																						<td><a onclick="return confirm('Are you sure you want to delete')"
																								href="<c:url value='deleteextparam.html?extparamid=${extParam.extparamid}'/>">
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
																		<td width="750" align="right">
																			<a href="createextparam.html"><button type="button"	style="width: 150px">
																					<spring:message code="create_external_parameter" />
																				</button>
																			</a>
																		</td>
																	</tr>
																	<tr height="20"></tr>
																	<tr>
																		<td>
																			<table style="width: 100%">
																				<tr>
																					<td>
																						<!-- Import external parameter -->					
																						<b><spring:message code="import_external_parameter"/></b>
																					</td>
																					<td></td>
																					<td></td>
																				</tr>
																				<form:form method="POST" action="importextparam.html" enctype="multipart/form-data">
														        					<tr>
														        						<td><spring:message code="import_external_time_series" /> (CSV)</td>
																						<td><input id="file" name="file" type="file"/></td>
																						<td align="right"><input type="submit" value="<spring:message code="import_file"/>"></td>	
																					</tr>
													    						</form:form>
													    						<tr height="20"></tr>
													    						<tr>
													    							<td></td>
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
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>